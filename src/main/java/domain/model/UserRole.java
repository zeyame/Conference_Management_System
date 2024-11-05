package domain.model;

public enum UserRole {
    ORGANIZER("Organizer"),
    ATTENDEE("Attendee"),
    SPEAKER("Speaker");

    private final String displayName;
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    @Override
    public String toString() {
        return displayName;
    }
}
