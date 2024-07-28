package com.moviex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class HomePage extends JFrame {
    private String userEmail;
    private JPanel moviePanel;
    private List<Movie> allMovies;
    private JTextField searchField;

    public HomePage(String userEmail) {
        this.userEmail = userEmail;
        setTitle("MovieX - Home");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(MovieXTheme.BACKGROUND_COLOR);

        // Top panel for welcome message and search
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(MovieXTheme.BACKGROUND_COLOR);
        JLabel welcomeLabel = new JLabel("Welcome to MovieX, " + userEmail);
        welcomeLabel.setFont(MovieXTheme.TITLE_FONT);
        welcomeLabel.setForeground(MovieXTheme.TEXT_COLOR);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(MovieXTheme.BACKGROUND_COLOR);
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchMovies());
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        topPanel.add(searchPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        moviePanel = new JPanel(new GridLayout(0, 3, 20, 20));
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
            noMoviesLabel.setFont(MovieXTheme.NORMAL_FONT);
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
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(MovieXTheme.ACCENT_COLOR, 2));

        // Try to load the image
        ImageIcon imageIcon = new ImageIcon(movie.getImagePath());
        if (imageIcon.getIconWidth() == -1) {
            // If image loading fails, use a colored panel
            JPanel colorPanel = new JPanel();
            colorPanel.setBackground(MovieXTheme.ACCENT_COLOR);
            colorPanel.setPreferredSize(new Dimension(200, 300));
            card.add(colorPanel, BorderLayout.CENTER);
        } else {
            // If image loads successfully, scale it and add to the card
            Image image = imageIcon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            card.add(imageLabel, BorderLayout.CENTER);
        }

        JLabel titleLabel = new JLabel(movie.getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(MovieXTheme.NORMAL_FONT);
        titleLabel.setForeground(MovieXTheme.TEXT_COLOR);
        card.add(titleLabel, BorderLayout.SOUTH);

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
        dialog.setSize(600, 800);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(MovieXTheme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Try to load the image
        ImageIcon imageIcon = new ImageIcon(movie.getImagePath());
        if (imageIcon.getIconWidth() == -1) {
            // If image loading fails, use a colored panel
            JPanel colorPanel = new JPanel();
            colorPanel.setBackground(MovieXTheme.ACCENT_COLOR);
            colorPanel.setPreferredSize(new Dimension(200, 300));
            panel.add(colorPanel, BorderLayout.NORTH);
        } else {
            // If image loads successfully, scale it and add to the panel
            Image image = imageIcon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            panel.add(imageLabel, BorderLayout.NORTH);
        }

        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        detailsPanel.setBackground(MovieXTheme.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(MovieXTheme.TITLE_FONT);
        titleLabel.setForeground(MovieXTheme.TEXT_COLOR);
        detailsPanel.add(titleLabel, BorderLayout.NORTH);

        JTextArea descriptionArea = new JTextArea(movie.getDescription());
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setOpaque(false);
        descriptionArea.setFont(MovieXTheme.NORMAL_FONT);
        descriptionArea.setForeground(MovieXTheme.TEXT_COLOR);
        descriptionArea.setEditable(false);
        detailsPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        JButton addReviewButton = new JButton("Add Review");
        addReviewButton.addActionListener(e -> addReview(movie));
        detailsPanel.add(addReviewButton, BorderLayout.SOUTH);

        panel.add(detailsPanel, BorderLayout.CENTER);

        // Add reviews panel
        JPanel reviewsPanel = new JPanel(new BorderLayout());
        reviewsPanel.setBackground(MovieXTheme.BACKGROUND_COLOR);
        JLabel reviewsLabel = new JLabel("Reviews");
        reviewsLabel.setFont(MovieXTheme.TITLE_FONT);
        reviewsLabel.setForeground(MovieXTheme.TEXT_COLOR);
        reviewsPanel.add(reviewsLabel, BorderLayout.NORTH);

        JTextArea reviewsArea = new JTextArea();
        reviewsArea.setWrapStyleWord(true);
        reviewsArea.setLineWrap(true);
        reviewsArea.setOpaque(false);
        reviewsArea.setFont(MovieXTheme.NORMAL_FONT);
        reviewsArea.setForeground(MovieXTheme.TEXT_COLOR);
        reviewsArea.setEditable(false);

        List<Review> reviews = DatabaseManager.getReviewsForMovie(movie.getId());
        for (Review review : reviews) {
            reviewsArea.append(review.getUserEmail() + ": " + review.getReviewText() + "\n\n");
        }

        JScrollPane reviewsScrollPane = new JScrollPane(reviewsArea);
        reviewsScrollPane.setPreferredSize(new Dimension(500, 200));
        reviewsPanel.add(reviewsScrollPane, BorderLayout.CENTER);

        panel.add(reviewsPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addReview(Movie movie) {
        String review = JOptionPane.showInputDialog(this, "Enter your review for " + movie.getTitle());
        if (review != null && !review.trim().isEmpty()) {
            boolean success = DatabaseManager.addReview(movie.getId(), userEmail, review);
            if (success) {
                JOptionPane.showMessageDialog(this, "Review added successfully!");
                showMovieDetails(movie); // Refresh the movie details dialog to show the new review
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add review. Please try again.");
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