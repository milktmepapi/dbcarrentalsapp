package com.example.dbcarrentalsapp;

import model.CarUtilizationReport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CarUtilizationReportDAO {
    public List<CarUtilizationReport> getCarUtilizationReport() {
        List<CarUtilizationReport> list = new ArrayList<>();

        // 1. MODIFIED SQL (Removed renter join, select, and group by)
        String sql = """
                SELECT b.branch_name, c.car_plate_number, c.car_brand, c.car_model, c.car_transmission,
                COUNT(rd.rental_id) AS total_rentals,
                COALESCE(SUM(DATEDIFF(
                    COALESCE(rd.rental_actual_return_datetime, rd.rental_expected_return_datetime),
                    COALESCE(rd.rental_actual_return_datetime, rd.rental_expected_return_datetime)
                ) + 1), 0) AS total_days_rented,
                ROUND(
                    COALESCE(SUM(DATEDIFF(
                        COALESCE(rd.rental_actual_return_datetime, rd.rental_expected_return_datetime),
                        COALESCE(rd.rental_actual_return_datetime, rd.rental_expected_return_datetime)
                     ) + 1), 0) / 365.0 * 100, 2
                ) AS rate_of_utilization
                FROM car_record c
                JOIN rental_details rd ON rd.rental_car_plate_number = c.car_plate_number
                JOIN branch_record b on c.car_branch_id = b.branch_id
                GROUP BY b.branch_name, c.car_plate_number, c.car_brand, c.car_model, c.car_transmission
                ORDER BY b.branch_name, rate_of_utilization DESC
           """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                // 2. MODIFIED Constructor Call (Removed renter_dl_number)
                //    This assumes you have a 4-argument constructor in BranchReport
                list.add(new CarUtilizationReport(
                        rs.getString("branch_name"),
                        rs.getString("car_plate_number"),
                        rs.getString("car_brand"),
                        rs.getString("car_model"),
                        rs.getString("car_transmission"),
                        rs.getInt("total_rentals"),
                        rs.getInt("total_days_rented"),
                        rs.getDouble("rate_of_utilization")
                ));
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }
}
