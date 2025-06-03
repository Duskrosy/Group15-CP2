package com.motorph;

import com.motorph.controllers.DashboardController;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class Dashboard extends JFrame {
    private Font customFont;

    public Dashboard(String employeeID, String role) {
        setTitle("MotorPH Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Set favicon
        try {
            setIconImage(new ImageIcon(getClass().getResource("/img/favicon.png")).getImage());
        } catch (Exception e) {
            // ignore
        }

        // Set custom font for all UI (optional)
        try (InputStream is = getClass().getResourceAsStream("/static/Montserrat-VariableFont_wght.ttf")) {
            customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(15f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            UIManager.put("Label.font", customFont);
            UIManager.put("Button.font", customFont);
            UIManager.put("TabbedPane.font", customFont.deriveFont(14f));
        } catch (Exception e) {
            // ignore, fallback to system font
        }

        JTabbedPane tabbedPane = DashboardController.buildDashboardTabs(employeeID, role, this);
        add(tabbedPane);
        setVisible(true);
    }
}
