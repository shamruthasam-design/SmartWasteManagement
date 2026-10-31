package model;

/** A collection staff member. openTasks = assigned requests that are not completed yet. */
public record Staff(int id, String name, String phone, String email, String area, String status, int openTasks) {
    /** Placeholder shown in combo boxes when no staff is selected. */
    public static final Staff UNASSIGNED = new Staff(0, "-- Unassigned --", "", "", "", "Active", 0);

    @Override
    public String toString() { return name; }
}
