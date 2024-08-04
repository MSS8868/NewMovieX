package com.moviex;

import javax.swing.*;

public class MovieX {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MovieXTheme.applyTheme();
            DatabaseManager.initDatabase();
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
        });
    }
}
