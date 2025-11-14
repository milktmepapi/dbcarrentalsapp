package com.example.dbcarrentalsapp;

import model.RenterRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RenterDAO {

    /** Get all renters */
    public List<RenterRecord> getAllRenters() {
        List<RenterRecord> renters = new ArrayList<>();
        String sql = "SELECT * FROM renter_record ORDER BY renter_dl_number ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                renters.add(new RenterRecord(
                        rs.getString("renter_dl_number"),
                        rs.getString("renter_first_name"),
                        rs.getString("renter_last_name"),
                        rs.getString("renter_phone_number"),
                        rs.getString("renter_email_address")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return renters;
    }

    /** Add renter using RenterRecord */
    public boolean addRenter(RenterRecord r) {

        String check = "SELECT COUNT(*) FROM renter_record WHERE renter_dl_number = ?";
        String insert =
                "INSERT INTO renter_record " +
                        "(renter_dl_number, renter_first_name, renter_last_name, renter_phone_number, renter_email_address) " +
                        "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {

            // Check ID first
            try (PreparedStatement ps = conn.prepareStatement(check)) {
                ps.setString(1, r.getRenterDlNumber());
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) return false;
            }

            // Insert
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setString(1, r.getRenterDlNumber());
                ps.setString(2, r.getRenterFirstName());
                ps.setString(3, r.getRenterLastName());
                ps.setString(4, r.getRenterPhoneNumber());
                ps.setString(5, r.getRenterEmailAddress());
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Update renter using RenterRecord */
    public boolean updateRenter(RenterRecord r) {

        String update =
                "UPDATE renter_record SET " +
                        "renter_first_name=?, renter_last_name=?, renter_phone_number=?, renter_email_address=? " +
                        "WHERE renter_dl_number=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(update)) {

            ps.setString(1, r.getRenterFirstName());
            ps.setString(2, r.getRenterLastName());
            ps.setString(3, r.getRenterPhoneNumber());
            ps.setString(4, r.getRenterEmailAddress());
            ps.setString(5, r.getRenterDlNumber());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Delete renter */
    public boolean deleteRenter(String dl) {
        String sql = "DELETE FROM renter_record WHERE renter_dl_number=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dl);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
