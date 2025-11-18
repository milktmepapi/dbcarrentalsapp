package com.example.dbcarrentalsapp;

import model.ViolationRecord;
import model.RentalDetails;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for violation-related database operations
 * Handles CRUD operations, automatic violation detection, and penalty calculations
 */
public class ViolationDAO {

    // Penalty rates configuration
    private static final double RATE_FIRST_6_HOURS = 50.0;
    private static final double RATE_AFTER_6_HOURS = 100.0;
    private static final double DAMAGE_PENALTY_BASE = 200.0;
    private static final double CLEANING_FEE = 75.0;
    private static final double TRAFFIC_VIOLATION_FEE = 150.0;

    /**
     * Checks if rental was returned late by comparing actual vs expected return datetime
     */
    public boolean isLateReturn(String rentalId) throws SQLException {
        String sql = "SELECT rental_expected_return_datetime, rental_actual_return_datetime FROM rental_details WHERE rental_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rentalId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp expected = rs.getTimestamp("rental_expected_return_datetime");
                Timestamp actual = rs.getTimestamp("rental_actual_return_datetime");

                // Check if actual return exists and is after expected return
                return actual != null && expected != null && actual.after(expected);
            }
        }
        return false;
    }

    /**
     * Calculates number of hours a rental was returned late
     */
    public int calculateLateHours(String rentalId) throws SQLException {
        String sql = "SELECT rental_expected_return_datetime, rental_actual_return_datetime FROM rental_details WHERE rental_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rentalId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp expected = rs.getTimestamp("rental_expected_return_datetime");
                Timestamp actual = rs.getTimestamp("rental_actual_return_datetime");

                if (actual != null && expected != null && actual.after(expected)) {
                    long diffMillis = actual.getTime() - expected.getTime();
                    return (int) Math.ceil(diffMillis / (1000.0 * 60 * 60)); // Convert to hours, round up
                }
            }
        }
        return 0;
    }

    /**
     * Calculates late return penalty based on rental duration
     * Uses tiered pricing: $50/hour first 6 hours, $100/hour thereafter
     */
    public double calculateLatePenalty(String rentalId) throws SQLException {
        int lateHours = calculateLateHours(rentalId);

        if (lateHours <= 0) {
            return 0.0;
        }

        double penalty = 0.0;

        if (lateHours <= 6) {
            penalty = lateHours * RATE_FIRST_6_HOURS;
        } else {
            penalty = (6 * RATE_FIRST_6_HOURS) + ((lateHours - 6) * RATE_AFTER_6_HOURS);
        }

        return penalty;
    }

    /**
     * Automatically creates late return violation record if rental is late
     * Updates existing late violation if one already exists for this rental
     */
    public ViolationRecord createAutomaticLateViolation(String rentalId, String staffId) throws SQLException {
        if (!isLateReturn(rentalId)) {
            return null;
        }

        int lateHours = calculateLateHours(rentalId);
        double penalty = calculateLatePenalty(rentalId);

        if (lateHours <= 0 || penalty <= 0) {
            return null;
        }

        // Check if late violation already exists for this rental
        String checkSql = "SELECT * FROM violation_details WHERE violation_rental_id = ? AND violation_type = 'Late Return'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, rentalId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Late violation already exists, update it
                String updateSql = "UPDATE violation_details SET violation_penalty_fee = ?, violation_duration_hours = ?, violation_timestamp = ? WHERE violation_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setDouble(1, penalty);
                    updateStmt.setInt(2, lateHours);
                    updateStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    updateStmt.setString(4, rs.getString("violation_id"));
                    updateStmt.executeUpdate();
                }
                return getViolationById(rs.getString("violation_id"));
            }
        }

        // Create new late violation
        String violationId = generateNextViolationId();
        String expectedTime = getExpectedReturnTime(rentalId);
        String reason = String.format("Late return: %d hours overdue. Expected return: %s",
                lateHours, expectedTime);

        ViolationRecord violation = new ViolationRecord(
                violationId,
                rentalId,
                staffId,
                "Late Return",
                penalty,
                reason,
                lateHours,
                LocalDateTime.now()
        );

        addViolation(violation);
        return violation;
    }

    /**
     * Updates car status in the database
     */
    public void updateCarStatus(String carPlateNumber, String status) throws SQLException {
        String sql = "UPDATE car_record SET car_status = ? WHERE car_plate_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, carPlateNumber);
            stmt.executeUpdate();
        }
    }

    /**
     * Processes car return and updates relevant statuses
     * Validates staff permissions, updates car status, and creates violations
     */
    public ViolationRecord processCarReturn(String rentalId, String staffId) throws SQLException {
        // 1. Validate staff can process this return (same branch and operations department)
        if (!validateStaffForViolation(staffId, rentalId)) {
            throw new SQLException("Staff " + staffId + " cannot process return for rental " + rentalId +
                    ". Staff must be from Operations department and same branch as rental.");
        }

        // 2. Get car plate number from rental
        String carSql = "SELECT rental_car_plate_number, rental_branch_id FROM rental_details WHERE rental_id = ?";
        String carPlateNumber = null;
        String rentalBranchId = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(carSql)) {

            stmt.setString(1, rentalId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                carPlateNumber = rs.getString("rental_car_plate_number");
                rentalBranchId = rs.getString("rental_branch_id");
            }
        }

        if (carPlateNumber == null) {
            throw new SQLException("Car not found for rental: " + rentalId);
        }

        // 3. Update car status to Available
        updateCarStatus(carPlateNumber, "Available");

        // 4. Update rental status to Completed with current timestamp
        updateRentalStatus(rentalId, "COMPLETED", staffId);

        // 5. Check for and create late return violation
        ViolationRecord lateViolation = createAutomaticLateViolation(rentalId, staffId);

        // Return the late violation if created, otherwise return null
        // The controller will handle displaying ALL violations
        return lateViolation;
    }

    /**
     * Gets all violations for a specific rental ID
     */
    public List<ViolationRecord> getViolationsByRentalId(String rentalId) throws SQLException {
        List<ViolationRecord> violations = new ArrayList<>();
        String sql = "SELECT * FROM violation_details WHERE violation_rental_id = ? ORDER BY violation_timestamp";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rentalId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                violations.add(mapResultSetToViolationRecord(rs));
            }
        }
        return violations;
    }

    /**
     * Updates rental status and sets return staff and actual return datetime
     */
    private void updateRentalStatus(String rentalId, String status, String staffId) throws SQLException {
        String sql = "UPDATE rental_details SET rental_status = ?, rental_staff_id_return = ?, rental_actual_return_datetime = NOW() WHERE rental_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, staffId);
            stmt.setString(3, rentalId);
            stmt.executeUpdate();
        }
    }

    /**
     * Generates detailed receipt for rental including all violations and penalties
     */
    public String generateRentalReceipt(String rentalId) throws SQLException {
        StringBuilder receipt = new StringBuilder();

        // Get rental details
        String rentalSql = """
                SELECT rd.*, cr.car_model, cr.car_rental_fee, cr.car_plate_number,
                sr.staff_first_name as pickup_staff_first_name, sr.staff_last_name as pickup_staff_last_name,
                sr2.staff_first_name as return_staff_first_name, sr2.staff_last_name as return_staff_last_name,
                rr.renter_first_name, rr.renter_last_name, rr.renter_phone_number
                FROM rental_details rd
                JOIN car_record cr ON rd.rental_car_plate_number = cr.car_plate_number
                LEFT JOIN staff_record sr ON rd.rental_staff_id_pickup = sr.staff_id
                LEFT JOIN staff_record sr2 ON rd.rental_staff_id_return = sr2.staff_id
                JOIN renter_record rr ON rd.rental_renter_dl_number = rr.renter_dl_number
                WHERE rd.rental_id = ?
            """;

        // Get violation details
        String violationSql = """
            SELECT * FROM violation_details 
            WHERE violation_rental_id = ? 
            ORDER BY violation_timestamp
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement rentalStmt = conn.prepareStatement(rentalSql);
             PreparedStatement violationStmt = conn.prepareStatement(violationSql)) {

            // Rental details
            rentalStmt.setString(1, rentalId);
            ResultSet rentalRs = rentalStmt.executeQuery();

            if (rentalRs.next()) {
                receipt.append("=== CAR RENTAL RECEIPT ===\n\n");
                receipt.append(String.format("Rental ID: %s\n", rentalRs.getString("rental_id")));

                // Combine first and last name for customer name
                String firstName = rentalRs.getString("renter_first_name");
                String lastName = rentalRs.getString("renter_last_name");
                receipt.append(String.format("Customer: %s %s\n", firstName, lastName));
                receipt.append(String.format("Contact: %s\n", rentalRs.getString("renter_phone_number")));

                receipt.append(String.format("Car: %s (%s)\n", rentalRs.getString("car_model"), rentalRs.getString("car_plate_number")));
                receipt.append(String.format("Daily Rate: $%.2f\n", rentalRs.getDouble("car_rental_fee")));
                receipt.append(String.format("Rental Date: %s\n", formatTimestamp(rentalRs.getTimestamp("rental_datetime"))));
                receipt.append(String.format("Pickup Date: %s\n", formatTimestamp(rentalRs.getTimestamp("rental_actual_pickup_datetime"))));
                receipt.append(String.format("Expected Return: %s\n", formatTimestamp(rentalRs.getTimestamp("rental_expected_return_datetime"))));
                receipt.append(String.format("Actual Return: %s\n", formatTimestamp(rentalRs.getTimestamp("rental_actual_return_datetime"))));
                receipt.append(String.format("Pickup Staff: %s %s\n",
                        rentalRs.getString("pickup_staff_first_name"),
                        rentalRs.getString("pickup_staff_last_name")));
                receipt.append(String.format("Return Staff: %s %s\n",
                        rentalRs.getString("return_staff_first_name"),
                        rentalRs.getString("return_staff_last_name")));
                receipt.append(String.format("Status: %s\n", rentalRs.getString("rental_status")));

                double totalPayment = rentalRs.getDouble("rental_total_payment");
                receipt.append(String.format("\nBase Rental Payment: $%.2f\n", totalPayment));
            }

            // Violation details
            violationStmt.setString(1, rentalId);
            ResultSet violationRs = violationStmt.executeQuery();

            double totalViolationFees = 0.0;
            boolean hasViolations = false;

            while (violationRs.next()) {
                if (!hasViolations) {
                    receipt.append("\n--- VIOLATIONS AND PENALTIES ---\n");
                    hasViolations = true;
                }

                String violationType = violationRs.getString("violation_type");
                double penaltyFee = violationRs.getDouble("violation_penalty_fee");
                String reason = violationRs.getString("violation_reason");
                int duration = violationRs.getInt("violation_duration_hours");
                Timestamp violationTime = violationRs.getTimestamp("violation_timestamp");

                receipt.append(String.format("\n%s:\n", violationType));
                receipt.append(String.format("  Recorded: %s\n", formatTimestamp(violationTime)));
                receipt.append(String.format("  Reason: %s\n", reason));
                if (duration > 0) {
                    receipt.append(String.format("  Duration: %d hours\n", duration));
                }
                receipt.append(String.format("  Penalty: $%.2f\n", penaltyFee));

                totalViolationFees += penaltyFee;
            }

            // Total calculation
            double finalTotal = rentalRs.getDouble("rental_total_payment") + totalViolationFees;

            receipt.append("\n--- PAYMENT SUMMARY ---\n");
            receipt.append(String.format("Rental Amount: $%.2f\n", rentalRs.getDouble("rental_total_payment")));
            receipt.append(String.format("Violation Fees: $%.2f\n", totalViolationFees));
            receipt.append(String.format("TOTAL AMOUNT: $%.2f\n", finalTotal));
            receipt.append("\nThank you for your business!\n");
            receipt.append("=== END OF RECEIPT ===\n");
        }

        return receipt.toString();
    }

    /**
     * Generates late return receipt specifically for overdue rentals
     */
    public String generateLateReturnReceipt(String rentalId) throws SQLException {
        if (!isLateReturn(rentalId)) {
            return "No late return detected for rental: " + rentalId;
        }

        int lateHours = calculateLateHours(rentalId);
        double penalty = calculateLatePenalty(rentalId);
        String expectedTime = getExpectedReturnTime(rentalId);

        StringBuilder receipt = new StringBuilder();
        receipt.append("=== LATE RETURN NOTICE ===\n\n");
        receipt.append(String.format("Rental ID: %s\n", rentalId));
        receipt.append(String.format("Expected Return: %s\n", expectedTime));
        receipt.append(String.format("Late Return Detected: %d hours overdue\n", lateHours));
        receipt.append(String.format("Penalty Calculation:\n"));
        if (lateHours <= 6) {
            receipt.append(String.format("  %d hours × $%.2f/hour = $%.2f\n", lateHours, RATE_FIRST_6_HOURS, penalty));
        } else {
            receipt.append(String.format("  First 6 hours: 6 × $%.2f = $%.2f\n", RATE_FIRST_6_HOURS, 6 * RATE_FIRST_6_HOURS));
            receipt.append(String.format("  Additional %d hours: %d × $%.2f = $%.2f\n",
                    lateHours - 6, lateHours - 6, RATE_AFTER_6_HOURS, (lateHours - 6) * RATE_AFTER_6_HOURS));
        }
        receipt.append(String.format("Total Penalty Fee: $%.2f\n", penalty));
        receipt.append("\nPlease pay the penalty fee at the rental counter.\n");
        receipt.append("=== END OF NOTICE ===\n");

        return receipt.toString();
    }

    /**
     * Formats timestamp for display
     */
    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return timestamp.toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    /**
     * Gets expected return time as formatted string
     */
    private String getExpectedReturnTime(String rentalId) throws SQLException {
        String sql = "SELECT rental_expected_return_datetime FROM rental_details WHERE rental_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rentalId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp expected = rs.getTimestamp("rental_expected_return_datetime");
                return expected != null ? formatTimestamp(expected) : "Unknown";
            }
        }
        return "Unknown";
    }

    /**
     * Gets active rentals that can be processed for return
     */
    public List<String> getActiveRentalsForReturn() throws SQLException {
        List<String> activeRentals = new ArrayList<>();
        String sql = "SELECT rental_id FROM rental_details WHERE rental_status IN ('ACTIVE', 'UPCOMING') ORDER BY rental_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                activeRentals.add(rs.getString("rental_id"));
            }
        }
        return activeRentals;
    }

    /**
     * Checks for overdue rentals that haven't been returned yet
     */
    public List<String> getOverdueRentals() throws SQLException {
        List<String> overdueRentals = new ArrayList<>();
        String sql = """
            SELECT rental_id FROM rental_details 
            WHERE rental_status = 'ACTIVE' 
            AND rental_expected_return_datetime < NOW() 
            AND rental_actual_return_datetime IS NULL 
            ORDER BY rental_expected_return_datetime
            """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                overdueRentals.add(rs.getString("rental_id"));
            }
        }
        return overdueRentals;
    }

    /**
     * Generates next sequential violation ID
     */
    public String generateNextViolationId() throws SQLException {
        String sql = "SELECT violation_id FROM violation_details WHERE violation_id LIKE 'VLN%' ORDER BY violation_id DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastId = rs.getString("violation_id");
                int num = Integer.parseInt(lastId.substring(3)) + 1;
                return String.format("VLN%03d", num);
            }
        }
        return "VLN001";
    }

    /**
     * Adds new violation record to database
     */
    public void addViolation(ViolationRecord violation) throws SQLException {
        String sql = """
        INSERT INTO violation_details (
            violation_id, 
            violation_rental_id, 
            violation_staff_id,
            violation_type, 
            violation_penalty_fee, 
            violation_reason,
            violation_duration_hours,
            violation_timestamp
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, violation.getViolationId());
            stmt.setString(2, violation.getRentalId());
            stmt.setString(3, violation.getStaffId());
            stmt.setString(4, violation.getViolationType());
            stmt.setDouble(5, violation.getPenaltyFee());
            stmt.setString(6, violation.getReason());
            stmt.setInt(7, violation.getDurationHours());
            stmt.setTimestamp(8, Timestamp.valueOf(violation.getTimestamp()));

            stmt.executeUpdate();
        }
    }

    /**
     * Updates existing violation record in database
     */
    public void updateViolation(ViolationRecord violation) throws SQLException {
        String sql = """
            UPDATE violation_details 
            SET violation_rental_id = ?, violation_staff_id = ?, violation_type = ?,
                violation_penalty_fee = ?, violation_reason = ?, violation_duration_hours = ?,
                violation_timestamp = ?
            WHERE violation_id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, violation.getRentalId());
            stmt.setString(2, violation.getStaffId());
            stmt.setString(3, violation.getViolationType());
            stmt.setDouble(4, violation.getPenaltyFee());
            stmt.setString(5, violation.getReason());
            stmt.setInt(6, violation.getDurationHours());
            stmt.setTimestamp(7, Timestamp.valueOf(violation.getTimestamp()));
            stmt.setString(8, violation.getViolationId());

            stmt.executeUpdate();
        }
    }

    /**
     * Gets all violations from database
     */
    public List<ViolationRecord> getAllViolations() throws SQLException {
        List<ViolationRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM violation_details ORDER BY violation_timestamp DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToViolationRecord(rs));
            }
        }
        return list;
    }

    /**
     * Gets violation by ID
     */
    public ViolationRecord getViolationById(String id) throws SQLException {
        String sql = "SELECT * FROM violation_details WHERE violation_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToViolationRecord(rs);
            }
        }
        return null;
    }

    /**
     * Deletes violation from database
     */
    public boolean deleteViolation(String violationId) throws SQLException {
        String sql = "DELETE FROM violation_details WHERE violation_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, violationId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Gets all rental IDs from database
     */
    public List<String> getAllRentalIds() throws SQLException {
        List<String> rentalIds = new ArrayList<>();
        String sql = "SELECT rental_id FROM rental_details ORDER BY rental_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rentalIds.add(rs.getString("rental_id"));
            }
        }
        return rentalIds;
    }

    /**
     * Gets all staff IDs from database
     */
    public List<String> getAllStaffIds() throws SQLException {
        List<String> staffIds = new ArrayList<>();
        String sql = "SELECT staff_id FROM staff_record ORDER BY staff_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                staffIds.add(rs.getString("staff_id"));
            }
        }
        return staffIds;
    }

    /**
     * Validates if staff can process violation for rental
     * Staff must be from Operations department and same branch as rental
     */
    public boolean validateStaffForViolation(String staffId, String rentalId) throws SQLException {
        String sql = """
        SELECT sr.staff_id, sr.staff_branch_id, jr.job_department_id
        FROM staff_record sr
        JOIN job_record jr ON sr.staff_job_id = jr.job_id
        JOIN rental_details rd ON rd.rental_id = ? AND rd.rental_branch_id = sr.staff_branch_id
        WHERE sr.staff_id = ? AND jr.job_department_id = 'DEPT_OPS'
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rentalId);  // First parameter: rental_id in JOIN
            stmt.setString(2, staffId);   // Second parameter: staff_id in WHERE
            ResultSet rs = stmt.executeQuery();

            return rs.next();
        }
    }

    /**
     * Gets branch ID for specified rental
     */
    public String getRentalBranchId(String rentalId) throws SQLException {
        String sql = "SELECT rental_branch_id FROM rental_details WHERE rental_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rentalId);
            ResultSet rs = stmt.executeQuery();

            return rs.next() ? rs.getString("rental_branch_id") : null;
        }
    }

    /**
     * Gets Operations staff IDs for specified branch
     */
    public List<String> getOperationsStaffByBranch(String branchId) throws SQLException {
        List<String> staffIds = new ArrayList<>();
        String sql = """
            SELECT sr.staff_id 
            FROM staff_record sr
            JOIN job_record jr ON sr.staff_job_id = jr.job_id
            WHERE sr.staff_branch_id = ? AND jr.job_department_id = 'DEPT_OPS'
            ORDER BY sr.staff_id
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                staffIds.add(rs.getString("staff_id"));
            }
        }
        return staffIds;
    }

    /**
     * Maps database ResultSet to ViolationRecord object
     */
    private ViolationRecord mapResultSetToViolationRecord(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("violation_timestamp");

        return new ViolationRecord(
                rs.getString("violation_id"),
                rs.getString("violation_rental_id"),
                rs.getString("violation_staff_id"),
                rs.getString("violation_type"),
                rs.getDouble("violation_penalty_fee"),
                rs.getString("violation_reason"),
                rs.getInt("violation_duration_hours"),
                timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now()
        );
    }

    /**
     * Calculates how many hours late a rental would be if returned now
     */
    public int calculateLateHoursIfReturnedNow(String rentalId) throws SQLException {
        String sql = "SELECT rental_expected_return_datetime FROM rental_details WHERE rental_id = ? AND rental_status IN ('ACTIVE', 'UPCOMING')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rentalId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp expected = rs.getTimestamp("rental_expected_return_datetime");

                if (expected != null && expected.before(new Timestamp(System.currentTimeMillis()))) {
                    long diffMillis = System.currentTimeMillis() - expected.getTime();
                    return (int) Math.ceil(diffMillis / (1000.0 * 60 * 60)); // Convert to hours, round up
                }
            }
        }
        return 0;
    }

    /**
     * Calculates late penalty from hours (for preview purposes)
     */
    public double calculateLatePenaltyFromHours(int lateHours) {
        if (lateHours <= 0) {
            return 0.0;
        }

        double penalty = 0.0;

        if (lateHours <= 6) {
            penalty = lateHours * RATE_FIRST_6_HOURS;
        } else {
            penalty = (6 * RATE_FIRST_6_HOURS) + ((lateHours - 6) * RATE_AFTER_6_HOURS);
        }

        return penalty;
    }
}