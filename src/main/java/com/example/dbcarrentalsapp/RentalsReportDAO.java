package com.example.dbcarrentalsapp;

import model.BranchReport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class RentalsReportDAO {

    public List<BranchReport> getRentalsByBranch() {
        List<BranchReport> list = new ArrayList<>();

        // 1. MODIFIED SQL (Removed renter join, select, and group by)
        String sql = "SELECT b.branch_name, c.car_transmission, " +
                "CASE WHEN DATEDIFF(rd.rental_actual_return_datetime, rd.rental_pickup_datetime) <= 3 THEN 'Short-term' " +
                "WHEN DATEDIFF(rd.rental_actual_return_datetime, rd.rental_pickup_datetime) <= 7 THEN 'Medium-term' " +
                "ELSE 'Long-term' END AS rental_duration, " +
                "COUNT(*) AS total_rentals " +
                "FROM rental_details rd " +
                "JOIN car_record c ON rd.rental_car_plate_number = c.car_plate_number " +
                "JOIN branch_record b ON c.car_branch_id = b.branch_id " +
                "WHERE rd.rental_status = 'Completed' " + //
                "GROUP BY b.branch_name, c.car_transmission, rental_duration " +
                "ORDER BY b.branch_name, c.car_transmission";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // 2. MODIFIED Constructor Call (Removed renter_dl_number)
                //    This assumes you have a 4-argument constructor in BranchReport
                list.add(new BranchReport(
                        rs.getString("branch_name"),
                        rs.getString("car_transmission"),
                        rs.getString("rental_duration"),
                        // rs.getString("renter_dl_number"), // <-- REMOVED
                        rs.getInt("total_rentals")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
