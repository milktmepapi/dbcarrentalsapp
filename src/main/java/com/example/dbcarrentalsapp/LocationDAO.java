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
     * Updates an existing location's city and province by its ID.
     *
     * @param id       the location ID (cannot be changed)
     * @param city     the new city name
     * @param province the new province name
     * @return true if update succeeded, false otherwise
     */
    public boolean updateLocation(String id, String city, String province) {
        String checkComboSql = "SELECT COUNT(*) FROM location_record WHERE location_city = ? AND location_province = ? AND location_id <> ?";
        String updateSql = "UPDATE location_record SET location_city = ?, location_province = ? WHERE location_id = ?";

        try (Connection conn = DBConnection.getConnection()) {

            // === Prevent duplicate city+province ===
            try (PreparedStatement psCheck = conn.prepareStatement(checkComboSql)) {
                psCheck.setString(1, city);
                psCheck.setString(2, province);
                psCheck.setString(3, id);
                ResultSet rs = psCheck.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Error: City and province combination already exists.");
                    return false;
                }
            }

            // === Perform update ===
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, city);
                ps.setString(2, province);
                ps.setString(3, id);
                int rows = ps.executeUpdate();
                return rows > 0;
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
        String checkBranches = "SELECT COUNT(*) FROM branch_record WHERE branch_location_id = ?";
        String delete = "DELETE FROM location_record WHERE location_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkBranches)) {

            checkStmt.setString(1, id);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Cannot delete: There are branches for this location.");
                return false;
            }

            try (PreparedStatement deleteStmt = conn.prepareStatement(delete)) {
                deleteStmt.setString(1, id);
                int rowsAffected = deleteStmt.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all location IDs for dropdown menus.
     */
    public List<String> getAllLocationIds() {
        List<String> locationIds = new ArrayList<>();
        String query = "SELECT location_id FROM location_record ORDER BY location_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                locationIds.add(rs.getString("location_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return locationIds;
    }

    /**
     * Retrieves all location IDs and city/province for dropdown display,
     * e.g. "MNL001 — Manila, Metro Manila"
     */
    public List<String> getAllLocationDisplayValues() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT location_id, location_city, location_province FROM location_record ORDER BY location_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("location_id");
                String city = rs.getString("location_city");
                String province = rs.getString("location_province");
                list.add(String.format("%s — %s, %s", id, city, province));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}