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


    /**
     * Adds a new job if the ID are unique.
     *
     * @param jobId new id of job in branch
     * @param jobTitle title of job
     * @param jobDepartmentId department of job
     * @param jobSalary salary of job
     * @return true if added successfully, false otherwise
     */
    public boolean addJob (String jobId, String jobTitle, String jobDepartmentId, String jobSalary){
        String checkIdSql = "SELECT COUNT(*) FROM job_record WHERE job_id = ?";
        String insertSql = "INSERT INTO job_record (job_id, job_title, job_department, job_salary) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            // === Check if plate number already exists ===
            try (PreparedStatement psCheckId = conn.prepareStatement(checkIdSql)) {
                psCheckId.setString(1, jobId);
                ResultSet rs = psCheckId.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Error: Job ID already exists.");
                    return false;
                }
            }

            // === If checks pass, insert the record ===
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)){
                pstmt.setString(1, jobId);
                pstmt.setString(2, jobTitle);
                pstmt.setString(3, jobDepartmentId);
                pstmt.setString(4, jobSalary);
                return true;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a new job.
     *
     * @param jobId new id of job in branch
     * @param jobTitle title of job
     * @param jobDepartmentId department of job
     * @param jobSalary salary of job
     * @return true if added successfully, false otherwise
     */
    public boolean updateJob(String jobId, String jobTitle, String jobDepartmentId, String jobSalary){
        String updateSql = "UPDATE job_record SET job_title=?, job_department_id=?, job_salary=? WHERE job_id=?";
        try (Connection conn = DBConnection.getConnection()) {
            // === Perform Update ===
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)){
                pstmt.setString(1, jobId);
                pstmt.setString(2, jobTitle);
                pstmt.setString(3, jobDepartmentId);
                pstmt.setString(4, jobSalary);
                int rows = pstmt.executeUpdate();
                return rows > 0;
            }
        } catch (SQLException e){
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
    public boolean deleteJob(String jobId){
        String sql = "DELETE FROM job_record WHERE job_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, jobId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
