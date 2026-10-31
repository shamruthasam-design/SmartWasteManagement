package ui;

import model.User;
import service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Modern split-screen login window with admin / staff login and staff registration. */
public class LoginFrame extends JFrame {
    private final AuthService auth = new AuthService();
    private final JComboBox<String> role = new JComboBox<>(new String[]{"Admin", "Staff"});
    private final JTextField username = new JTextField();
    private final JPasswordField password = new JPasswordField();

    public LoginFrame() {
        super("Smart Waste Management - Sign in");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 600);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new GridLayout(1, 2));
        add(buildBrandPanel());
        add(buildFormPanel());
    }

    private JPanel buildBrandPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(0x052E2B), getWidth(), getHeight(), new Color(0x0F766E)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 14));
                g2.fillOval(-120, getHeight() - 260, 380, 380);
                g2.fillOval(getWidth() - 200, -100, 320, 320);
                g2.setColor(new Color(16, 185, 129, 40));
                g2.fillOval(getWidth() / 2 - 60, getHeight() / 2 - 40, 220, 220);
                g2.dispose();
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(70, 50, 40, 40));

        JLabel icon = label("\u267B", 64f, Font.BOLD, Theme.ACCENT);
        JLabel title = label("Smart Waste", 38f, Font.BOLD, Color.WHITE);
        JLabel title2 = label("Management System", 28f, Font.PLAIN, Color.WHITE);
        JLabel tag = label("Segregation \u2022 Collection \u2022 Analytics", 14f, Font.PLAIN, new Color(0xA7F3D0));
        p.add(icon);
        p.add(Box.createVerticalStrut(14));
        p.add(title);
        p.add(title2);
        p.add(Box.createVerticalStrut(14));
        p.add(tag);
        p.add(Box.createVerticalStrut(50));
        for (String f : new String[]{"\u2714  Classify waste into four categories", "\u2714  Assign and track collection staff",
                "\u2714  Live dashboards and reports"}) {
            p.add(label(f, 14f, Font.PLAIN, new Color(0xD1FAE5)));
            p.add(Box.createVerticalStrut(10));
        }
        return p;
    }

    private static JLabel label(String text, float size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.font(style, size));
        l.setForeground(color);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(Theme.CARD);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        int y = 0;

        JLabel head = new JLabel("Welcome back");
        head.setFont(Theme.font(Font.BOLD, 28f));
        head.setForeground(Theme.TEXT);
        c.gridy = y++;
        form.add(head, c);
        JLabel sub = new JLabel("Sign in to continue to your dashboard");
        sub.setForeground(Theme.MUTED);
        c.gridy = y++;
        c.insets = new Insets(4, 0, 22, 0);
        form.add(sub, c);

        username.putClientProperty("JTextField.placeholderText", "Enter username");
        password.putClientProperty("JTextField.placeholderText", "Enter password");
        password.putClientProperty("JPasswordField.showRevealButton", true);
        for (JComponent f : new JComponent[]{role, username, password}) f.setPreferredSize(new Dimension(320, 42));

        y = addField(form, c, y, "Login as", role);
        y = addField(form, c, y, "Username", username);
        y = addField(form, c, y, "Password", password);

        RoundButton signIn = new RoundButton("Sign In", Theme.ACCENT);
        signIn.setPreferredSize(new Dimension(320, 46));
        signIn.addActionListener(e -> doLogin());
        c.gridy = y++;
        c.insets = new Insets(22, 0, 0, 0);
        form.add(signIn, c);

        RoundButton register = new RoundButton("Create staff account", Theme.CARD_HOVER);
        register.addActionListener(e -> openRegister());
        c.gridy = y++;
        c.insets = new Insets(10, 0, 0, 0);
        form.add(register, c);

        JLabel hint = new JLabel("<html><center>Demo accounts<br>admin / admin123 &nbsp;\u2022&nbsp; staff / staff123</center></html>");
        hint.setForeground(Theme.MUTED);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        hint.setFont(Theme.font(Font.PLAIN, 12f));
        c.gridy = y;
        c.insets = new Insets(22, 0, 0, 0);
        form.add(hint, c);

        outer.add(form);
        getRootPane().setDefaultButton(signIn);
        return outer;
    }

    private int addField(JPanel form, GridBagConstraints c, int y, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setForeground(Theme.MUTED);
        l.setFont(Theme.font(Font.PLAIN, 12f));
        c.gridy = y++;
        c.insets = new Insets(10, 0, 0, 0);
        form.add(l, c);
        c.gridy = y++;
        c.insets = new Insets(4, 0, 0, 0);
        form.add(field, c);
        return y;
    }

    private void doLogin() {
        try {
            String r = role.getSelectedIndex() == 0 ? "ADMIN" : "STAFF";
            User user = auth.login(username.getText(), new String(password.getPassword()), r);
            MainFrame main = new MainFrame();
            main.setVisible(true);
            dispose();
            Notify.success(main.getRootPane(), "Welcome, " + user.fullName() + "!");
        } catch (Exception ex) {
            Notify.fail(this, ex);
        }
    }

    private void openRegister() {
        FormDialog d = new FormDialog(this, "Create Staff Account", "Register to access the staff portal");
        JTextField name = new JTextField();
        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();
        JPasswordField confirm = new JPasswordField();
        d.addRow("Full name", name);
        d.addRow("Username (min 4 characters)", user);
        d.addRow("Password (min 6 characters)", pass);
        d.addRow("Confirm password", confirm);
        d.showDialog(() -> {
            try {
                auth.register(name.getText(), user.getText(), new String(pass.getPassword()), new String(confirm.getPassword()));
                username.setText(user.getText().trim());
                role.setSelectedIndex(1);
                Notify.success(this, "Account created. You can sign in now.");
                return true;
            } catch (Exception ex) {
                Notify.fail(d, ex);
                return false;
            }
        });
    }
}
