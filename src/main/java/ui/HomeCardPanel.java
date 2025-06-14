package com.motorph.ui;

import com.motorph.services.TimeTrackerService;
import com.motorph.utils.FontHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HomeCardPanel extends JPanel {
    private final TimeTrackerService tracker;

    private JLabel greetingLabel;
    private JLabel clockLabel;
    private JButton timeInButton;
    private JButton breakButton;
    private JLabel lastTimeLabel;
    private JLabel timeInOutStatus;
    private ClockToggleButton clockToggleBtn;

    public HomeCardPanel(String employeeID, String fullName, TimeTrackerService tracker) {
        this.tracker = tracker;
        setOpaque(false);
        setPreferredSize(new Dimension(1100, 370));
        setLayout(null);

        // Greeting
        greetingLabel = new JLabel();
        greetingLabel.setFont(FontHelper.black(38f));
        greetingLabel.setForeground(new Color(86, 37, 102));
        greetingLabel.setBounds(36, 48, 900, 48);
        greetingLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(greetingLabel);

        // Clock
        clockLabel = new JLabel();
        clockLabel.setFont(FontHelper.semiBold(34f));
        clockLabel.setForeground(new Color(120, 83, 176));
        clockLabel.setBounds(36, 110, 220, 38);
        add(clockLabel);

        // Toggle Button
        clockToggleBtn = new ClockToggleButton();
        clockToggleBtn.setBounds(265, 118, 44, 28);
        add(clockToggleBtn);

        // Time In/Out Button
        timeInButton = new JButton();
        timeInButton.setFont(FontHelper.semiBold(26f));
        timeInButton.setBounds(36, 200, 340, 66);
        timeInButton.setFocusPainted(false);
        timeInButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        timeInButton.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        timeInButton.setForeground(Color.WHITE);
        timeInButton.setBackground(new Color(156, 110, 255));
        timeInButton.setOpaque(true);
        timeInButton.addActionListener(e -> {
            if (tracker.canTimeInNow()) {
                tracker.timeIn();
                tracker.setOnBreak(false);
                updateUIContent();
            } else if (tracker.canTimeOutNow()) {
                tracker.timeOutAndLog();
                tracker.setOnBreak(false);
                updateUIContent();
            }
        });
        add(timeInButton);

        // Lunch Break
        breakButton = new JButton("Lunch Break");
        breakButton.setFont(FontHelper.semiBold(20f));
        breakButton.setBounds(36, 290, 280, 52);
        breakButton.setFocusPainted(false);
        breakButton.setBackground(new Color(255, 191, 69));
        breakButton.setForeground(new Color(66, 28, 115));
        breakButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        breakButton.addActionListener(e -> {
            tracker.setOnBreak(!tracker.isOnBreak());
            updateUIContent();
        });
        add(breakButton);

        // Last Time in/out
        lastTimeLabel = new JLabel("", SwingConstants.LEFT);
        lastTimeLabel.setFont(FontHelper.medium(18f));
        lastTimeLabel.setForeground(new Color(100, 75, 140));
        lastTimeLabel.setBounds(400, 110, 340, 56);
        lastTimeLabel.setVerticalAlignment(SwingConstants.TOP);
        add(lastTimeLabel);

        // Status Text
        timeInOutStatus = new JLabel("", SwingConstants.LEFT);
        timeInOutStatus.setFont(FontHelper.semiBold(18f));
        timeInOutStatus.setForeground(new Color(202, 41, 41));
        timeInOutStatus.setBounds(400, 210, 520, 80);
        timeInOutStatus.setVerticalAlignment(SwingConstants.TOP);
        add(timeInOutStatus);

        // Timer
        new Timer(1000, e -> updateClockAndGreeting()).start();
        updateUIContent();
    }

    private void updateClockAndGreeting() {
        greetingLabel.setText(tracker.getGreeting());
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(clockToggleBtn.is24h() ? "HH:mm:ss" : "hh:mm:ss a");
        clockLabel.setText(LocalDateTime.now().format(timeFormat));
    }

    private void updateUIContent() {
        TimeTrackerService.StatusInfo status = tracker.getStatusInfo();

        if (tracker.canTimeInNow()) {
            timeInButton.setText("Time In");
            timeInButton.setBackground(new Color(156, 110, 255));
            timeInButton.setEnabled(true);
        } else if (tracker.canTimeOutNow()) {
            timeInButton.setText("Time Out");
            timeInButton.setBackground(new Color(255, 124, 124));
            timeInButton.setEnabled(true);
        } else {
            timeInButton.setText("Time In");
            timeInButton.setBackground(new Color(211, 211, 211));
            timeInButton.setEnabled(false);
        }

        // Last time in/out
        StringBuilder sb = new StringBuilder("<html>");
        if (!status.timeIn.isEmpty()) sb.append("<b>Time In:</b> ").append(status.timeIn).append("<br>");
        if (!status.timeOut.isEmpty()) sb.append("<b>Time Out:</b> ").append(status.timeOut).append("<br>");
        sb.append("</html>");
        lastTimeLabel.setText(sb.toString());

        // Status
        timeInOutStatus.setText("<html>" + status.status + "<br>" + status.subStatus + "</html>");

        // Lunch Break button logic
        boolean showBreak = tracker.canTimeOutNow();
        breakButton.setVisible(showBreak);

        if (tracker.isOnBreak()) {
            breakButton.setText("End Lunch Break");
            breakButton.setBackground(new Color(255, 124, 124));
            breakButton.setForeground(Color.WHITE);
        } else {
            breakButton.setText("Lunch Break");
            breakButton.setBackground(new Color(255, 191, 69));
            breakButton.setForeground(new Color(66, 28, 115));
        }
    }

    // --- ClockToggleButton inner class (local only, move out if reused) ---
    private static class ClockToggleButton extends JComponent {
        private boolean _is24h = false;
        public ClockToggleButton() {
            setPreferredSize(new Dimension(44, 28));
            setToolTipText("Switch between 12/24-hour clock");
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    _is24h = !_is24h;
                    repaint();
                }
            });
        }
        public boolean is24h() { return _is24h; }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(220, 205, 245));
            g2.fillRoundRect(0, 4, 44, 20, 20, 20);
            if (_is24h) {
                g2.setColor(new Color(106, 29, 120));
                g2.fillOval(24, 0, 24, 24);
                g2.setColor(Color.WHITE);
                g2.setFont(FontHelper.bold(10f));
                g2.drawString("24h", 29, 16);
            } else {
                g2.setColor(new Color(203, 159, 251));
                g2.fillOval(0, 0, 24, 24);
                g2.setColor(Color.WHITE);
                g2.setFont(FontHelper.bold(10f));
                g2.drawString("12h", 5, 16);
            }
            g2.dispose();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(255, 255, 255, 240));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
        g2.setColor(new Color(186, 141, 232, 40));
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 32, 32);
        g2.dispose();
        super.paintComponent(g);
    }
}
