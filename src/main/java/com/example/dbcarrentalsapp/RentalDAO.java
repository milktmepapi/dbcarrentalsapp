package com.example.dbcarrentalsapp;

import model.RentalRecord;
import model.RentalRecord.RentalStatus;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

    private static final int PICKUP_GRACE_MINUTES = 5;

    /**
     * Check if a car is valid to rent:
     * - Must belong to the same branch
     * - Must NOT be Under Maintenance
     * - Must NOT be currently Rented
     * - Must NOT have an UPCOMING rental overlapping
     */
    private void validateCarAvailability(Connection conn, String carPlate, String branchId,
                                         LocalDateTime expectedPickup, LocalDateTime expectedReturn)
            throws SQLException {

        String error = null;

        String carSql = """
        SELECT car_status, car_branch_id
        FROM car_record
        WHERE car_plate_number = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(carSql)) {
            ps.setString(1, carPlate);
            ResultSet rs = ps.executeQuery();

            if (!rs.next())
                error = "Car does not exist.";
            else {
                String status = rs.getString("car_status");
                String carBranch = rs.getString("car_branch_id");

                if (!carBranch.equals(branchId))
                    error = "Car does not belong to the selected branch.";
                else if (status.equals("Under Maintenance"))
                    error = "Car is under maintenance.";
                else if (status.equals("Rented"))
                    error = "Car is currently rented.";
            }
        }

        if (error != null) throw new SQLException(error);

        // --- second query ---
        String overlapSql = """
        SELECT 1 FROM rental_details
        WHERE rental_car_plate_number = ?
          AND rental_status = 'UPCOMING'
          AND (
               (rental_expected_pickup_datetime <= ? AND rental_expected_return_datetime >= ?)
            )
        LIMIT 1
        """;

        error = null;

        try (PreparedStatement ps = conn.prepareStatement(overlapSql)) {
            ps.setString(1, carPlate);
            ps.setTimestamp(2, Timestamp.valueOf(expectedReturn));
            ps.setTimestamp(3, Timestamp.valueOf(expectedPickup));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) error = "Car has an upcoming reservation in that timeslot.";
        }

        if (error != null) throw new SQLException(error);
    }

    private void validateRentalTimes(LocalDateTime pickup, LocalDateTime expectedReturn)
            throws SQLException {

        if (pickup.toLocalDate().isBefore(LocalDate.now()))
            throw new SQLException("Pickup date cannot be in the past.");

        if (!expectedReturn.isAfter(pickup))
            throw new SQLException("Return time must be after pickup time.");
    }

    /**
     * Generate next rental id in format RNT### (always).
     */
    public String generateNextRentalId(Connection conn) throws SQLException {
        String sql = "SELECT rental_id FROM rental_details WHERE rental_id LIKE 'RNT%' ORDER BY rental_id DESC LIMIT 1";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1);
                int num = Integer.parseInt(lastId.substring(3)) + 1;
                return String.format("RNT%03d", num);
            }
        }
        return "RNT001";
    }

    public String generateNextRentalId() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return generateNextRentalId(conn); // call the connection-required version
        }
    }

    /**
     * Add rental. Always generates a new rental id regardless of incoming object's id.
     * This method uses a transaction and rolls back on failure.
     */
    public void addRental(RentalRecord rental) throws SQLException {

        // Validate times BEFORE starting transaction
        validateRentalTimes(
                rental.getExpectedPickupDateTime(),
                rental.getExpectedReturnDateTime()
        );

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {
                // 1. Generate ID USING SAME CONNECTION
                String newRentalId = generateNextRentalId(conn);
                rental.setRentalId(newRentalId);

                // 2. Validate car availability BEFORE insert
                validateCarAvailability(
                        conn,
                        rental.getCarPlateNumber(),
                        rental.getBranchId(),
                        rental.getExpectedPickupDateTime(),
                        rental.getExpectedReturnDateTime()
                );

                // 3. Perform insert using same connection
                String sql = """
                INSERT INTO rental_details (
                    rental_id,
                    rental_renter_dl_number,
                    rental_car_plate_number,
                    rental_branch_id,
                    rental_staff_id_pickup,
                    rental_staff_id_return,
                    rental_datetime,
                    rental_expected_pickup_datetime,
                    rental_actual_pickup_datetime,
                    rental_expected_return_datetime,
                    rental_actual_return_datetime,
                    rental_total_payment,
                    rental_status
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setString(1, rental.getRentalId());
                    stmt.setString(2, rental.getRenterDlNumber());
                    stmt.setString(3, rental.getCarPlateNumber());
                    stmt.setString(4, rental.getBranchId());

                    stmt.setNull(5, Types.VARCHAR); // staff_id_pickup
                    stmt.setNull(6, Types.VARCHAR); // staff_id_return

                    stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now())); // rental_datetime

                    stmt.setTimestamp(8, Timestamp.valueOf(rental.getExpectedPickupDateTime()));
                    stmt.setNull(9, Types.TIMESTAMP); // actual_pickup_datetime

                    stmt.setTimestamp(10, Timestamp.valueOf(rental.getExpectedReturnDateTime()));
                    stmt.setNull(11, Types.TIMESTAMP); // actual_return_datetime

                    stmt.setBigDecimal(12, rental.getTotalPayment());
                    stmt.setString(13, RentalRecord.RentalStatus.UPCOMING.name());

                    stmt.executeUpdate();
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback(); // Rollback on any error
                throw e; // Re-throw the exception
            }
        }
    }


    /**
     * Update full rental row (all editable fields).
     */
    public void updateRental(RentalRecord rental) throws SQLException {
        String sql = """
                UPDATE rental_details
                SET
                    rental_renter_dl_number = ?,
                    rental_car_plate_number = ?,
                    rental_branch_id = ?,
                    rental_staff_id_pickup = ?,
                    rental_staff_id_return = ?,
                    rental_expected_pickup_datetime = ?,
                    rental_actual_pickup_datetime = ?,
                    rental_expected_return_datetime = ?,
                    rental_actual_return_datetime = ?,
                    rental_total_payment = ?,
                    rental_status = ?
                WHERE rental_id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rental.getRenterDlNumber());
            stmt.setString(2, rental.getCarPlateNumber());
            stmt.setString(3, rental.getBranchId());
            stmt.setString(4, rental.getStaffIdPickup());
            stmt.setString(5, rental.getStaffIdReturn());

            stmt.setTimestamp(6, Timestamp.valueOf(rental.getExpectedPickupDateTime()));

            if (rental.getActualPickupDateTime() != null) {
                stmt.setTimestamp(7, Timestamp.valueOf(rental.getActualPickupDateTime()));
            } else {
                stmt.setNull(7, Types.TIMESTAMP);
            }

            stmt.setTimestamp(8, Timestamp.valueOf(rental.getExpectedReturnDateTime()));

            if (rental.getActualReturnDateTime() != null) {
                stmt.setTimestamp(9, Timestamp.valueOf(rental.getActualReturnDateTime()));
            } else {
                stmt.setNull(9, Types.TIMESTAMP);
            }

            stmt.setBigDecimal(10, rental.getTotalPayment());
            stmt.setString(11, rental.getRentalStatus().name());
            stmt.setString(12, rental.getRentalId());

            stmt.executeUpdate();

            // If car plate changed or status changed, ensure car statuses are consistent.
            updateCarStatus(conn, rental.getCarPlateNumber());
        }
    }

    /**
     * Apply pickup grace period: mark UPCOMING rentals as CANCELLED if expected pickup + grace <= now.
     */
    public void applyGracePeriod(LocalDateTime now) throws SQLException {
        String selectSql = """
                SELECT rental_id, rental_car_plate_number, rental_expected_pickup_datetime
                FROM rental_details
                WHERE rental_status = 'UPCOMING'
                AND rental_expected_pickup_datetime <= ?
                """;

        String updateRentalSql = """
                UPDATE rental_details
                SET rental_status = 'CANCELLED'
                WHERE rental_id = ?
                """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                // Find rentals whose pickup time is *before* (now - grace period)
                selectStmt.setTimestamp(1, Timestamp.valueOf(now.minusMinutes(PICKUP_GRACE_MINUTES)));

                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        String rentalId = rs.getString("rental_id");
                        String carPlate = rs.getString("rental_car_plate_number");

                        try (PreparedStatement updateStmt = conn.prepareStatement(updateRentalSql)) {
                            updateStmt.setString(1, rentalId);
                            updateStmt.executeUpdate();
                        }

                        // This car *might* be available now, update its status.
                        // (This implementation is safe, it only sets to 'Available' if no *other* ACTIVE rental exists)
                        updateCarStatus(conn, carPlate);
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Update car_record status based on whether there's any ACTIVE rental for the car.
     * Preserves 'Under Maintenance' status when setting Available.
     */
    private void updateCarStatus(Connection conn, String carPlate) throws SQLException {
        String rentedSql = """
                UPDATE car_record
                SET car_status = 'Rented'
                WHERE car_plate_number = ?
                AND EXISTS (
                    SELECT 1 FROM rental_details
                    WHERE rental_car_plate_number = ?
                      AND rental_status = 'ACTIVE'
                )
                """;

        try (PreparedStatement stmt = conn.prepareStatement(rentedSql)) {
            stmt.setString(1, carPlate);
            stmt.setString(2, carPlate);
            stmt.executeUpdate();
        }

        String availableSql = """
                UPDATE car_record
                SET car_status = 'Available'
                WHERE car_plate_number = ?
                  AND NOT EXISTS (
                      SELECT 1 FROM rental_details
                      WHERE rental_car_plate_number = ?
                        AND rental_status = 'ACTIVE'
                  )
                  AND car_status != 'Under Maintenance'
                """;

        try (PreparedStatement stmt = conn.prepareStatement(availableSql)) {
            stmt.setString(1, carPlate);
            stmt.setString(2, carPlate);
            stmt.executeUpdate();
        }
    }

    /**
     * Return all rentals.
     */
    public static List<RentalRecord> getAllRentals() throws SQLException {
        List<RentalRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM rental_details";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToRentalRecord(rs));
            }
        }
        return list;
    }

    /**
     * Get rental by id.
     */
    public RentalRecord getRentalById(String id) throws SQLException {
        String sql = "SELECT * FROM rental_details WHERE rental_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToRentalRecord(rs);
            }
        }
        return null;
    }

    /**
     * Get rental by id, using a provided transaction connection.
     */
    public RentalRecord getRentalById(Connection conn, String id) throws SQLException {
        String sql = "SELECT * FROM rental_details WHERE rental_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToRentalRecord(rs);
            }
        }
        return null;
    }


    /**
     * Map a resultset row into RentalRecord.
     */
        private static RentalRecord mapResultSetToRentalRecord(ResultSet rs) throws SQLException {
        // Timestamps from DB
        Timestamp rentalTs = rs.getTimestamp("rental_datetime");
        Timestamp expectedPickupTs = rs.getTimestamp("rental_pickup_datetime");
        // Timestamp actualPickupTs = rs.getTimestamp("rental_actual_pickup_datetime"); // <-- FIXED: Removed this line, column doesn't exist
        Timestamp expectedReturnTs = rs.getTimestamp("rental_expected_return_datetime");
        Timestamp actualReturnTs = rs.getTimestamp("rental_actual_return_datetime");

        // Convert to LocalDateTime, handle nulls
        LocalDateTime rentalDateTime = rentalTs != null ? rentalTs.toLocalDateTime() : null;
        LocalDateTime expectedPickupDateTime = expectedPickupTs != null ? expectedPickupTs.toLocalDateTime() : null;
        // LocalDateTime actualPickupDateTime = actualPickupTs != null ? actualPickupTs.toLocalDateTime() : null; // <-- FIXED: Removed this line
        LocalDateTime expectedReturnDateTime = expectedReturnTs != null ? expectedReturnTs.toLocalDateTime() : null;
        LocalDateTime actualReturnDateTime = actualReturnTs != null ? actualReturnTs.toLocalDateTime() : null;

        // Map enum safely
        RentalRecord.RentalStatus status;
        try {
            status = RentalRecord.RentalStatus.valueOf(rs.getString("rental_status").toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            status = RentalRecord.RentalStatus.UPCOMING; // default if DB has wrong value
        }

        return new RentalRecord(
                rs.getString("rental_id"),
                rs.getString("rental_renter_dl_number"),
                rs.getString("rental_car_plate_number"),
                rs.getString("rental_branch_id"),
                rs.getString("rental_staff_id_pickup"),
                rs.getString("rental_staff_id_return"),
                rentalDateTime,
                expectedPickupDateTime,
                null, // <-- FIXED: Passed null for 'actualPickupDateTime' since it's not in the DB
                expectedReturnDateTime,
                actualReturnDateTime,
                rs.getBigDecimal("rental_total_payment"),
                status
        );
    }

    /**
     * Partial update used by the UI modify popup:
     * Updates rental_status, rental_actual_pickup_datetime, rental_staff_id_pickup
     */
    public void updateRentalPartial(RentalRecord rental) throws SQLException {
        String sql = """
                UPDATE rental_details
                SET
                    rental_status = ?,
                    rental_actual_pickup_datetime = ?,
                    rental_staff_id_pickup = ?
                WHERE rental_id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rental.getRentalStatus().name());

            if (rental.getActualPickupDateTime() != null) {
                stmt.setTimestamp(2, Timestamp.valueOf(rental.getActualPickupDateTime()));
            } else {
                stmt.setNull(2, Types.TIMESTAMP);
            }

            stmt.setString(3, rental.getStaffIdPickup());
            stmt.setString(4, rental.getRentalId());

            stmt.executeUpdate();

            // update car status (in case status changed to ACTIVE)
            updateCarStatus(conn, rental.getCarPlateNumber());
        }
    }

    /**
     * Update only rental status (convenience).
     */
    public boolean updateRentalStatus(RentalRecord rental) {
        String sql = "UPDATE rental_details SET rental_status = ? WHERE rental_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rental.getRentalStatus().name());
            ps.setString(2, rental.getRentalId());

            int updated = ps.executeUpdate();

            // Ensure car status consistency if updated
            if (updated > 0) {
                updateCarStatus(conn, rental.getCarPlateNumber());
            }

            return updated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update only rental status, using a provided transaction connection.
     */
    public boolean updateRentalStatus(Connection conn, RentalRecord rental) throws SQLException {
        String sql = "UPDATE rental_details SET rental_status=? WHERE rental_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rental.getRentalStatus().name());
            ps.setString(2, rental.getRentalId());

            int updated = ps.executeUpdate();

            // Note: We also update car status within the same transaction.
            if (updated > 0) {
                updateCarStatus(conn, rental.getCarPlateNumber());
            }
            return updated > 0;
        }
    }

    /**
     * Processes a pickup event in a transaction.
     */
    public static void processPickup(String rentalId, String staffId, LocalDateTime actualPickup) throws SQLException {
        String sql = """
        UPDATE rental_details
        SET rental_actual_pickup_datetime = ?,
            rental_status = 'ACTIVE',
            rental_staff_id_pickup = ?
        WHERE rental_id = ?
          AND rental_status = 'UPCOMING'
        """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setTimestamp(1, Timestamp.valueOf(actualPickup));
                    stmt.setString(2, staffId);
                    stmt.setString(3, rentalId);

                    int rows = stmt.executeUpdate();
                    if (rows == 0)
                        throw new SQLException("Rental cannot be picked up (it may not be 'UPCOMING').");
                }

                // Set the car to rented
                updateCarToRented(conn, rentalId);

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private static void updateCarToRented(Connection conn, String rentalId) throws SQLException {
        String sql = """
        UPDATE car_record
        SET car_status = 'Rented'
        WHERE car_plate_number = (
            SELECT rental_car_plate_number
            FROM rental_details
            WHERE rental_id = ?
        )
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rentalId);
            stmt.executeUpdate();
        }
    }
}
