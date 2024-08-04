package com.moviex;

import javax.swing.*;
import java.awt.*;

public class LoadingScreen extends JDialog {
    public LoadingScreen(LoginPage loginPage) {
        setTitle("Loading...");
        setSize(200, 100);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel loadingLabel = new JLabel("Please wait...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(loadingLabel, BorderLayout.CENTER);
    }
}
