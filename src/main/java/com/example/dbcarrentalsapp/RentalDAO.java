package com.example.dbcarrentalsapp;

import model.RentalRecord;
import model.RentalRecord.RentalStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

    private static final int PICKUP_GRACE_MINUTES = 5;

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

    public void addRental(RentalRecord rental) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            rental.setRentalId(generateNextRentalId());

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
                stmt.setString(5, rental.getStaffIdPickup());
                stmt.setString(6, rental.getStaffIdReturn());
                stmt.setTimestamp(7, Timestamp.valueOf(rental.getRentalDateTime()));
                stmt.setTimestamp(8, Timestamp.valueOf(rental.getExpectedPickupDateTime()));

                stmt.setTimestamp(9, rental.getActualPickupDateTime() != null ?
                        Timestamp.valueOf(rental.getActualPickupDateTime()) : null);

                stmt.setTimestamp(10, Timestamp.valueOf(rental.getExpectedReturnDateTime()));

                stmt.setTimestamp(11, rental.getActualReturnDateTime() != null ?
                        Timestamp.valueOf(rental.getActualReturnDateTime()) : null);

                stmt.setBigDecimal(12, rental.getTotalPayment());
                stmt.setString(13, rental.getRentalStatus().name());
                stmt.executeUpdate();
            }

            updateCarStatus(conn, rental.getCarPlateNumber());

            conn.commit();
        }
    }

    public void updateRental(RentalRecord rental) throws SQLException {
        String sql = """
        UPDATE rental_record
        SET renter_dl_number = ?,
            car_plate_number = ?,
            branch_id = ?,
            staff_id_pickup = ?,
            staff_id_return = ?,
            expected_pickup_datetime = ?,
            actual_pickup_datetime = ?,
            expected_return_datetime = ?,
            actual_return_datetime = ?,
            total_payment = ?,
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

            // Handle nullable actual pickup
            if (rental.getActualPickupDateTime() != null) {
                stmt.setTimestamp(7, Timestamp.valueOf(rental.getActualPickupDateTime()));
            } else {
                stmt.setNull(7, java.sql.Types.TIMESTAMP);
            }

            stmt.setTimestamp(8, Timestamp.valueOf(rental.getExpectedReturnDateTime()));

            // Handle nullable actual return
            if (rental.getActualReturnDateTime() != null) {
                stmt.setTimestamp(9, Timestamp.valueOf(rental.getActualReturnDateTime()));
            } else {
                stmt.setNull(9, java.sql.Types.TIMESTAMP);
            }

            stmt.setBigDecimal(10, rental.getTotalPayment());
            stmt.setString(11, rental.getRentalStatus().name());

            // rental_id is a STRING here
            stmt.setString(12, rental.getRentalId());

            stmt.executeUpdate();
        }
    }


    // AUTO-GRACE LOGIC FIXED
    public void applyGracePeriod(LocalDateTime now) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            String selectSql = """
                    SELECT rental_id, rental_car_plate_number, rental_expected_pickup_datetime
                    FROM rental_details
                    WHERE rental_status = 'UPCOMING'
                    AND rental_expected_pickup_datetime <= ?
                    """;

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setTimestamp(1, Timestamp.valueOf(now.minusMinutes(PICKUP_GRACE_MINUTES)));
                ResultSet rs = selectStmt.executeQuery();

                while (rs.next()) {
                    String rentalId = rs.getString("rental_id");
                    String carPlate = rs.getString("rental_car_plate_number");
                    LocalDateTime expectedPickup =
                            rs.getTimestamp("rental_expected_pickup_datetime").toLocalDateTime();

                    LocalDateTime actualPickup = expectedPickup.plusMinutes(PICKUP_GRACE_MINUTES);

                    String updateRentalSql = """
                            UPDATE rental_details
                            SET rental_status = 'ACTIVE',
                                rental_actual_pickup_datetime = ?
                            WHERE rental_id = ?
                            """;

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateRentalSql)) {
                        updateStmt.setTimestamp(1, Timestamp.valueOf(actualPickup));
                        updateStmt.setString(2, rentalId);
                        updateStmt.executeUpdate();
                    }

                    updateCarStatus(conn, carPlate);
                }
            }

            conn.commit();
        }
    }

    // CAR STATUS FIXED
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

    public List<RentalRecord> getAllRentals() throws SQLException {
        List<RentalRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM rental_details";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(mapResultSetToRentalRecord(rs));
        }

        return list;
    }

    public RentalRecord getRentalById(String id) throws SQLException {
        String sql = "SELECT * FROM rental_details WHERE rental_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return mapResultSetToRentalRecord(rs);
        }

        return null;
    }

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

    public void updateRentalPartial(RentalRecord rental) throws SQLException {
        String sql = """
        UPDATE rental_record
        SET rental_status = ?,
            actual_pickup_datetime = ?,
            pickup_staff_id = ?
        WHERE rental_id = ?
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rental.getRentalStatus().name());
            stmt.setTimestamp(2, rental.getActualPickupDateTime() == null ?
                    null : Timestamp.valueOf(rental.getActualPickupDateTime()));
            stmt.setString(3, rental.getStaffIdPickup());
            stmt.setString(4, rental.getRentalId());

            stmt.executeUpdate();
        }
    }

}