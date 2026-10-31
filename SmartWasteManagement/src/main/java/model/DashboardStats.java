package model;

/** All numbers displayed on the dashboard cards. */
public record DashboardStats(double collected, double segregated, double recyclable, double organic,
                             double hazardous, double nonRecyclable,
                             int pending, int assigned, int completed) { }
