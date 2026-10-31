package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Toast notifications (success / warning / error) and confirmation dialogs. */
public final class Notify {
    private Notify() { }

    public static void success(Component parent, String message) { toast(parent, message, Theme.ACCENT, "\u2714"); }
    public static void warn(Component parent, String message) { toast(parent, message, Theme.WARNING, "!"); }
    public static void error(Component parent, String message) { toast(parent, message, Theme.DANGER, "\u2716"); }

    /** Shows the message of an exception as an error toast. */
    public static void fail(Component parent, Exception ex) {
        String msg = ex.getMessage();
        error(parent, msg == null ? ex.toString() : msg);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Please confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private static void toast(Component parent, String message, Color color, String glyph) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        if (owner == null || !owner.isShowing()) {
            JOptionPane.showMessageDialog(parent, message);
            return;
        }
        JWindow toast = new JWindow(owner);
        try {
            toast.setBackground(new Color(0, 0, 0, 0));
        } catch (UnsupportedOperationException ignored) {
            // translucent windows not supported - toast will have square corners
        }
        RoundedPanel panel = new RoundedPanel(new BorderLayout(12, 0), new Color(0x1B2430), 16);
        panel.setBorder(new EmptyBorder(14, 18, 14, 22));
        JLabel icon = new JLabel(glyph, SwingConstants.CENTER);
        icon.setForeground(color);
        icon.setFont(Theme.font(Font.BOLD, 18f));
        JLabel text = new JLabel(message);
        text.setForeground(Theme.TEXT);
        text.setFont(Theme.font(Font.PLAIN, 13f));
        panel.add(icon, BorderLayout.WEST);
        panel.add(text, BorderLayout.CENTER);
        toast.setContentPane(panel);
        toast.pack();
        toast.setAlwaysOnTop(true);
        int x = owner.getX() + owner.getWidth() - toast.getWidth() - 32;
        int y = owner.getY() + owner.getHeight() - toast.getHeight() - 56;
        toast.setLocation(x, y);
        toast.setVisible(true);
        Timer timer = new Timer(2800, e -> toast.dispose());
        timer.setRepeats(false);
        timer.start();
    }
}
