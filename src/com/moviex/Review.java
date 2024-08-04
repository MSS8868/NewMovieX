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
    private int likes;
    private boolean isTrustedReviewer;
    private boolean hasBadge; // New field for badge

    // Updated constructor
    public Review(int id, int movieId, String userEmail, String reviewText, LocalDateTime dateTime, int rating, int likes, boolean isTrustedReviewer, boolean hasBadge) {
        this.id = id;
        this.movieId = movieId;
        this.userEmail = userEmail;
        this.reviewText = reviewText;
        this.dateTime = dateTime;
        this.rating = rating;
        this.likes = likes;
        this.isTrustedReviewer = isTrustedReviewer;
        this.hasBadge = hasBadge;
    }

    // Getters
    public int getId() { return id; }
    public int getMovieId() { return movieId; }
    public String getUserEmail() { return userEmail; }
    public String getReviewText() { return reviewText; }
    public LocalDateTime getDateTime() { return dateTime; }
    public int getRating() { return rating; }
    public int getLikes() { return likes; }
    public boolean isTrustedReviewer() { return isTrustedReviewer; }
    public boolean hasBadge() { return hasBadge; } // New getter

    // Setters
    public void setLikes(int likes) { this.likes = likes; }
    public void setTrustedReviewer(boolean trustedReviewer) { this.isTrustedReviewer = trustedReviewer; }
    public void setBadge(boolean hasBadge) { this.hasBadge = hasBadge; } // New setter

    public String getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public String getStarRating() {
        return "★".repeat(rating) + "☆".repeat(5 - rating);
    }

    public String getDisplayName() {
        return hasBadge ? userEmail + " 🏅" : userEmail; // Updated to include badge
    }
}
