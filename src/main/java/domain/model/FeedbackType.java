package domain.model;

public enum FeedbackType {
    CONFERENCE("Conference"),
    SESSION("Session"),
    SPEAKER("Speaker");

    private final String displayName;
    FeedbackType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
