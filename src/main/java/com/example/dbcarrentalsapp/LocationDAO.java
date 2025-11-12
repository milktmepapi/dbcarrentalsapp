package com.example.dbcarrentalsapp;

import model.LocationRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for the Location module.
 */
public class LocationDAO {

    /**
     * Retrieves all location records, ordered by ID.
     *
     * @return List of all locations from the database.
     */
    public List<LocationRecord> getAllLocations() {
        List<LocationRecord> list = new ArrayList<>();
        String sql = "SELECT location_id, location_city, location_province " +
                "FROM location_record ORDER BY location_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new LocationRecord(
                        rs.getString("location_id"),
                        rs.getString("location_city"),
                        rs.getString("location_province")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Adds a new location if both ID and city+province are unique.
     *
     * @param id       the location ID (manually entered)
     * @param city     the city name
     * @param province the province name
     * @return true if added successfully, false otherwise
     */
    public boolean addLocation(String id, String city, String province) {
        String checkIdSql = "SELECT COUNT(*) FROM location_record WHERE location_id = ?";
        String checkComboSql = "SELECT COUNT(*) FROM location_record WHERE location_city = ? AND location_province = ?";
        String insertSql = "INSERT INTO location_record (location_id, location_city, location_province) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {

            // === Check if ID already exists ===
            try (PreparedStatement psCheckId = conn.prepareStatement(checkIdSql)) {
                psCheckId.setString(1, id);
                ResultSet rs = psCheckId.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Error: Location ID already exists.");
                    return false;
                }
            }

            // === Check if (city, province) already exists ===
            try (PreparedStatement psCheckCombo = conn.prepareStatement(checkComboSql)) {
                psCheckCombo.setString(1, city);
                psCheckCombo.setString(2, province);
                ResultSet rs = psCheckCombo.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Error: City and province combination already exists.");
                    return false;
                }
            }

            // === If both checks pass, insert the record ===
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setString(1, id);
                psInsert.setString(2, city);
                psInsert.setString(3, province);
                psInsert.executeUpdate();
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a location by its ID.
     *
     * @param id the location ID to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteLocation(String id) {
        String sql = "DELETE FROM location_record WHERE location_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // true if something was deleted

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}