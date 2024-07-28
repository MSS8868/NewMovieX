package com.moviex;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:" + new File("moviex.db").getAbsolutePath();

    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Create users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "email TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL)");

            // Create movies table
            stmt.execute("CREATE TABLE IF NOT EXISTS movies (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT NOT NULL," +
                    "description TEXT," +
                    "image_path TEXT)");

            // Create reviews table
            stmt.execute("CREATE TABLE IF NOT EXISTS reviews (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "movie_id INTEGER," +
                    "user_id INTEGER," +
                    "review TEXT NOT NULL," +
                    "FOREIGN KEY (movie_id) REFERENCES movies(id)," +
                    "FOREIGN KEY (user_id) REFERENCES users(id))");

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    public static boolean registerUser(String email, String password) {
        String sql = "INSERT INTO users (email, password) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password); // In a real application, you should hash the password
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public static boolean authenticateUser(String email, String password) {
        String sql = "SELECT password FROM users WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password); // In a real application, you should compare hashed passwords
            }
        } catch (SQLException e) {
            System.out.println("Error authenticating user: " + e.getMessage());
        }
        return false;
    }

    public static List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT id, title, description, image_path FROM movies";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String imagePath = rs.getString("image_path");
                movies.add(new Movie(id, title, description, imagePath));
            }
        } catch (SQLException e) {
            System.out.println("Error getting movies: " + e.getMessage());
        }
        return movies;
    }

    public static boolean addReview(int movieId, String userEmail, String review) {
        String sql = "INSERT INTO reviews (movie_id, user_id, review) VALUES (?, (SELECT id FROM users WHERE email = ?), ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            pstmt.setString(2, userEmail);
            pstmt.setString(3, review);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding review: " + e.getMessage());
            return false;
        }
    }

    public static List<Review> getReviewsForMovie(int movieId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.id, r.movie_id, u.email, r.review FROM reviews r JOIN users u ON r.user_id = u.id WHERE r.movie_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String userEmail = rs.getString("email");
                String reviewText = rs.getString("review");
                reviews.add(new Review(id, movieId, userEmail, reviewText));
            }
        } catch (SQLException e) {
            System.out.println("Error getting reviews: " + e.getMessage());
        }
        return reviews;
    }
}