package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Flat rounded button with hover and pressed effects. */
public class RoundButton extends JButton {
    private final Color base;

    public RoundButton(String text, Color base) {
        super(text);
        this.base = base;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setForeground(Color.WHITE);
        setFont(getFont().deriveFont(Font.BOLD, 13f));
        setBorder(new EmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color c = base;
        if (!isEnabled()) c = Theme.BORDER;
        else if (getModel().isPressed()) c = base.darker();
        else if (getModel().isRollover()) c = Theme.lighten(base, 28);
        g2.setColor(c);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
        g2.dispose();
        super.paintComponent(g);
    }
}
