package domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Feedback {

    private final String id;
    private final String attendeeId;
    private final int rating;
    private final String comment;
    @JsonProperty("type")
    private final FeedbackType type;

    // no-arg constructor for JSON serialization/de-serialization
    private Feedback() {
        this.id = null;
        this.attendeeId = null;
        this.rating = 0;
        this.comment = null;
        this.type = null;
    }

    public Feedback(String id, String attendeeId, int rating, String comment, FeedbackType type) {
        this.id = id;
        this.attendeeId = attendeeId;
        this.rating = rating;
        this.comment = comment;
        this.type = type;
    }

    public String getId() {
        return this.id;
    }

    public String getAttendeeId() {
        return this.attendeeId;
    }

    public int getRating() {
        return this.rating;
    }

    public String getComment() {
        return this.comment;
    }

    public FeedbackType getType() {
        return this.type;
    }
}
