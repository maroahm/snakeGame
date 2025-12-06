/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snakegame;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
/**
 *
 * @author omar
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/snake_game";
    private static final String DB_USER = "snake_user";
    private static final String DB_PASSWORD = "snake_password";

    private Connection connection;

    public void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            // Show an error message to the user
            JOptionPane.showMessageDialog(null, 
                "Could not connect to the database. Please ensure the MySQL server is running.",
                "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) { /* Ignore */ }
        }
    }

    public void saveScore(String playerName, int score) {
        if (connection == null) return;
        String sql = "INSERT INTO highscores(player_name, score) VALUES(?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving score: " + e.getMessage());
        }
    }

    public List<String> getHighScores() {
        if (connection == null) return new ArrayList<>();
        List<String> highScores = new ArrayList<>();
        String sql = "SELECT player_name, score FROM highscores ORDER BY score DESC LIMIT 10";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int rank = 1;
            while (rs.next()) {
                String name = rs.getString("player_name");
                int score = rs.getInt("score");
                highScores.add(rank + ". " + name + " - " + score);
                rank++;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving high scores: " + e.getMessage());
        }
        return highScores;
    }
}

