package domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Feedback {

    private final String id;
    private final String attendeeId;
    private final String attendeeName;
    private final String sessionId;
    private final String conferenceId;
    private final int rating;
    private final String comment;
    @JsonProperty("type")
    private final FeedbackType type;

    // no-arg constructor for JSON serialization/de-serialization
    private Feedback() {
        this.id = null;
        this.attendeeId = null;
        this.attendeeName = null;
        this.sessionId = null;
        this.conferenceId = null;
        this.rating = 0;
        this.comment = null;
        this.type = null;
    }

    public Feedback(String id, String attendeeId, String attendeeName, String sessionId, String conferenceId, int rating, String comment, FeedbackType type) {
        this.id = id;
        this.attendeeId = attendeeId;
        this.attendeeName = attendeeName;
        this.sessionId = sessionId;
        this.conferenceId = conferenceId;
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

    public String getAttendeeName() {return this.attendeeName;}

    public String getSessionId() {return this.sessionId;}
    public String getConferenceId() {
        return this.conferenceId;
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

    @Override
    public String toString() {
        return "Feedback{" +
                "id='" + id + '\'' +
                ", attendeeId='" + attendeeId + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", type=" + type +
                '}';
    }
}
