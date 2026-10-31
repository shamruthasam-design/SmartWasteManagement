package utils;

import java.util.regex.Pattern;

/** Reusable form validation helpers. They throw AppException with a friendly message. */
public final class Validator {
    private Validator() { }

    public static String required(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new AppException(field + " is required.");
        }
        return value.trim();
    }

    public static double positive(String value, String field) {
        String text = required(value, field);
        double number;
        try {
            number = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            throw new AppException(field + " must be a valid number.");
        }
        if (number <= 0 || number > 100000) {
            throw new AppException(field + " must be greater than 0 and at most 100000.");
        }
        return number;
    }

    public static String optionalEmail(String value) {
        if (value == null || value.isBlank()) return "";
        if (!Pattern.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$", value.trim())) {
            throw new AppException("Please enter a valid email address.");
        }
        return value.trim();
    }

    public static String phone(String value) {
        String text = required(value, "Phone");
        if (!Pattern.matches("^[0-9+\\-\\s]{7,15}$", text)) {
            throw new AppException("Phone must contain 7 to 15 digits.");
        }
        return text;
    }
}
