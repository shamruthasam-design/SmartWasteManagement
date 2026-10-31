package ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/** Colours, fonts and look-and-feel setup for the premium dark theme. */
public final class Theme {
    public static final Color BG = new Color(0x0B0F14);
    public static final Color SIDEBAR = new Color(0x0F172A);
    public static final Color CARD = new Color(0x161C24);
    public static final Color CARD_HOVER = new Color(0x1F2937);
    public static final Color INPUT = new Color(0x0F141B);
    public static final Color BORDER = new Color(0x263040);
    public static final Color ACCENT = new Color(0x10B981);   // emerald
    public static final Color TEAL = new Color(0x14B8A6);
    public static final Color TEXT = new Color(0xF1F5F9);
    public static final Color MUTED = new Color(0x94A3B8);
    public static final Color WARNING = new Color(0xF59E0B);
    public static final Color DANGER = new Color(0xEF4444);
    public static final Color INFO = new Color(0x3B82F6);
    public static final Color PURPLE = new Color(0xA78BFA);

    private Theme() { }

    /** Must be called once before any window is created. */
    public static void install() {
        FlatLaf.setPreferredFontFamily("Segoe UI");
        Map<String, String> extra = new HashMap<>();
        extra.put("@accentColor", "#10B981");
        extra.put("@background", "#0B0F14");
        FlatLaf.setGlobalExtraDefaults(extra);
        FlatDarkLaf.setup();

        UIManager.put("Component.arc", 14);
        UIManager.put("Button.arc", 14);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollBar.track", CARD);
        UIManager.put("ScrollBar.thumb", new Color(0x334155));
        for (String key : new String[]{"TextField", "PasswordField", "ComboBox", "Spinner", "FormattedTextField"}) {
            UIManager.put(key + ".background", INPUT);
        }
        UIManager.put("Table.background", CARD);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.rowHeight", 42);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.gridColor", new Color(0x1F2937));
        UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
        UIManager.put("Table.selectionBackground", new Color(0x134E3A));
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("TableHeader.background", new Color(0x1B2430));
        UIManager.put("TableHeader.foreground", MUTED);
        UIManager.put("TableHeader.bottomSeparatorColor", BORDER);
        UIManager.put("TableHeader.height", 42);
        UIManager.put("OptionPane.background", CARD);
        UIManager.put("Panel.background", BG);
    }

    public static Font font(int style, float size) {
        Font base = UIManager.getFont("Label.font");
        if (base == null) base = new Font("SansSerif", Font.PLAIN, 13);
        return base.deriveFont(style, size);
    }

    /** Colour used for category and status "pills". Returns null for normal text. */
    public static Color badgeColor(String value) {
        if (value == null) return null;
        return switch (value) {
            case "Biodegradable", "Segregated", "Completed", "Active" -> ACCENT;
            case "Recyclable" -> TEAL;
            case "Non-Recyclable" -> PURPLE;
            case "Hazardous" -> DANGER;
            case "Pending" -> WARNING;
            case "Assigned" -> INFO;
            case "Inactive", "Unassigned" -> MUTED;
            default -> null;
        };
    }

    public static Color categoryColor(String category) {
        Color c = badgeColor(category);
        return c == null ? MUTED : c;
    }

    public static Color lighten(Color c, int amount) {
        return new Color(Math.min(255, c.getRed() + amount), Math.min(255, c.getGreen() + amount),
                Math.min(255, c.getBlue() + amount));
    }

    public static Color alpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }
}