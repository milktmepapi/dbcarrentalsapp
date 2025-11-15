package com.example.dbcarrentalsapp;

import com.example.dbcarrentalsapp.DBConnection;
import model.RentalRecord;
import model.RentalRecord.RentalStatus;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

    // Add a new rental
    public void addRental(RentalRecord rental) throws SQLException {
        String sql = "INSERT INTO rental_details (" +
                "rental_id, rental_renter_dl_number, rental_car_plate_number, rental_branch_id, " +
                "rental_staff_id_pickup, rental_staff_id_return, rental_datetime, " +
                "rental_pickup_datetime, rental_expected_return_datetime, rental_actual_return_datetime, " +
                "rental_total_payment, rental_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rental.getRentalId());
            stmt.setString(2, rental.getRenterDlNumber());
            stmt.setString(3, rental.getCarPlateNumber());
            stmt.setString(4, rental.getBranchId());
            stmt.setString(5, rental.getStaffIdPickup());
            stmt.setString(6, rental.getStaffIdReturn());
            stmt.setTimestamp(7, Timestamp.valueOf(rental.getRentalDateTime()));
            stmt.setTimestamp(8, Timestamp.valueOf(rental.getPickupDateTime()));
            stmt.setTimestamp(9, Timestamp.valueOf(rental.getExpectedReturnDateTime()));
            stmt.setTimestamp(10, Timestamp.valueOf(rental.getActualReturnDateTime()));
            stmt.setBigDecimal(11, rental.getTotalPayment());
            stmt.setString(12, rental.getRentalStatus().name());

            stmt.executeUpdate();
        }
    }

    // Fetch a rental by ID
    public RentalRecord getRentalById(String rentalId) throws SQLException {
        String sql = "SELECT * FROM rental_details WHERE rental_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rentalId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToRentalRecord(rs);
            }
        }
        return null;
    }

    // Fetch all rentals
    public List<RentalRecord> getAllRentals() throws SQLException {
        List<RentalRecord> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental_details";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rentals.add(mapResultSetToRentalRecord(rs));
            }
        }
        return rentals;
    }

    // Update rental (e.g., status, return time, payment)
    public void updateRental(RentalRecord rental) throws SQLException {
        String sql = "UPDATE rental_details SET " +
                "rental_renter_dl_number = ?, rental_car_plate_number = ?, rental_branch_id = ?, " +
                "rental_staff_id_pickup = ?, rental_staff_id_return = ?, rental_datetime = ?, " +
                "rental_pickup_datetime = ?, rental_expected_return_datetime = ?, " +
                "rental_actual_return_datetime = ?, rental_total_payment = ?, rental_status = ? " +
                "WHERE rental_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rental.getRenterDlNumber());
            stmt.setString(2, rental.getCarPlateNumber());
            stmt.setString(3, rental.getBranchId());
            stmt.setString(4, rental.getStaffIdPickup());
            stmt.setString(5, rental.getStaffIdReturn());
            stmt.setTimestamp(6, Timestamp.valueOf(rental.getRentalDateTime()));
            stmt.setTimestamp(7, Timestamp.valueOf(rental.getPickupDateTime()));
            stmt.setTimestamp(8, Timestamp.valueOf(rental.getExpectedReturnDateTime()));
            stmt.setTimestamp(9, Timestamp.valueOf(rental.getActualReturnDateTime()));
            stmt.setBigDecimal(10, rental.getTotalPayment());
            stmt.setString(11, rental.getRentalStatus().name());
            stmt.setString(12, rental.getRentalId());

            stmt.executeUpdate();
        }
    }

    // Delete a rental by ID, returns true if success
    public boolean deleteRental(String rentalId) {
        String sql = "DELETE FROM rental_details WHERE rental_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rentalId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Helper method to map result set to model object
    private RentalRecord mapResultSetToRentalRecord(ResultSet rs) throws SQLException {
        Timestamp actualReturn = rs.getTimestamp("rental_actual_return_datetime");

        return new RentalRecord(
                rs.getString("rental_id"),
                rs.getString("rental_renter_dl_number"),
                rs.getString("rental_car_plate_number"),
                rs.getString("rental_branch_id"),
                rs.getString("rental_staff_id_pickup"),
                rs.getString("rental_staff_id_return"),
                rs.getTimestamp("rental_datetime").toLocalDateTime(),
                rs.getTimestamp("rental_pickup_datetime").toLocalDateTime(),
                rs.getTimestamp("rental_expected_return_datetime").toLocalDateTime(),
                actualReturn != null ? actualReturn.toLocalDateTime() : null,
                rs.getBigDecimal("rental_total_payment"),
                RentalStatus.valueOf(rs.getString("rental_status").toUpperCase())
        );
    }


}