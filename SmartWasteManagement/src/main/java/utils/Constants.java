package utils;

/** Fixed lists used across the application. */
public final class Constants {
    private Constants() { }

    public static final String[] CATEGORIES = {"Biodegradable", "Recyclable", "Non-Recyclable", "Hazardous"};
    public static final String[] WASTE_STATUS = {"Pending", "Segregated"};
    public static final String[] COLLECTION_STATUS = {"Pending", "Assigned", "Completed"};
    public static final String[] STAFF_STATUS = {"Active", "Inactive"};

    /** Returns a new array with 'first' placed in front of 'rest' (used for "All ..." filter options). */
    public static String[] prepend(String first, String[] rest) {
        String[] out = new String[rest.length + 1];
        out[0] = first;
        System.arraycopy(rest, 0, out, 1, rest.length);
        return out;
    }
}
