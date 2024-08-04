package com.moviex;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
                    "password TEXT NOT NULL," +
                    "is_trusted_reviewer BOOLEAN DEFAULT FALSE," +
                    "has_badge BOOLEAN DEFAULT FALSE)");

            // Create movies table with rating column
            stmt.execute("CREATE TABLE IF NOT EXISTS movies (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT NOT NULL," +
                    "description TEXT," +
                    "image_path TEXT," +
                    "rating REAL DEFAULT 0)");

            // Create reviews table with rating column
            stmt.execute("CREATE TABLE IF NOT EXISTS reviews (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "movie_id INTEGER," +
                    "user_id INTEGER," +
                    "review TEXT NOT NULL," +
                    "rating INTEGER NOT NULL," +
                    "date_time TEXT NOT NULL," +
                    "likes INTEGER DEFAULT 0," +
                    "FOREIGN KEY (movie_id) REFERENCES movies(id)," +
                    "FOREIGN KEY (user_id) REFERENCES users(id))");

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    public static boolean authenticateUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // If a row is returned, authentication is successful
        } catch (SQLException e) {
            System.out.println("Error authenticating user: " + e.getMessage());
            return false;
        }
    }

    public static boolean registerUser(String email, String password) {
        String sql = "INSERT INTO users (email, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public static List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT id, title, description, image_path, rating FROM movies";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String imagePath = rs.getString("image_path");
                double rating = rs.getDouble("rating");
                movies.add(new Movie(id, title, description, imagePath, rating));
            }
        } catch (SQLException e) {
            System.out.println("Error getting movies: " + e.getMessage());
        }
        return movies;
    }

    public static boolean addReview(int movieId, String userEmail, String review, int rating) {
        String sql = "INSERT INTO reviews (movie_id, user_id, review, rating, date_time) VALUES (?, (SELECT id FROM users WHERE email = ?), ?, ?, datetime('now'))";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            pstmt.setString(2, userEmail);
            pstmt.setString(3, review);
            pstmt.setInt(4, rating);
            pstmt.executeUpdate();

            // Update movie rating
            updateMovieRating(movieId);

            return true;
        } catch (SQLException e) {
            System.out.println("Error adding review: " + e.getMessage());
            return false;
        }
    }

    public static boolean addOrUpdateMovie(String title, String description, String imageUrl) {
        String sql = "INSERT INTO movies (title, description, image_path) VALUES (?, ?, ?) " +
                "ON CONFLICT(title) DO UPDATE SET description = ?, image_path = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, imageUrl);
            pstmt.setString(4, description);
            pstmt.setString(5, imageUrl);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error adding/updating movie: " + e.getMessage());
            return false;
        }
    }

    public static boolean likeReview(int reviewId) {
        String sqlUpdateLikes = "UPDATE reviews SET likes = likes + 1 WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmtUpdateLikes = conn.prepareStatement(sqlUpdateLikes)) {

            pstmtUpdateLikes.setInt(1, reviewId);
            int affectedRows = pstmtUpdateLikes.executeUpdate();
            if (affectedRows > 0) {
                checkAndUpdateBadge(reviewId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Error liking review: " + e.getMessage());
            return false;
        }
    }

    private static void checkAndUpdateBadge(int reviewId) {
        String sqlSelectReview = "SELECT user_id, likes FROM reviews WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmtSelectReview = conn.prepareStatement(sqlSelectReview)) {

            pstmtSelectReview.setInt(1, reviewId);
            ResultSet rs = pstmtSelectReview.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                int likes = rs.getInt("likes");
                if (likes >= 5) {
                    updateUserBadge(userId, true);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking badge: " + e.getMessage());
        }
    }

    private static void updateUserBadge(int userId, boolean hasBadge) {
        String sql = "UPDATE users SET has_badge = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, hasBadge);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating user badge: " + e.getMessage());
        }
    }

    public static void markMovieAsWatched(String userEmail, int movieId) {
        String sql = "INSERT OR IGNORE INTO watched_movies (user_id, movie_id) VALUES ((SELECT id FROM users WHERE email = ?), ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            pstmt.setInt(2, movieId);
            pstmt.executeUpdate();
            System.out.println("Movie marked as watched successfully.");
        } catch (SQLException e) {
            System.out.println("Error marking movie as watched: " + e.getMessage());
        }
    }

    public static List<Review> getReviewsForMovie(int movieId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.id, r.movie_id, u.email, r.review, r.rating, r.date_time, r.likes, u.has_badge " +
                "FROM reviews r JOIN users u ON r.user_id = u.id WHERE r.movie_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                int id = rs.getInt("id");
                String userEmail = rs.getString("email");
                String reviewText = rs.getString("review");
                int rating = rs.getInt("rating");
                String dateTimeString = rs.getString("date_time");
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
                int likes = rs.getInt("likes");
                boolean hasBadge = rs.getBoolean("has_badge");

                Review review = new Review(id, movieId, userEmail, reviewText, dateTime, rating, likes, false, hasBadge);
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.out.println("Error getting reviews: " + e.getMessage());
        }
        return reviews;
    }

    private static void updateMovieRating(int movieId) {
        String sql = "UPDATE movies SET rating = (SELECT AVG(rating) FROM reviews WHERE movie_id = ?) WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            pstmt.setInt(2, movieId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating movie rating: " + e.getMessage());
        }
    }
}
