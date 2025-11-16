package com.example.dbcarrentalsapp;

import model.RentalRecord;
import model.RentalRecord.RentalStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

    private static final int PICKUP_GRACE_MINUTES = 5;

    /**
     * Generate next rental id in format RNT### (always).
     */
    public String generateNextRentalId() throws SQLException {
        String sql = "SELECT rental_id FROM rental_details WHERE rental_id LIKE 'RNT%' ORDER BY rental_id DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastId = rs.getString("rental_id");
                int num = Integer.parseInt(lastId.substring(3)) + 1;
                return String.format("RNT%03d", num);
            }
        }
        return "RNT001";
    }

    /**
     * Add rental. Always generates a new rental id regardless of incoming object's id.
     * This method uses a transaction and rolls back on failure.
     */
    public void addRental(RentalRecord rental) throws SQLException {
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

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                // Always generate ID
                rental.setRentalId(generateNextRentalId());

                stmt.setString(1, rental.getRentalId());
                stmt.setString(2, rental.getRenterDlNumber());
                stmt.setString(3, rental.getCarPlateNumber());
                stmt.setString(4, rental.getBranchId());
                stmt.setString(5, rental.getStaffIdPickup());
                stmt.setString(6, rental.getStaffIdReturn());

                // These timestamps are NOT NULL in your schema (rental_datetime, expected pick/return)
                stmt.setTimestamp(7, Timestamp.valueOf(rental.getRentalDateTime()));
                stmt.setTimestamp(8, Timestamp.valueOf(rental.getExpectedPickupDateTime()));

                if (rental.getActualPickupDateTime() != null) {
                    stmt.setTimestamp(9, Timestamp.valueOf(rental.getActualPickupDateTime()));
                } else {
                    stmt.setNull(9, Types.TIMESTAMP);
                }

                stmt.setTimestamp(10, Timestamp.valueOf(rental.getExpectedReturnDateTime()));

                if (rental.getActualReturnDateTime() != null) {
                    stmt.setTimestamp(11, Timestamp.valueOf(rental.getActualReturnDateTime()));
                } else {
                    stmt.setNull(11, Types.TIMESTAMP);
                }

                stmt.setBigDecimal(12, rental.getTotalPayment());
                stmt.setString(13, rental.getRentalStatus().name());

                stmt.executeUpdate();
            }

            // update car status after inserting rental
            updateCarStatus(conn, rental.getCarPlateNumber());

            conn.commit();
        } catch (SQLException ex) {
            // If an exception occurs the connection will be closed by try-with-resources,
            // but we must ensure rollback — rethrow to indicate failure.
            throw ex;
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
     * Apply pickup grace period: mark UPCOMING rentals as ACTIVE if expected pickup + grace <= now.
     * This method sets actual pickup datetime = expected + grace (as per original design).
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
                SET rental_status = 'ACTIVE',
                    rental_actual_pickup_datetime = ?
                WHERE rental_id = ?
                """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setTimestamp(1, Timestamp.valueOf(now.minusMinutes(PICKUP_GRACE_MINUTES)));
                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        String rentalId = rs.getString("rental_id");
                        String carPlate = rs.getString("rental_car_plate_number");
                        LocalDateTime expectedPickup = rs.getTimestamp("rental_expected_pickup_datetime").toLocalDateTime();
                        LocalDateTime actualPickup = expectedPickup.plusMinutes(PICKUP_GRACE_MINUTES);

                        try (PreparedStatement updateStmt = conn.prepareStatement(updateRentalSql)) {
                            updateStmt.setTimestamp(1, Timestamp.valueOf(actualPickup));
                            updateStmt.setString(2, rentalId);
                            updateStmt.executeUpdate();
                        }

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
    public List<RentalRecord> getAllRentals() throws SQLException {
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
     * Map a resultset row into RentalRecord.
     */
    private RentalRecord mapResultSetToRentalRecord(ResultSet rs) throws SQLException {
        Timestamp actualPickup = rs.getTimestamp("rental_actual_pickup_datetime");
        Timestamp actualReturn = rs.getTimestamp("rental_actual_return_datetime");

        return new RentalRecord(
                rs.getString("rental_id"),
                rs.getString("rental_renter_dl_number"),
                rs.getString("rental_car_plate_number"),
                rs.getString("rental_branch_id"),
                rs.getString("rental_staff_id_pickup"),
                rs.getString("rental_staff_id_return"),
                rs.getTimestamp("rental_datetime").toLocalDateTime(),
                rs.getTimestamp("rental_expected_pickup_datetime").toLocalDateTime(),
                actualPickup != null ? actualPickup.toLocalDateTime() : null,
                rs.getTimestamp("rental_expected_return_datetime").toLocalDateTime(),
                actualReturn != null ? actualReturn.toLocalDateTime() : null,
                rs.getBigDecimal("rental_total_payment"),
                RentalStatus.valueOf(rs.getString("rental_status"))
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
}
