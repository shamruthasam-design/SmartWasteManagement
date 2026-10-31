package model;

/** An application login account. role is ADMIN or STAFF. */
public record User(int id, String username, String passwordHash, String fullName, String role) { }
