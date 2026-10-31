package ui;

import model.User;
import service.AuthService;
import utils.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/** Main application window: left sidebar, top bar and a card layout holding all pages. */
public class MainFrame extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final Map<String, NavButton> navButtons = new LinkedHashMap<>();
    private final Map<String, Refreshable> pages = new HashMap<>();
    private final JLabel pageTitle = new JLabel();
    private final JLabel clock = new JLabel();
    private final Timer clockTimer;

    public MainFrame() {
        super("Smart Waste Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 700));
        setSize(1360, 820);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        content.setOpaque(false);
        register("Dashboard", new DashboardPanel());
        register("Waste Segregation", new WastePanel());
        register("Collections", new CollectionPanel());
        if (Session.isAdmin()) register("Staff", new StaffPanel());
        register("Tracking", new TrackingPanel());
        register("Reports", new ReportsPanel());

        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.add(buildTopBar(), BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);
        add(buildSidebar(), BorderLayout.WEST);
        add(main, BorderLayout.CENTER);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy   HH:mm:ss");
        clockTimer = new Timer(1000, e -> clock.setText(LocalDateTime.now().format(fmt)));
        clockTimer.setInitialDelay(0);
        clockTimer.start();

        navigate("Dashboard");
    }

    private <T extends JComponent & Refreshable> void register(String key, T page) {
        content.add(page, key);
        pages.put(key, page);
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setBackground(Theme.SIDEBAR);
        side.setPreferredSize(new Dimension(250, 0));
        side.setBorder(new MatteBorder(0, 0, 0, 1, Theme.BORDER));

        JLabel logo = new JLabel("\u267B  SmartWaste");
        logo.setFont(Theme.font(Font.BOLD, 22f));
        logo.setForeground(Theme.ACCENT);
        JLabel tag = new JLabel("Management Suite");
        tag.setForeground(Theme.MUTED);
        tag.setFont(Theme.font(Font.PLAIN, 12f));
        JPanel brand = new JPanel(new GridLayout(2, 1, 0, 2));
        brand.setOpaque(false);
        brand.setBorder(new EmptyBorder(26, 24, 26, 16));
        brand.add(logo);
        brand.add(tag);

        JPanel nav = new JPanel(new GridLayout(0, 1, 0, 6));
        nav.setOpaque(false);
        nav.setBorder(new EmptyBorder(6, 14, 6, 14));
        addNav(nav, "Dashboard", "\u25A6", "Dashboard");
        addNav(nav, "Waste Segregation", "\u267B", "Waste Segregation");
        addNav(nav, "Collections", "\u2611", "Waste Collection");
        if (Session.isAdmin()) addNav(nav, "Staff", "\u263A", "Staff Management");
        addNav(nav, "Tracking", "\u25CE", "Collection Tracking");
        addNav(nav, "Reports", "\u25A4", "Reports & Analytics");
        JPanel navWrap = new JPanel(new BorderLayout());
        navWrap.setOpaque(false);
        navWrap.add(nav, BorderLayout.NORTH);

        NavButton logout = new NavButton("\u279C", "Logout");
        logout.setForeground(Theme.DANGER);
        logout.addActionListener(e -> logout());
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(10, 14, 20, 14));
        bottom.add(logout, BorderLayout.CENTER);

        side.add(brand, BorderLayout.NORTH);
        side.add(navWrap, BorderLayout.CENTER);
        side.add(bottom, BorderLayout.SOUTH);
        return side;
    }

    private void addNav(JPanel nav, String key, String glyph, String label) {
        NavButton b = new NavButton(glyph, label);
        b.addActionListener(e -> navigate(key));
        navButtons.put(key, b);
        nav.add(b);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.SIDEBAR);
        bar.setBorder(new javax.swing.border.CompoundBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER),
                new EmptyBorder(0, 28, 0, 24)));
        bar.setPreferredSize(new Dimension(0, 68));

        pageTitle.setFont(Theme.font(Font.BOLD, 18f));
        pageTitle.setForeground(Theme.TEXT);
        clock.setForeground(Theme.MUTED);

        User u = Session.getUser();
        JLabel avatar = new JLabel(u.fullName().substring(0, 1).toUpperCase(), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, Theme.ACCENT, getWidth(), getHeight(), Theme.TEAL));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setForeground(Color.WHITE);
        avatar.setFont(Theme.font(Font.BOLD, 16f));
        avatar.setPreferredSize(new Dimension(42, 42));

        JLabel name = new JLabel(u.fullName());
        name.setForeground(Theme.TEXT);
        name.setFont(Theme.font(Font.BOLD, 13f));
        JLabel role = new JLabel(u.role().equals("ADMIN") ? "Administrator" : "Staff");
        role.setForeground(Theme.MUTED);
        role.setFont(Theme.font(Font.PLAIN, 11f));
        JPanel who = new JPanel(new GridLayout(2, 1));
        who.setOpaque(false);
        who.add(name);
        who.add(role);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 13));
        right.setOpaque(false);
        right.add(clock);
        right.add(avatar);
        right.add(who);

        bar.add(pageTitle, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private void navigate(String key) {
        cards.show(content, key);
        pages.get(key).refresh();
        NavButton active = navButtons.get(key);
        pageTitle.setText(active == null ? key : active.getText().replaceAll("^\\S+\\s+", ""));
        navButtons.forEach((k, b) -> b.setActive(k.equals(key)));
    }

    private void logout() {
        if (!Notify.confirm(this, "Do you want to sign out?")) return;
        clockTimer.stop();
        new AuthService().logout();
        LoginFrame login = new LoginFrame();
        login.setVisible(true);
        dispose();
    }
}
