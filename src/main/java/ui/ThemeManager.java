package com.motorph.ui;
/* I honestly dont know how to do this.
*/

import javax.swing.*;
import java.awt.*;

public class ThemeManager {
    public enum Theme {
        LIGHT, DARK
    }

    private static Theme currentTheme = Theme.LIGHT;

    public static void setTheme(Theme theme) {
        currentTheme = theme;
        if (theme == Theme.DARK) {
            UIManager.put("Panel.background", new Color(37, 28, 50));
            UIManager.put("Panel.foreground", Color.WHITE);
            UIManager.put("Label.foreground", Color.WHITE);
            UIManager.put("Button.background", new Color(72, 43, 98));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("TextField.background", new Color(52, 37, 70));
            UIManager.put("TextField.foreground", Color.WHITE);
            UIManager.put("PasswordField.background", new Color(52, 37, 70));
            UIManager.put("PasswordField.foreground", Color.WHITE);
            UIManager.put("ScrollPane.background", new Color(37, 28, 50));
            UIManager.put("Menu.background", new Color(37, 28, 50));
            UIManager.put("Menu.foreground", Color.WHITE);
        } else {
            UIManager.put("Panel.background", Color.WHITE);
            UIManager.put("Panel.foreground", Color.BLACK);
            UIManager.put("Label.foreground", new Color(66, 28, 115));
            UIManager.put("Button.background", new Color(200, 125, 255));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextField.foreground", Color.BLACK);
            UIManager.put("PasswordField.background", Color.WHITE);
            UIManager.put("PasswordField.foreground", Color.BLACK);
            UIManager.put("ScrollPane.background", Color.WHITE);
            UIManager.put("Menu.background", Color.WHITE);
            UIManager.put("Menu.foreground", Color.BLACK);
        }
        // Update all existing open windows
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
        }
    }

    public static Theme getCurrentTheme() {
        return currentTheme;
    }
}
