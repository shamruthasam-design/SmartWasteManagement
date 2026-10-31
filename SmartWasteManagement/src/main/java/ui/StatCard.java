package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Dashboard statistic card: coloured icon, title and big value. */
public class StatCard extends RoundedPanel {
    private final JLabel valueLabel = new JLabel("0");

    public StatCard(String title, String glyph, Color accent) {
        super(new BorderLayout(14, 0), Theme.CARD, 20);
        setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel icon = new JLabel(glyph, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.alpha(accent, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icon.setForeground(accent);
        icon.setFont(Theme.font(Font.BOLD, 22f));
        icon.setPreferredSize(new Dimension(50, 50));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Theme.MUTED);
        titleLabel.setFont(Theme.font(Font.PLAIN, 13f));
        valueLabel.setForeground(Theme.TEXT);
        valueLabel.setFont(Theme.font(Font.BOLD, 24f));

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);
        text.add(titleLabel);
        text.add(valueLabel);

        add(icon, BorderLayout.WEST);
        add(text, BorderLayout.CENTER);
        enableHover();
    }

    public void setValue(String value) { valueLabel.setText(value); }
}
