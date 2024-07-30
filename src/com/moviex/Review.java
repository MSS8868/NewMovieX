package com.moviex;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Review {
    private int id;
    private int movieId;
    private String userEmail;
    private String reviewText;
    private LocalDateTime dateTime;
    private int rating;

    public Review(int id, int movieId, String userEmail, String reviewText, LocalDateTime dateTime, int rating) {
        this.id = id;
        this.movieId = movieId;
        this.userEmail = userEmail;
        this.reviewText = reviewText;
        this.dateTime = dateTime;
        this.rating = rating;
    }

    // Getters
    public int getId() { return id; }
    public int getMovieId() { return movieId; }
    public String getUserEmail() { return userEmail; }
    public String getReviewText() { return reviewText; }
    public LocalDateTime getDateTime() { return dateTime; }
    public int getRating() { return rating; }

    public String getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public String getStarRating() {
        return "★".repeat(rating) + "☆".repeat(5 - rating);
    }
}
