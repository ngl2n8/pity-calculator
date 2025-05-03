package com.hsr.pity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckDatabase {
    public static void main(String[] args) {
        try {
            // Подключаемся к базе данных
            Connection conn = DriverManager.getConnection("jdbc:sqlite:history.db");
            Statement stmt = conn.createStatement();

            // Получаем список таблиц
            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");
            System.out.println("Таблицы в базе данных:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("name"));
            }

            // Получаем данные из таблицы banner_history
            rs = stmt.executeQuery("SELECT * FROM banner_history");
            System.out.println("\nЗаписи в таблице banner_history:");
            while (rs.next()) {
                System.out.printf("ID: %d, Время: %s, Тип: %s, Счетчик: %d, 5*: %s%n",
                    rs.getInt("id"),
                    rs.getString("timestamp"),
                    rs.getString("item_type"),
                    rs.getInt("pity_counter"),
                    rs.getBoolean("is_5star"));
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 