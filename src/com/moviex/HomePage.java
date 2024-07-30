package com.moviex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;
import java.net.URL;
import javax.imageio.ImageIO;
import java.io.IOException;

public class HomePage extends JFrame {
    private String userEmail;
    private JPanel moviePanel;
    private List<Movie> allMovies;
    private JTextField searchField;

    public HomePage(String userEmail) {
        this.userEmail = userEmail;
        setTitle("MovieX");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(MovieXTheme.BACKGROUND_COLOR);

        // Top panel for logo, welcome message, and search
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(MovieXTheme.BACKGROUND_COLOR);

        // Logo
        ImageIcon logoIcon = new ImageIcon("path/to/your/logo.png"); // Replace with your logo path
        Image scaledLogo = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        topPanel.add(logoLabel, BorderLayout.WEST);

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to MovieX, " + userEmail);
        welcomeLabel.setFont(MovieXTheme.TITLE_FONT);
        welcomeLabel.setForeground(MovieXTheme.ACCENT_COLOR);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(MovieXTheme.BACKGROUND_COLOR);
        searchField = new JTextField(20);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MovieXTheme.ACCENT_COLOR),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        JButton searchButton = new JButton("Search");
        searchButton.setFont(MovieXTheme.NORMAL_FONT);
        searchButton.addActionListener(e -> searchMovies());
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        topPanel.add(searchPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        moviePanel = new JPanel(new GridLayout(0, 4, 20, 20));
        moviePanel.setBackground(MovieXTheme.BACKGROUND_COLOR);
        moviePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(moviePanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        populateMovies();

        setVisible(true);
    }

    private void populateMovies() {
        allMovies = DatabaseManager.getAllMovies();
        displayMovies(allMovies);
    }

    private void displayMovies(List<Movie> movies) {
        moviePanel.removeAll();

        if (movies.isEmpty()) {
            JLabel noMoviesLabel = new JLabel("No movies available.", SwingConstants.CENTER);
            noMoviesLabel.setFont(MovieXTheme.SUBTITLE_FONT);
            noMoviesLabel.setForeground(MovieXTheme.TEXT_COLOR);
            moviePanel.add(noMoviesLabel);
        } else {
            for (Movie movie : movies) {
                JPanel movieCard = createMovieCard(movie);
                moviePanel.add(movieCard);
            }
        }
        moviePanel.revalidate();
        moviePanel.repaint();
    }

    private JPanel createMovieCard(Movie movie) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(MovieXTheme.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createLineBorder(MovieXTheme.ACCENT_COLOR, 2));

        try {
            URL url = new URL(movie.getImagePath());
            Image image = ImageIO.read(url);
            if (image != null) {
                image = image.getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(image));
                card.add(imageLabel, BorderLayout.CENTER);
            } else {
                throw new IOException("Failed to load image");
            }
        } catch (IOException e) {
            JPanel colorPanel = new JPanel();
            colorPanel.setBackground(MovieXTheme.ACCENT_COLOR);
            colorPanel.setPreferredSize(new Dimension(200, 300));
            card.add(colorPanel, BorderLayout.CENTER);
        }

        JLabel titleLabel = new JLabel(movie.getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(MovieXTheme.NORMAL_FONT);
        titleLabel.setForeground(MovieXTheme.TEXT_COLOR);
        card.add(titleLabel, BorderLayout.SOUTH);

        // Add rating display
        JLabel ratingLabel = new JLabel(String.format("Rating: %.1f/5.0", movie.getRating()), SwingConstants.CENTER);
        ratingLabel.setFont(MovieXTheme.NORMAL_FONT);
        ratingLabel.setForeground(MovieXTheme.ACCENT_COLOR);
        card.add(ratingLabel, BorderLayout.NORTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showMovieDetails(movie);
            }
        });

        return card;
    }

    private void showMovieDetails(Movie movie) {
        JDialog dialog = new JDialog(this, movie.getTitle(), true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(MovieXTheme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        try {
            URL url = new URL(movie.getImagePath());
            Image image = ImageIO.read(url);
            if (image != null) {
                image = image.getScaledInstance(300, 450, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(image));
                panel.add(imageLabel, BorderLayout.WEST);
            } else {
                throw new IOException("Failed to load image");
            }
        } catch (IOException e) {
            JPanel colorPanel = new JPanel();
            colorPanel.setBackground(MovieXTheme.ACCENT_COLOR);
            colorPanel.setPreferredSize(new Dimension(300, 450));
            panel.add(colorPanel, BorderLayout.WEST);
        }

        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        detailsPanel.setBackground(MovieXTheme.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(MovieXTheme.TITLE_FONT);
        titleLabel.setForeground(MovieXTheme.ACCENT_COLOR);
        detailsPanel.add(titleLabel, BorderLayout.NORTH);

        JTextArea descriptionArea = new JTextArea(movie.getDescription());
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setOpaque(false);
        descriptionArea.setFont(MovieXTheme.NORMAL_FONT);
        descriptionArea.setForeground(MovieXTheme.TEXT_COLOR);
        descriptionArea.setEditable(false);
        detailsPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        // Add rating display
        JLabel currentRatingLabel = new JLabel(String.format("Current Rating: %.1f/5.0", movie.getRating()));
        currentRatingLabel.setFont(MovieXTheme.NORMAL_FONT);
        currentRatingLabel.setForeground(MovieXTheme.TEXT_COLOR);
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ratingPanel.setBackground(new Color(30, 30, 30));
        ratingPanel.add(currentRatingLabel);
        detailsPanel.add(ratingPanel, BorderLayout.CENTER);

        JButton addReviewButton = new JButton("Add Review");
        addReviewButton.setFont(MovieXTheme.NORMAL_FONT);
        addReviewButton.addActionListener(e -> addReview(movie));
        detailsPanel.add(addReviewButton, BorderLayout.SOUTH);

        panel.add(detailsPanel, BorderLayout.CENTER);

        // Add reviews panel
        JPanel reviewsPanel = new JPanel(new BorderLayout());
        reviewsPanel.setBackground(MovieXTheme.BACKGROUND_COLOR);
        JLabel reviewsLabel = new JLabel("Reviews");
        reviewsLabel.setFont(MovieXTheme.SUBTITLE_FONT);
        reviewsLabel.setBackground(new Color(30, 30, 30));
        reviewsLabel.setForeground(MovieXTheme.ACCENT_COLOR);
        reviewsPanel.add(reviewsLabel, BorderLayout.NORTH);

        JTextArea reviewsArea = new JTextArea();
        reviewsArea.setWrapStyleWord(true);
        reviewsArea.setLineWrap(true);
        reviewsArea.setOpaque(false);
        reviewsArea.setFont(MovieXTheme.NORMAL_FONT);
        reviewsArea.setBackground(new Color(30, 30, 30));
        reviewsArea.setForeground(MovieXTheme.TEXT_COLOR);
        reviewsArea.setEditable(false);

        List<Review> reviews = DatabaseManager.getReviewsForMovie(movie.getId());
        for (Review review : reviews) {
            reviewsArea.append(review.getUserEmail() + " - " + review.getStarRating() + "\n");
            reviewsArea.append(review.getReviewText() + "\n");
            reviewsArea.append("Posted on: " + review.getFormattedDateTime() + "\n\n");
        }

        JScrollPane reviewsScrollPane = new JScrollPane(reviewsArea);
        reviewsScrollPane.setPreferredSize(new Dimension(500, 200));
        reviewsPanel.add(reviewsScrollPane, BorderLayout.CENTER);

        panel.add(reviewsPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addReview(Movie movie) {
        JPanel reviewPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextArea reviewTextArea = new JTextArea(5, 20);
        JSlider ratingSlider = new JSlider(1, 5, 3);
        ratingSlider.setMajorTickSpacing(1);
        ratingSlider.setPaintTicks(true);
        ratingSlider.setPaintLabels(true);
        reviewTextArea.setForeground(MovieXTheme.TEXT_COLOR);
        reviewPanel.add(new JLabel("Your Review:"));
        reviewPanel.add(reviewTextArea);
        reviewPanel.add(new JLabel("Rating:"));
        reviewPanel.add(ratingSlider);

        int result = JOptionPane.showConfirmDialog(this, reviewPanel, "Add Review for " + movie.getTitle(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String reviewText = reviewTextArea.getText();
            int rating = ratingSlider.getValue();
            if (!reviewText.trim().isEmpty()) {
                boolean success = DatabaseManager.addReview(movie.getId(), userEmail, reviewText, rating);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Review added successfully!");
                    showMovieDetails(movie); // Refresh the movie details dialog
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add review. Please try again.");
                }
            }
        }
    }

    private void searchMovies() {
        String searchTerm = searchField.getText().toLowerCase();
        List<Movie> filteredMovies = allMovies.stream()
                .filter(movie -> movie.getTitle().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        displayMovies(filteredMovies);
    }
}
