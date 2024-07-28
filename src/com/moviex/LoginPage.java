package com.moviex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginPage() {
        setTitle("MovieX - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(MovieXTheme.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("MovieX");
        titleLabel.setFont(MovieXTheme.TITLE_FONT);
        titleLabel.setForeground(MovieXTheme.ACCENT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(MovieXTheme.TEXT_COLOR);
        emailField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(MovieXTheme.TEXT_COLOR);
        passwordField = new JPasswordField(20);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        gbc.gridy = 4;
        panel.add(registerButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                if (DatabaseManager.authenticateUser(email, password)) {
                    JOptionPane.showMessageDialog(LoginPage.this, "Login successful!");
                    new HomePage(email).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginPage.this, "Invalid email or password.");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                if (DatabaseManager.registerUser(email, password)) {
                    JOptionPane.showMessageDialog(LoginPage.this, "Registration successful! You can now log in.");
                } else {
                    JOptionPane.showMessageDialog(LoginPage.this, "Registration failed. Email might already be in use.");
                }
            }
        });

        add(panel);
    }
}