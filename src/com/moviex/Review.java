package com.moviex;

public class Review {
    private int id;
    private int movieId;
    private String userEmail;
    private String reviewText;

    public Review(int id, int movieId, String userEmail, String reviewText) {
        this.id = id;
        this.movieId = movieId;
        this.userEmail = userEmail;
        this.reviewText = reviewText;
    }

    // Getters
    public int getId() { return id; }
    public int getMovieId() { return movieId; }
    public String getUserEmail() { return userEmail; }
    public String getReviewText() { return reviewText; }
}