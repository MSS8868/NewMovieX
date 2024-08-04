package com.moviex;

import javax.swing.*;
import java.awt.*;

public class MovieXTheme {
    public static final Color BACKGROUND_COLOR = new Color(18, 18, 18);
    public static final Color TEXT_COLOR = new Color(220, 180, 166);
    public static final Color ACCENT_COLOR = new Color(229, 9, 20);
    public static final Color CARD_BACKGROUND = new Color(40, 40, 40);
    public static final Font TITLE_FONT = new Font("Montserrat", Font.BOLD, 32);
    public static final Font SUBTITLE_FONT = new Font("Montserrat", Font.BOLD, 24);
    public static final Font NORMAL_FONT = new Font("Roboto", Font.PLAIN, 16);

    public static void applyTheme() {
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        UIManager.put("Button.background", ACCENT_COLOR);
        UIManager.put("Button.foreground", TEXT_COLOR);
        UIManager.put("Label.foreground", TEXT_COLOR);
        UIManager.put("TextField.background", CARD_BACKGROUND);
        UIManager.put("TextField.foreground", TEXT_COLOR);
        UIManager.put("PasswordField.background", CARD_BACKGROUND);
        UIManager.put("PasswordField.foreground", TEXT_COLOR);
        UIManager.put("TextArea.background", CARD_BACKGROUND);
        UIManager.put("TextArea.foreground", TEXT_COLOR);
        UIManager.put("ScrollPane.background", BACKGROUND_COLOR);
    }
}