package com.moviex;

public class Movie {
    private int id;
    private String title;
    private String description;
    private String imagePath;
    private double rating;

    public Movie(int id, String title, String description, String imagePath, double rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.rating = rating;
    }

    // Existing getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImagePath() { return imagePath; }

    // New getter and setter for rating
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
}