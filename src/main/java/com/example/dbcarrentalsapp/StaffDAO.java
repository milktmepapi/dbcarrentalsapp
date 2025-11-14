package com.example.dbcarrentalsapp;

import model.StaffRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    /**
     * Retrieves all staff records, ordered by plate number.
     *
     * @return List of all cars from the database.
     */
    public static List<StaffRecord> getAllStaff() {
        List<StaffRecord> staff = new ArrayList<>();
        String query = "SELECT * FROM staff_record ORDER BY staff_id ASC;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                staff.add(new StaffRecord(
                        rs.getString("staff_id"),
                        rs.getString("staff_first_name"),
                        rs.getString("staff_last_name"),
                        rs.getString("staff_job_id"),
                        rs.getString("staff_branch_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return staff;
    }


    /**
     * Adds a new staff if the ID are unique.
     *
     * @param staffId new id of staff member in branch
     * @param firstName first name of staff member
     * @param lastName last name of staff member
     * @param jobId brand of the car
     * @param branchId location of the staff in respective branch
     * @return true if added successfully, false otherwise
     */
    public boolean addStaff (String staffId, String firstName, String lastName, String jobId, String branchId){
        String checkIdSql = "SELECT COUNT(*) FROM staff_record WHERE staff_id = ?";
        String insertSql = "INSERT INTO car_record (staff_id, staff_first_name, staff_last_name, staff_job_id, staff_branch_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            // === Check if plate number already exists ===
            try (PreparedStatement psCheckId = conn.prepareStatement(checkIdSql)) {
                psCheckId.setString(1, staffId);
                ResultSet rs = psCheckId.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Error: Location ID already exists.");
                    return false;
                }
            }

            // === If checks pass, insert the record ===
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)){
                pstmt.setString(1, staffId);
                pstmt.setString(2, firstName);
                pstmt.setString(3, lastName);
                pstmt.setString(4, jobId);
                pstmt.setString(5, branchId);
                return true;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a new staff member.
     *
     * @param staffId id of staff member in branch
     * @param firstName first name of staff member
     * @param lastName last name of staff member
     * @param jobId id of the job of staff
     * @param branchId location of the staff in respective branch
     * @return true if added successfully, false otherwise
     */
    public boolean updateStaff(String staffId, String firstName, String lastName, String jobId, String branchId){
        String updateSql = "UPDATE staff_record SET staff_first_name=?, staff_last_name=?, staff_job_id=?, staff_branch_id=? WHERE staff_id=?";
        try (Connection conn = DBConnection.getConnection()) {
            // === Perform Update ===
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)){
                pstmt.setString(1, staffId);
                pstmt.setString(2, firstName);
                pstmt.setString(3, lastName);
                pstmt.setString(4, jobId);
                pstmt.setString(5, branchId);
                int rows = pstmt.executeUpdate();
                return rows > 0;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a staff member by its id
     *
     * @param staffId the staff ID to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteStaff(String staffId){
        String sql = "DELETE FROM staff_record WHERE staff_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, staffId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
