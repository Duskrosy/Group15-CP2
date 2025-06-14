package com.motorph.ui;

import com.motorph.models.Employee;
import com.motorph.repositories.EmployeeRepository;
import com.motorph.utils.FontHelper;
import com.motorph.utils.CurrencyFormatter;
import com.motorph.utils.ClipboardUtil;
import com.motorph.ui.components.RoundedImageLabel;

import javax.swing.*;
import java.awt.*;

public class MyDetailsPanel extends JPanel {
    public MyDetailsPanel(String employeeID) {
        setOpaque(true);
        setBackground(new Color(247, 247, 252)); // light bg

        Employee emp = EmployeeRepository.findById(employeeID);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 16, 16, 16);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // ---- Profile Card (top left, takes 2 columns width)
        JPanel profileCard = createProfileCard(emp);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        add(profileCard, gbc);

        // ---- Salary Info Card (top right)
        JPanel salaryCard = createSalaryCard(emp);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        add(salaryCard, gbc);

        // ---- Basic Details Card (bottom left)
        JPanel basicDetailsCard = createBasicDetailsCard(emp);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        add(basicDetailsCard, gbc);

        // ---- Government IDs Card (bottom right)
        JPanel govIDCard = createGovernmentIDCard(emp);
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        add(govIDCard, gbc);
    }

	private JPanel createProfileCard(Employee emp) {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);   // Change color
		panel.setOpaque(true);              // Background test
		panel.setLayout(new GridBagLayout());
		panel.setPreferredSize(new Dimension(660, 220));
		panel.setBorder(new RoundedBorder(32));

		GridBagConstraints gbc = new GridBagConstraints();

        // Profile Picture
        JLabel img;
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/profile_placeholder.png"));
            img = new RoundedImageLabel(icon.getImage(), 130);
        } catch (Exception e) {
            img = new JLabel("👤");
            img.setFont(FontHelper.black(72f));
        }
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.insets = new Insets(0, 12, 0, 24);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(img, gbc);

        // Name
        JLabel name = new JLabel(emp != null ? emp.getFullName() : "Full Name");
        name.setFont(FontHelper.black(38f));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.insets = new Insets(18, 0, 4, 0);
        panel.add(name, gbc);

        // Position
        JLabel position = new JLabel(emp != null ? emp.position : "Position");
        position.setFont(FontHelper.medium(28f));
        position.setForeground(new Color(0x10C650));
        gbc.gridy = 1;
        gbc.insets = new Insets(2, 0, 2, 0);
        panel.add(position, gbc);

        // Employee ID (italic, blue)
        JLabel empid = new JLabel(emp != null ? emp.id : "10000");
        empid.setFont(FontHelper.lightItalic(22f));
        empid.setForeground(new Color(0x2266FF));
        gbc.gridy = 2;
        gbc.insets = new Insets(2, 0, 8, 0);
        panel.add(empid, gbc);

        return panel;
    }

    // ---- Salary Card
    private JPanel createSalaryCard(Employee emp) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
		outer.setOpaque(true);
        outer.setPreferredSize(new Dimension(660, 220));
        outer.setBorder(new RoundedBorder(32));

        // Header
        JPanel header = createCardHeader("Salary Info", true);
        outer.add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 24));

        // By default, hidden
        content.setVisible(false);

        content.add(salaryRow("Basic Salary:", emp != null ? CurrencyFormatter.format(emp.basicSalary) : ""));
        content.add(salaryRow("Rice Subsidy:", emp != null ? CurrencyFormatter.format(emp.riceSubsidy) : ""));
        content.add(salaryRow("Phone Allowance:", emp != null ? CurrencyFormatter.format(emp.phoneAllowance) : ""));
        content.add(salaryRow("Clothing Allowance:", emp != null ? CurrencyFormatter.format(emp.clothingAllowance) : ""));
        content.add(salaryRow("Gross Semi-Monthly:", emp != null ? CurrencyFormatter.format(emp.grossRate) : ""));
        content.add(salaryRow("Hourly Rate:", emp != null ? CurrencyFormatter.format(emp.hourlyRate) : ""));

        outer.add(content, BorderLayout.CENTER);

        // Toggle
        JButton toggleBtn = (JButton) header.getComponent(header.getComponentCount() - 1);
        toggleBtn.addActionListener(e -> {
            content.setVisible(!content.isVisible());
            toggleBtn.setIcon(new ImageIcon(getClass().getResource(content.isVisible() ? "/img/icon_eye.png" : "/img/icon_eye_off.png")));
        });

        return outer;
    }

    // ---- Basic Details Card
    private JPanel createBasicDetailsCard(Employee emp) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
		outer.setOpaque(true);
        outer.setPreferredSize(new Dimension(660, 310));
        outer.setBorder(new RoundedBorder(32));

        JPanel header = createCardHeader("Basic Details", false);
        outer.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 24));

        content.add(detailRow("Position:", emp != null ? emp.position : ""));
        content.add(detailRow("Employee ID:", emp != null ? emp.id : ""));
        content.add(detailRow("Status:", emp != null ? emp.status : ""));
        content.add(detailRow("Birthday:", emp != null ? emp.birthday : ""));
        content.add(detailRow("Phone #:", emp != null ? emp.phoneNumber : ""));
        content.add(detailRow("Address:", emp != null ? emp.address : ""));
        content.add(detailRow("Employment Status:", emp != null ? emp.status : ""));
        content.add(detailRow("Immediate Supervisor:", emp != null ? emp.supervisor : ""));

        outer.add(content, BorderLayout.CENTER);

        return outer;
    }

    // ---- Government IDs Card
    private JPanel createGovernmentIDCard(Employee emp) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
		outer.setOpaque(true);
        outer.setPreferredSize(new Dimension(660, 310));
        outer.setBorder(new RoundedBorder(32));

        JPanel header = createCardHeader("Government IDs", false);
        outer.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 24));

        content.add(detailRowWithCopy("SSS:", emp != null ? emp.sss : ""));
        content.add(detailRowWithCopy("TIN:", emp != null ? emp.tin : ""));
        content.add(detailRowWithCopy("PhilHealth:", emp != null ? emp.philhealth : ""));
        content.add(detailRowWithCopy("Pagibig:", emp != null ? emp.pagibig : ""));

        outer.add(content, BorderLayout.CENTER);

        return outer;
    }

    // --- Card Header with blue background and rounded top ---
    private JPanel createCardHeader(String title, boolean hasToggle) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(12, 61, 164));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 30, 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(100, 50));

        JLabel label = new JLabel(title);
        label.setFont(FontHelper.bold(28f));
        label.setForeground(Color.WHITE);
        panel.add(label);

        if (hasToggle) {
            JButton eyeBtn = new JButton(new ImageIcon(getClass().getResource("/img/icon_eye_off.png")));
            eyeBtn.setContentAreaFilled(false);
            eyeBtn.setBorderPainted(false);
            eyeBtn.setFocusPainted(false);
            eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            eyeBtn.setToolTipText("Show/hide salary info");
            eyeBtn.setPreferredSize(new Dimension(40, 36));
            panel.add(eyeBtn);
        }

        return panel;
    }

    // --- Helper rows ---
    private JPanel detailRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        row.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(FontHelper.semiBold(18f));
        JLabel v = new JLabel(value == null ? "" : value);
        v.setFont(FontHelper.medium(18f));
        row.add(l);
        row.add(v);
        return row;
    }
    private JPanel salaryRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        row.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(FontHelper.semiBold(17f));
        JLabel v = new JLabel(value == null ? "" : value);
        v.setFont(FontHelper.medium(17f));
        row.add(l);
        row.add(v);
        return row;
    }
    private JPanel detailRowWithCopy(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        row.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(FontHelper.semiBold(18f));
        JLabel v = new JLabel(value == null ? "" : value);
        v.setFont(FontHelper.medium(18f));
        row.add(l);
        row.add(v);

        if (value != null && !value.trim().isEmpty()) {
            JButton copyBtn = new JButton("📋");
            copyBtn.setFocusable(false);
            copyBtn.setBorder(BorderFactory.createEmptyBorder());
            copyBtn.setToolTipText("Copy to clipboard");
            copyBtn.addActionListener(e -> ClipboardUtil.copyToClipboard(value));
            row.add(copyBtn);
        }
        return row;
    }

    // --- Rounded card border helper ---
    static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private int radius;
        public RoundedBorder(int radius) {
            this.radius = radius;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(220, 224, 232));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius, radius);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius/2, radius/2, radius/2); }
        @Override
        public Insets getBorderInsets(Component c, Insets insets) { return getBorderInsets(c); }
    }
}
