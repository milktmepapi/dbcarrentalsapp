package com.example.dbcarrentalsapp;

import model.JobRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {

    /**
     * Retrieves all job records, ordered by id.
     *
     * @return List of all jobs from the database.
     */
    public static List<JobRecord> getAllJobs() {
        List<JobRecord> jobs = new ArrayList<>();
        String query = "SELECT * FROM job_record ORDER BY job_id ASC;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                jobs.add(new JobRecord(
                        rs.getString("job_id"),
                        rs.getString("job_title"),
                        rs.getString("job_department_id"),
                        rs.getDouble("job_salary")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jobs;
    }

    public boolean addJob(String jobId, String jobTitle, String jobDepartmentId, double jobSalary) {
        String insertSql = "INSERT INTO job_record (job_id, job_title, job_department_id, job_salary) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

            pstmt.setString(1, jobId);
            pstmt.setString(2, jobTitle);
            pstmt.setString(3, jobDepartmentId);
            pstmt.setDouble(4, jobSalary);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a job.
     *
     * @param jobId           id of job to update
     * @param jobTitle        title of job
     * @param jobDepartmentId department of job
     * @param jobSalary       salary of job
     * @return true if updated successfully, false otherwise
     */
    public boolean updateJob(String jobId, String jobTitle, String jobDepartmentId, double jobSalary) {
        String updateSql = "UPDATE job_record SET job_title=?, job_department_id=?, job_salary=? WHERE job_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

            pstmt.setString(1, jobTitle);
            pstmt.setString(2, jobDepartmentId);
            pstmt.setDouble(3, jobSalary);
            pstmt.setString(4, jobId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a job by its id
     *
     * @param jobId the job ID to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteJob(String jobId) {
        String sql = "DELETE FROM job_record WHERE job_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, jobId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all job IDs for dropdown menus.
     */
    public List<String> getAllJobIds() {
        List<String> jobIds = new ArrayList<>();
        String query = "SELECT job_id FROM job_record ORDER BY job_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                jobIds.add(rs.getString("job_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jobIds;
    }

    public String generateJobID(String departmentCode) {
        String selectSql = "SELECT last_number FROM job_id_sequence WHERE department_id = ?";
        String insertOrUpdateSql = "INSERT INTO job_id_sequence (department_id, last_number) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE last_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement updateStmt = conn.prepareStatement(insertOrUpdateSql)) {

            // Check current sequence
            selectStmt.setString(1, departmentCode);
            ResultSet rs = selectStmt.executeQuery();

            int nextNumber = 1;
            if (rs.next()) {
                nextNumber = rs.getInt("last_number") + 1;
            }

            // Update or insert sequence
            updateStmt.setString(1, departmentCode);
            updateStmt.setInt(2, nextNumber);
            updateStmt.setInt(3, nextNumber);
            updateStmt.executeUpdate();

            // Generate new ID
            return departmentCode + String.format("%03d", nextNumber);

        } catch (SQLException e) {
            e.printStackTrace();
            return departmentCode + "001"; // Fallback
        }
    }

    public List<String> getAllJobDisplayValues() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT job_id, job_title FROM job_record ORDER BY job_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("job_id") + " — " + rs.getString("job_title"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}