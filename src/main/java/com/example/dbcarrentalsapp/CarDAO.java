package com.example.dbcarrentalsapp;

import model.CarRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CarDAO  {

    /**
     * Retrieves all car records, ordered by plate number.
     *
     * @return List of all cars from the database.
     */
    public static List<CarRecord> getAllCars() {
        List<CarRecord> cars = new ArrayList<>();
        String query = "SELECT * FROM car_record" +
                "ORDER BY car_plate_number ASC;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cars.add(new CarRecord(
                        rs.getString("car_plate_number"),
                        rs.getString("car_transmission"),
                        rs.getString("car_model"),
                        rs.getString("car_brand"),
                        rs.getInt("car_year_manufactured"),
                        rs.getInt("car_mileage"),
                        rs.getInt("car_seat_number"),
                        rs.getString("car_status"),
                        rs.getString("car_branch_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cars;
    }


    /**
     * Adds a new car if the plate number are unique.
     *
     * @param plateNumber  the plate number of each car
     * @param transmission transfer of power from engine to wheels
     * @param model model of the car
     * @param brand brand of the car
     * @param yearManufactured year of the car's creation
     * @param mileage total distance of the car
     * @param seatNumber number of seats
     * @param status status of car if Available or Not
     * @param branchId location of car in respective branch
     * @return true if added successfully, false otherwise
     */
    public boolean addCar (String plateNumber, String transmission, String model, String brand, int yearManufactured, int mileage, int seatNumber, String status, String branchId){
        String checkIdSql = "SELECT COUNT(*) FROM car_record WHERE car_plate_number = ?";
        String insertSql = "INSERT INTO car_record (car_plate_number, car_transmission, car_model, car_brand, car_year_manufactured, car_mileage, car_seat_number, car_status, car_branch_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            // === Check if plate number already exists ===
            try (PreparedStatement psCheckId = conn.prepareStatement(checkIdSql)) {
                psCheckId.setString(1, plateNumber);
                ResultSet rs = psCheckId.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Error: Location ID already exists.");
                    return false;
                }
            }

            // === If checks pass, insert the record ===
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)){
                pstmt.setString(1, plateNumber);
                pstmt.setString(2, transmission);
                pstmt.setString(3,model);
                pstmt.setString(4, brand);
                pstmt.setInt(5, yearManufactured);
                pstmt.setInt(6, mileage);
                pstmt.setInt(7, seatNumber);
                pstmt.setString(8, status);
                pstmt.setString(9, branchId);
                return true;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a new car.
     *
     * @param plateNumber  the plate number of each car
     * @param transmission transfer of power from engine to wheels
     * @param model model of the car
     * @param brand brand of the car
     * @param yearManufactured year of the car's creation
     * @param mileage total distance of the car
     * @param seatNumber number of seats
     * @param status status of car if Available or Not
     * @param branchId location of car in respective branch
     * @return true if added successfully, false otherwise
     */
    public boolean updateCar(String plateNumber, String transmission, String model, String brand, int yearManufactured, int mileage, int seatNumber, String status, String branchId){
        String updateSql = "UPDATE car_record SET car_transmission=?, car_model=?, car_brand=?, car_year_manufactured=?, car_mileage=?, car_seat_number=?, car_status=?, car_branch_id=? WHERE car_plate_number=?";
        try (Connection conn = DBConnection.getConnection()) {
            // === Perform Update ===
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)){
                pstmt.setString(1, plateNumber);
                pstmt.setString(2, transmission);
                pstmt.setString(3,model);
                pstmt.setString(4, brand);
                pstmt.setInt(5, yearManufactured);
                pstmt.setInt(6, mileage);
                pstmt.setInt(7, seatNumber);
                pstmt.setString(8, status);
                pstmt.setString(9, branchId);
                int rows = pstmt.executeUpdate();
                return rows > 0;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a car by its plate number.
     *
     * @param plateNumber the location ID to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteCar(String plateNumber){
        String sql = "DELETE FROM car_record WHERE car_plate_Number=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, plateNumber);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
