package com.moviex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

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

        // Top-right panel for search, home, profile, and feedback buttons
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.setBackground(MovieXTheme.BACKGROUND_COLOR);

        searchField = new JTextField(20);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MovieXTheme.ACCENT_COLOR),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JButton searchButton = new JButton("Search");
        searchButton.setFont(MovieXTheme.NORMAL_FONT);
        searchButton.addActionListener(e -> searchMovies());

        JButton homeButton = new JButton("Home");
        homeButton.setFont(MovieXTheme.NORMAL_FONT);
        homeButton.addActionListener(e -> {
            searchField.setText("");
            moviePanel.removeAll();
            populateMovies();
            moviePanel.revalidate();
            moviePanel.repaint();
        });

        JButton profileButton = new JButton("Profile");
        profileButton.setFont(MovieXTheme.NORMAL_FONT);
        profileButton.addActionListener(e -> showProfile());

        JButton feedbackButton = new JButton("Feedback");
        feedbackButton.setFont(MovieXTheme.NORMAL_FONT);
        feedbackButton.addActionListener(e -> showFeedbackForm());

        topRightPanel.add(searchField);
        topRightPanel.add(searchButton);
        topRightPanel.add(homeButton);
        topRightPanel.add(profileButton);
        topRightPanel.add(feedbackButton);

        topPanel.add(topRightPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        moviePanel = new JPanel(new GridLayout(0, 4, 20, 20));
        moviePanel.setBackground(MovieXTheme.BACKGROUND_COLOR);
        moviePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(moviePanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        initializeMovieList();
        populateMovies();

        setVisible(true);
    }

    private void populateMovies() {
        moviePanel.removeAll();
        JLabel loadingLabel = new JLabel("Loading movies...");
        moviePanel.add(loadingLabel);
        moviePanel.revalidate();
        moviePanel.repaint();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                if (allMovies == null) {
                    allMovies = DatabaseManager.getAllMovies();
                }
                return null;
            }

            @Override
            protected void done() {
                moviePanel.removeAll();
                for (Movie movie : allMovies) {
                    addMovieCard(movie);
                }
                moviePanel.revalidate();
                moviePanel.repaint();
            }
        };
        worker.execute();
    }

    private void addMovieCard(Movie movie) {
        JPanel movieCard = createMovieCard(movie);
        moviePanel.add(movieCard);
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

        // Movie image
        try {
            URL url = new URL(movie.getImagePath());
            Image image = ImageIO.read(url);
            if (image != null) {
                image = image.getScaledInstance(300, 450, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(image));
                panel.add(imageLabel, BorderLayout.WEST);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(MovieXTheme.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(MovieXTheme.TITLE_FONT);
        titleLabel.setForeground(MovieXTheme.ACCENT_COLOR);
        detailsPanel.add(titleLabel, gbc);

        // Description
        JTextArea descriptionArea = new JTextArea(movie.getDescription());
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setOpaque(false);
        descriptionArea.setEditable(false);
        descriptionArea.setForeground(MovieXTheme.TEXT_COLOR);
        detailsPanel.add(descriptionArea, gbc);

        // Current Rating
        JLabel currentRatingLabel = new JLabel(String.format("Current Rating: %.1f/5.0", movie.getRating()));
        currentRatingLabel.setForeground(MovieXTheme.TEXT_COLOR);
        detailsPanel.add(currentRatingLabel, gbc);

        // Add Review Button
        JButton addReviewButton = new JButton("Add Review");
        addReviewButton.addActionListener(e -> addReview(movie));
        detailsPanel.add(addReviewButton, gbc);

        // Upload Proof Button
        JButton uploadProofButton = new JButton("Upload Proof");
        uploadProofButton.addActionListener(e -> uploadProof(movie));
        detailsPanel.add(uploadProofButton, gbc);

        // Reviews
        JPanel reviewsPanel = new JPanel();
        reviewsPanel.setLayout(new BoxLayout(reviewsPanel, BoxLayout.Y_AXIS));
        reviewsPanel.setBackground(MovieXTheme.BACKGROUND_COLOR);

        List<Review> reviews = DatabaseManager.getReviewsForMovie(movie.getId());
        for (Review review : reviews) {
            JPanel reviewPanel = new JPanel(new BorderLayout());
            reviewPanel.setBackground(MovieXTheme.CARD_BACKGROUND);
            reviewPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JLabel reviewerLabel = new JLabel(review.getDisplayName());
            reviewerLabel.setForeground(MovieXTheme.ACCENT_COLOR);
            reviewPanel.add(reviewerLabel, BorderLayout.NORTH);

            JTextArea reviewText = new JTextArea(review.getReviewText());
            reviewText.setWrapStyleWord(true);
            reviewText.setLineWrap(true);
            reviewText.setOpaque(false);
            reviewText.setEditable(false);
            reviewText.setForeground(MovieXTheme.TEXT_COLOR);
            reviewPanel.add(reviewText, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bottomPanel.setOpaque(false);

            JLabel ratingLabel = new JLabel("Rating: " + review.getStarRating());
            ratingLabel.setForeground(MovieXTheme.TEXT_COLOR);
            bottomPanel.add(ratingLabel);

            JButton likeButton = new JButton("Like (" + review.getLikes() + ")");
            likeButton.addActionListener(e -> {
                if (DatabaseManager.likeReview(review.getId())) {
                    review.setLikes(review.getLikes() + 1);
                    likeButton.setText("Like (" + review.getLikes() + ")");
                    if (review.getLikes() >= 5 && !review.hasBadge()) {
                        review.setBadge(true);
                        reviewerLabel.setText(review.getDisplayName() + " [Badge]");
                    }
                }
            });
            bottomPanel.add(likeButton);

            reviewPanel.add(bottomPanel, BorderLayout.SOUTH);

            reviewsPanel.add(reviewPanel);
            reviewsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane reviewsScrollPane = new JScrollPane(reviewsPanel);
        reviewsScrollPane.setPreferredSize(new Dimension(400, 300));
        detailsPanel.add(reviewsScrollPane, gbc);

        panel.add(detailsPanel, BorderLayout.CENTER);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void uploadProof(Movie movie) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Here you would typically upload the file to a server
            // For this example, we'll just show a success message
            JOptionPane.showMessageDialog(this, "Proof uploaded successfully for " + movie.getTitle());
            // Update the database to mark this movie as watched by the user
            DatabaseManager.markMovieAsWatched(userEmail, movie.getId());
        }
    }

    private void addReview(Movie movie) {
        JPanel reviewPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextArea reviewTextArea = new JTextArea(5, 30);
        reviewTextArea.setLineWrap(true);
        reviewTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(reviewTextArea);
        reviewPanel.add(scrollPane, gbc);

        JSlider ratingSlider = new JSlider(1, 5, 3);
        ratingSlider.setMajorTickSpacing(1);
        ratingSlider.setPaintTicks(true);
        ratingSlider.setPaintLabels(true);
        reviewPanel.add(new JLabel("Rating:"), gbc);
        reviewPanel.add(ratingSlider, gbc);

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
        if (allMovies == null) {
            allMovies = DatabaseManager.getAllMovies();
        }
        List<Movie> filteredMovies = allMovies.stream()
                .filter(movie -> movie.getTitle().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        displayMovies(filteredMovies);
    }

    private void showProfile() {
        JDialog profileDialog = new JDialog(this, "User Profile", true);
        profileDialog.setSize(400, 300);
        profileDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(MovieXTheme.BACKGROUND_COLOR);

        JLabel emailLabel = new JLabel("Email: " + userEmail);
        emailLabel.setForeground(MovieXTheme.TEXT_COLOR);
        panel.add(emailLabel);

        // Add additional user information if needed
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> changePassword());
        panel.add(changePasswordButton);

        profileDialog.add(panel);
        profileDialog.setVisible(true);
    }

    private void changePassword() {
        // Implement password change functionality
        JOptionPane.showMessageDialog(this, "Password change functionality not implemented yet.");
    }

    private void showFeedbackForm() {
        JDialog feedbackDialog = new JDialog(this, "Provide Feedback", true);
        feedbackDialog.setSize(400, 300);
        feedbackDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(MovieXTheme.BACKGROUND_COLOR);

        JTextArea feedbackArea = new JTextArea(5, 30);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton submitButton = new JButton("Submit Feedback");
        submitButton.addActionListener(e -> {
            String feedback = feedbackArea.getText();
            if (!feedback.trim().isEmpty()) {
                // Here you would typically send the feedback to a server
                // For this example, we'll just show a success message
                JOptionPane.showMessageDialog(feedbackDialog, "Thank you for your feedback!");
                feedbackDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(feedbackDialog, "Please enter your feedback before submitting.");
            }
        });
        panel.add(submitButton, BorderLayout.SOUTH);

        feedbackDialog.add(panel);
        feedbackDialog.setVisible(true);
    }

    private void initializeMovieList() {
        allMovies = DatabaseManager.getAllMovies();
    }
}
