package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/** Draws category / status values as coloured rounded "pills" inside a table cell. */
public class BadgeRenderer extends DefaultTableCellRenderer {
    private Color badge;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                   boolean focus, int row, int col) {
        super.getTableCellRendererComponent(table, value, selected, false, row, col);
        String text = value == null ? "" : value.toString();
        setText(text);
        badge = Theme.badgeColor(text);
        setBorder(new EmptyBorder(0, 12, 0, 12));
        setFont(Theme.font(Font.BOLD, 12f));
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (badge == null) {
            super.paintComponent(g);
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        int w = fm.stringWidth(text) + 22;
        int h = fm.getHeight() + 8;
        int x = 10;
        int y = (getHeight() - h) / 2;
        g2.setColor(Theme.alpha(badge, 44));
        g2.fillRoundRect(x, y, w, h, h, h);
        g2.setColor(badge);
        g2.drawString(text, x + 11, y + (h - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }
}
