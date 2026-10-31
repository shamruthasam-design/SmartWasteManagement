package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** A panel with rounded corners, a fill colour and a thin border (used for all cards). */
public class RoundedPanel extends JPanel {
    private Color fill;
    private final int arc;

    public RoundedPanel(LayoutManager layout, Color fill, int arc) {
        super(layout);
        this.fill = fill;
        this.arc = arc;
        setOpaque(false);
    }

    public void setFill(Color fill) {
        this.fill = fill;
        repaint();
    }

    /** Smooth hover effect: card gets slightly lighter while the mouse is over it. */
    public void enableHover() {
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { setFill(Theme.CARD_HOVER); }
            @Override public void mouseExited(MouseEvent e) { setFill(Theme.CARD); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        g2.setColor(Theme.BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        g2.dispose();
        super.paintComponent(g);
    }
}
