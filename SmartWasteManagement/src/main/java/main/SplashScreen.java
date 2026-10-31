package main;

import config.DatabaseInitializer;
import ui.LoginFrame;
import ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Animated splash window that prepares the database, then opens the login screen. */
public class SplashScreen extends JWindow {
    private final JProgressBar bar = new JProgressBar(0, 100);
    private final JLabel status = new JLabel("Starting...");

    public SplashScreen() {
        JPanel p = new JPanel(new BorderLayout(0, 14)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(0x052E2B), getWidth(), getHeight(), new Color(0x0B0F14)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(16, 185, 129, 40));
                g2.fillOval(getWidth() - 220, -80, 320, 320);
                g2.setColor(new Color(20, 184, 166, 28));
                g2.fillOval(-100, getHeight() - 140, 280, 280);
                g2.setColor(Theme.BORDER);
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        p.setBorder(new EmptyBorder(50, 50, 36, 50));

        JLabel icon = new JLabel("\u267B", SwingConstants.CENTER);
        icon.setFont(Theme.font(Font.BOLD, 60f));
        icon.setForeground(Theme.ACCENT);
        JLabel title = new JLabel("Smart Waste Management", SwingConstants.CENTER);
        title.setFont(Theme.font(Font.BOLD, 28f));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Segregation and Collection Management System", SwingConstants.CENTER);
        sub.setForeground(Theme.MUTED);
        JPanel top = new JPanel(new GridLayout(3, 1, 0, 6));
        top.setOpaque(false);
        top.add(icon);
        top.add(title);
        top.add(sub);

        status.setForeground(Theme.MUTED);
        status.setFont(Theme.font(Font.PLAIN, 12f));
        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        bar.setPreferredSize(new Dimension(100, 8));
        bottom.add(bar, BorderLayout.CENTER);
        bottom.add(status, BorderLayout.SOUTH);

        p.add(top, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        setContentPane(p);
        setSize(620, 340);
        setLocationRelativeTo(null);
    }

    /** Shows the splash, initialises the database in the background, then opens the login window. */
    public void start() {
        setVisible(true);
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                step("Loading interface...", 25);
                step("Connecting to MySQL...", 55);
                DatabaseInitializer.initialize();
                step("Preparing dashboard...", 85);
                step("Ready", 100);
                return null;
            }

            private void step(String text, int value) throws InterruptedException {
                publish(text);
                SwingUtilities.invokeLater(() -> bar.setValue(value));
                Thread.sleep(550);
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                status.setText(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                try {
                    get();
                    dispose();
                    new LoginFrame().setVisible(true);
                } catch (Exception ex) {
                    dispose();
                    Throwable cause = ex.getCause() == null ? ex : ex.getCause();
                    JOptionPane.showMessageDialog(null,
                            "Could not connect to MySQL:\n" + cause.getMessage()
                                    + "\n\nCheck that MySQL is running and that the user/password in\n"
                                    + "src/main/resources/db.properties are correct.",
                            "Database error", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        }.execute();
    }
}
