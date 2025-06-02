package com.motorph;

/**
 * @author Gavril pogi
 * if this breaks, it was always like this
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {
    private JTextField employeeIDField;
    private JPasswordField passwordField;

    public LoginScreen() {
        setTitle("MotorPH Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Employee ID:"));
        employeeIDField = new JTextField();
        panel.add(employeeIDField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);

        add(new JLabel("MotorPH Employee App", SwingConstants.CENTER));
        add(panel);
        add(buttonPanel);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String employeeID = employeeIDField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                String role = Authenticator.authenticate(employeeID, password);
                if (role != null) {
                    dispose(); // close login window
                    new Dashboard(employeeID, role); // open dashboard with ID and role
                } else {
                    JOptionPane.showMessageDialog(LoginScreen.this,
                            "Invalid credentials", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginScreen();
    }
}