package domain.model.feedback;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import util.LoggerUtil;

public enum FeedbackType {
    CONFERENCE("Conference"),
    SESSION("Session"),
    SPEAKER("Speaker");

    private final String displayName;
    FeedbackType(String displayName) {
        this.displayName = displayName;
    }

    @JsonCreator
    public static FeedbackType fromString(String displayName) {
        for (FeedbackType type: FeedbackType.values()) {
            if (type.displayName.equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        LoggerUtil.getInstance().logError("Display name passed to fromString of FeedbackType enum does not match any existing enum constant.");
        throw new IllegalArgumentException("Invalid display name passed for feedback type.");
    }

    @JsonValue
    public String getDisplayName() {
        return this.displayName;
    }
}
