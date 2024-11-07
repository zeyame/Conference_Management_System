package domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import util.LoggerUtil;

public enum UserRole {
    ORGANIZER("Organizer"),
    ATTENDEE("Attendee"),
    SPEAKER("Speaker");

    private final String displayName;
    UserRole(String displayName) {
        this.displayName = displayName;
    }

    @JsonCreator
    public static UserRole fromString(String displayName) {
        for (UserRole role: UserRole.values()) {
            if (role.displayName.equalsIgnoreCase(displayName)) {
                return role;
            }
        }
        LoggerUtil.getInstance().logError("Display name passed to fromString of UserRole enum does not match any existing enum constant.");
        throw new IllegalArgumentException("Invalid display name passed for user role.");
    }
    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}
