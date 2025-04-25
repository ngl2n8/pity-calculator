package com.hsr.pity.database;

import java.sql.*;
import java.time.LocalDateTime;

public class BannerHistoryDao {
    private static final String DB_URL = "jdbc:sqlite:history.db";

    public BannerHistoryDao() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS banner_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "timestamp DATETIME," +
                    "item_type TEXT," +
                    "pity_counter INTEGER," +
                    "is_5star BOOLEAN)";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertRecord(LocalDateTime timestamp, String itemType,
                             int pityCounter, boolean is5Star) {
        String sql = "INSERT INTO banner_history(timestamp, item_type, pity_counter, is_5star) VALUES(?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, timestamp.toString());
            pstmt.setString(2, itemType);
            pstmt.setInt(3, pityCounter);
            pstmt.setBoolean(4, is5Star);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}