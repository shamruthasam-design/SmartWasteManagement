package service;

import dao.ActivityDAO;
import dao.UserDAO;
import model.User;
import utils.AppException;
import utils.PasswordUtil;
import utils.Session;
import utils.Validator;

/** Login, registration and logout logic. */
public class AuthService {
    private final UserDAO userDAO = new UserDAO();
    private final ActivityDAO activity = new ActivityDAO();

    /** role must be "ADMIN" or "STAFF". */
    public User login(String username, String password, String role) {
        Validator.required(username, "Username");
        Validator.required(password, "Password");
        User user = userDAO.findByUsername(username.trim());
        if (user == null || !PasswordUtil.verify(password, user.passwordHash())) {
            throw new AppException("Invalid username or password.");
        }
        if (!user.role().equals(role)) {
            throw new AppException("This account is not a " + role.toLowerCase() + " account. Choose the correct login type.");
        }
        Session.login(user);
        activity.log(user.fullName() + " signed in");
        return user;
    }

    /** Creates a STAFF account. */
    public void register(String fullName, String username, String password, String confirm) {
        String name = Validator.required(fullName, "Full name");
        String user = Validator.required(username, "Username");
        Validator.required(password, "Password");
        if (user.length() < 4) throw new AppException("Username must be at least 4 characters.");
        if (password.length() < 6) throw new AppException("Password must be at least 6 characters.");
        if (!password.equals(confirm)) throw new AppException("Passwords do not match.");
        if (userDAO.findByUsername(user) != null) throw new AppException("That username is already taken.");
        userDAO.insert(new User(0, user, PasswordUtil.hash(password), name, "STAFF"));
        activity.log("New staff account registered: " + name);
    }

    public void logout() {
        if (Session.isLoggedIn()) activity.log(Session.getUser().fullName() + " signed out");
        Session.logout();
    }
}
