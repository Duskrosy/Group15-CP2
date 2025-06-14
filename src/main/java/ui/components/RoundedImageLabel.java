package com.motorph.ui.components;

import javax.swing.*;
import java.awt.*;

public class RoundedImageLabel extends JLabel {
    private Image image;
    private int diameter;

    public RoundedImageLabel(Image image, int diameter) {
        this.image = image.getScaledInstance(diameter, diameter, Image.SCALE_SMOOTH);
        this.diameter = diameter;
        setPreferredSize(new Dimension(diameter, diameter));
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (image != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Shape circle = new java.awt.geom.Ellipse2D.Double(0, 0, diameter, diameter);
            g2.setClip(circle);
            g2.drawImage(image, 0, 0, diameter, diameter, this);
            g2.setClip(null);
            g2.setColor(new Color(180, 180, 180, 70)); // test border
            g2.drawOval(0, 0, diameter - 1, diameter - 1);
            g2.dispose();
        } else {
            super.paintComponent(g);
        }
    }
}
