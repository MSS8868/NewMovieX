package com.moviex;

import javax.swing.*;
import java.awt.*;

public class MovieXTheme {
    public static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    public static final Color TEXT_COLOR = new Color(33, 33, 33);
    public static final Color ACCENT_COLOR = new Color(0, 123, 255);
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    public static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 14);

    public static void applyTheme() {
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        UIManager.put("Button.background", ACCENT_COLOR);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Label.foreground", TEXT_COLOR);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", TEXT_COLOR);
        UIManager.put("PasswordField.background", Color.WHITE);
        UIManager.put("PasswordField.foreground", TEXT_COLOR);
    }
}