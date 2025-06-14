package com.motorph.ui.components;

import javax.swing.*;
import java.awt.*;

public class MiniCardPanel extends JPanel {
    public MiniCardPanel(JComponent content, int width, int height) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(width, height));

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Soft shadow
                g2.setColor(new Color(60, 60, 120, 25));
                g2.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 12, 32, 32);
                // Card
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        card.add(content, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
    }
}