package utils;

import model.User;

/** Holds the currently logged-in user (simple session management). */
public final class Session {
    private static User current;

    private Session() { }

    public static void login(User user) { current = user; }
    public static void logout() { current = null; }
    public static User getUser() { return current; }
    public static boolean isLoggedIn() { return current != null; }
    public static boolean isAdmin() { return current != null && "ADMIN".equals(current.role()); }
}
