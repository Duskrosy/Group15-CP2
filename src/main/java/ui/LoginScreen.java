package com.motorph;

import com.motorph.utils.FontHelper;
import com.motorph.ui.DashboardFrame;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {
    private JTextField employeeIDField;
    private JPasswordField passwordField;

    public LoginScreen() {
        setTitle("MotorPH Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // Favicon
        try {
            setIconImage(new ImageIcon(getClass().getResource("/img/favicon.png")).getImage());
        } catch (Exception e) {
            System.err.println("Favicon not found");
        }

        // Background with your pastel image
        JPanel backgroundPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bgImage = new ImageIcon(getClass().getResource("/img/543cf06c-c94b-48e1-89b7-93e1507ab0b9.png")).getImage();
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    Graphics2D g2 = (Graphics2D) g;
                    GradientPaint gp = new GradientPaint(0, 0, new Color(239, 181, 211), getWidth(), getHeight(), new Color(180, 194, 231));
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        backgroundPanel.setLayout(null);

        // Left panel for logo (centered)
        JPanel leftPanel = new JPanel(null);
        leftPanel.setOpaque(false);
        leftPanel.setBounds(0, 0, 500, 500);

        JLabel logoLabel = new JLabel();
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/img/logo.png"));
            logoLabel.setIcon(logoIcon);
            int logoHeight = logoIcon.getIconHeight();
            int logoWidth = logoIcon.getIconWidth();
            int x = (500 - logoWidth) / 2;
            int y = (500 - logoHeight) / 2;
            logoLabel.setBounds(Math.max(0, x), Math.max(0, y), logoWidth, logoHeight);
        } catch (Exception e) {
            logoLabel.setText("Logo");
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            logoLabel.setBounds(80, 130, 240, 240);
            System.err.println("Logo image not found.");
        }
        leftPanel.add(logoLabel);

        // Right panel for card
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Soft shadow
                g2.setColor(new Color(180, 125, 187, 45));
                g2.fillRoundRect(7, 12, getWidth() - 14, getHeight() - 14, 32, 32);
                // White card
                g2.setColor(new Color(255,255,255,240));
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 32, 32);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(null);
        card.setOpaque(false);
        card.setBounds(600, 90, 400, 320);

        // Title
        JLabel welcome = new JLabel("Login into your account", SwingConstants.CENTER);
        welcome.setFont(FontHelper.bold(26f));
        welcome.setForeground(new Color(85, 47, 75));
        welcome.setBounds(0, 15, 400, 36);
        card.add(welcome);

        // Employee ID field
        employeeIDField = new JTextField();
        employeeIDField.setFont(FontHelper.regular(16f));
        employeeIDField.setBorder(BorderFactory.createTitledBorder("Employee ID"));
        employeeIDField.setBounds(75, 65, 250, 44);
        card.add(employeeIDField);

        // Password field
        passwordField = new JPasswordField();
        passwordField.setFont(FontHelper.regular(16f));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        passwordField.setBounds(75, 120, 250, 44);
        card.add(passwordField);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(FontHelper.semiBold(18f));
        loginButton.setBackground(new Color(200, 125, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        loginButton.setOpaque(true);
        loginButton.setBounds(75, 180, 250, 44);
        card.add(loginButton);

        // Signup/help label
        JLabel signupLabel = new JLabel(
            "<html><div style='text-align:center;'>" +
            "<span style='color:#291F33;font-weight:bold;'>Don’t have an account?</span> " +
            "<span style='color:#C87DFF;font-weight:bold;'>Contact IT.</span></div></html>"
        );
        signupLabel.setFont(FontHelper.medium(14f));
        signupLabel.setBounds(75, 235, 250, 32);
        card.add(signupLabel);

        // Copyright directly below card
        JLabel copyright = new JLabel("\u00a9 2025 Arkaneia. All Rights Reserved.", SwingConstants.CENTER);
        copyright.setFont(FontHelper.regular(12f));
        copyright.setForeground(new Color(90,80,90));
        copyright.setBounds(600, 420, 400, 20); // place below card

        // Add panels to background
        backgroundPanel.add(leftPanel);
        backgroundPanel.add(card);
        backgroundPanel.add(copyright);

        setContentPane(backgroundPanel);

        // --- LOGIN LOGIC ---
        loginButton.addActionListener(e -> doLogin(loginButton));
        passwordField.addActionListener(e -> loginButton.doClick());
        employeeIDField.addActionListener(e -> loginButton.doClick());

        setVisible(true);
    }

//Placebo effect kunware nag loload I saw this in a UX book
    private void doLogin(JButton loginButton) {
        String employeeID = employeeIDField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        loginButton.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            String role = Authenticator.authenticate(employeeID, password);
            loginButton.setEnabled(true);
            if (role != null) {
                // Show loading dialog
                JDialog loadingDialog = createLoadingDialog();
                new Thread(() -> {
                    try {
                        int delay = 500 + (int)(Math.random() * 700);
                        Thread.sleep(delay);
                    } catch (InterruptedException ignored) {}
                    SwingUtilities.invokeLater(() -> {
                        loadingDialog.dispose();
                        dispose();

                        String fullName = employeeID;
                        try {
                            com.motorph.models.Employee emp = com.motorph.repositories.EmployeeRepository.findById(employeeID);
                            if (emp != null) {
                                if (emp.firstName != null && emp.lastName != null) {
                                    fullName = emp.firstName + " " + emp.lastName;
                                } else if (emp.firstName != null) {
                                    fullName = emp.firstName;
                                }
                            }
                        } catch (Exception ex) {
                            System.err.println("Could not get full name: " + ex.getMessage());
                        }
                        boolean isAdmin = "admin".equalsIgnoreCase(role);

                        // SHOW ONLY DASHBOARDFRAME
                        DashboardFrame dashboard = new DashboardFrame(employeeID, fullName, isAdmin);
                        dashboard.setLocationRelativeTo(null);
                        dashboard.setVisible(true);
                    });
                }).start();
                loadingDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JDialog createLoadingDialog() {
        JDialog dialog = new JDialog(this, "Logging In...", true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        JLabel label = new JLabel("Logging in, please wait...", JLabel.CENTER);
        label.setFont(FontHelper.medium(14f));
        panel.add(label, BorderLayout.NORTH);
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        panel.add(bar, BorderLayout.CENTER);
        dialog.setUndecorated(true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}
