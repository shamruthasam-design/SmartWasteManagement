package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Sidebar navigation button with hover and active (selected) states. */
public class NavButton extends JButton {
    private boolean active;

    public NavButton(String glyph, String text) {
        super(glyph + "    " + text);
        setHorizontalAlignment(SwingConstants.LEFT);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setFont(Theme.font(Font.PLAIN, 14f));
        setForeground(Theme.MUTED);
        setBorder(new EmptyBorder(13, 20, 13, 14));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void setActive(boolean active) {
        this.active = active;
        setForeground(active ? Color.WHITE : Theme.MUTED);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (active) {
            g2.setColor(Theme.alpha(Theme.ACCENT, 38));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.setColor(Theme.ACCENT);
            g2.fillRoundRect(0, 10, 4, getHeight() - 20, 4, 4);
        } else if (getModel().isRollover()) {
            g2.setColor(new Color(255, 255, 255, 16));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
