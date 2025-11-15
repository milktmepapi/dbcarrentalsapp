// ViolationDAO.java
package com.example.dbcarrentalsapp;

import model.ViolationRecord;
import model.RentalDetails;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling violation-related database operations.
 * Provides methods for retrieving rental details, checking for late returns,
 * calculating penalties, recording violations, and generating reports.
 */
public class ViolationDAO {

    /**
     * Retrieves rental details by rental ID
     */
    public RentalDetails getRentalById(String rentalId) {
        String sql = "SELECT * FROM rental_details WHERE rental_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rentalId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new RentalDetails(
                        rs.getString("rental_id"),
                        rs.getString("rental_renter_dl_number"),
                        rs.getString("rental_car_plate_number"),
                        rs.getString("rental_branch_id"),
                        rs.getString("rental_staff_id_pickup"),
                        rs.getString("rental_staff_id_return"),
                        rs.getTimestamp("rental_datetime"),
                        rs.getTimestamp("rental_pickup_datetime"),
                        rs.getTimestamp("rental_expected_return_datetime"),
                        rs.getTimestamp("rental_actual_return_datetime"),
                        rs.getDouble("rental_total_payment"),
                        rs.getString("rental_status")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Checks if car was returned late
     */
    public boolean isReturnLate(String rentalId) {
        String sql = "SELECT rental_expected_return_datetime, rental_actual_return_datetime " +
                "FROM rental_details WHERE rental_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rentalId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Timestamp expectedReturn = rs.getTimestamp("rental_expected_return_datetime");
                Timestamp actualReturn = rs.getTimestamp("rental_actual_return_datetime");

                return actualReturn != null && actualReturn.after(expectedReturn);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Calculates late return penalty based on hours late
     */
    public double calculateLatePenalty(String rentalId, double hourlyRate) {
        String sql = "SELECT rental_expected_return_datetime, rental_actual_return_datetime " +
                "FROM rental_details WHERE rental_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rentalId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Timestamp expectedReturn = rs.getTimestamp("rental_expected_return_datetime");
                Timestamp actualReturn = rs.getTimestamp("rental_actual_return_datetime");

                if (actualReturn != null && actualReturn.after(expectedReturn)) {
                    long diffMs = actualReturn.getTime() - expectedReturn.getTime();
                    long diffHours = (diffMs / (1000 * 60 * 60)) + 1; // Round up to nearest hour

                    return diffHours * hourlyRate;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    /**
     * Records violation details in the database
     */
    public boolean recordViolation(ViolationRecord violation) {
        String sql = "INSERT INTO violation_details " +
                "(violation_id, violation_rental_id, violation_staff_id, " +
                "violation_type, violation_penalty_fee, violation_reason, " +
                "violation_duration_hours, violation_timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, violation.getViolationId());
            ps.setString(2, violation.getRentalId());
            ps.setString(3, violation.getStaffId());
            ps.setString(4, violation.getViolationType());
            ps.setDouble(5, violation.getPenaltyFee());
            ps.setString(6, violation.getReason());
            ps.setInt(7, violation.getDurationHours());
            ps.setTimestamp(8, new Timestamp(violation.getTimestamp().getTime()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates car status to Available
     */
    public boolean updateCarStatus(String plateNumber, String status) {
        String sql = "UPDATE car_record SET car_status = ? WHERE car_plate_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, plateNumber);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets all violations for reporting
     */
    public List<ViolationRecord> getAllViolations() {
        List<ViolationRecord> violations = new ArrayList<>();
        String sql = "SELECT * FROM violation_details ORDER BY violation_timestamp DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                violations.add(new ViolationRecord(
                        rs.getString("violation_id"),
                        rs.getString("violation_rental_id"),
                        rs.getString("violation_staff_id"),
                        rs.getString("violation_type"),
                        rs.getDouble("violation_penalty_fee"),
                        rs.getString("violation_reason"),
                        rs.getInt("violation_duration_hours"),
                        rs.getTimestamp("violation_timestamp")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return violations;
    }

    /**
     * Gets violations by branch
     */
    public List<ViolationRecord> getViolationsByBranch(String branchId) {
        List<ViolationRecord> violations = new ArrayList<>();
        String sql = "SELECT vd.* FROM violation_details vd " +
                "JOIN rental_details rd ON vd.violation_rental_id = rd.rental_id " +
                "WHERE rd.rental_branch_id = ? ORDER BY vd.violation_timestamp DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, branchId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                violations.add(new ViolationRecord(
                        rs.getString("violation_id"),
                        rs.getString("violation_rental_id"),
                        rs.getString("violation_staff_id"),
                        rs.getString("violation_type"),
                        rs.getDouble("violation_penalty_fee"),
                        rs.getString("violation_reason"),
                        rs.getInt("violation_duration_hours"),
                        rs.getTimestamp("violation_timestamp")  // Make sure this matches your DB
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return violations;
    }

    /**
     * Gets all branch IDs for filtering
     */
    public List<String> getAllBranchIds() {
        List<String> branchIds = new ArrayList<>();
        String sql = "SELECT branch_id FROM branch_record ORDER BY branch_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                branchIds.add(rs.getString("branch_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return branchIds;
    }
}