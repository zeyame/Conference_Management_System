package dto;

import domain.model.FeedbackType;

public class FeedbackDTO {

    private String id;      // optional
    private final String attendeeId;
    private final String attendeeName;
    private final String sessionId;
    private final String conferenceId;
    private final int rating;
    private final String comment;
    private final FeedbackType type;

    public FeedbackDTO(String attendeeId, String attendeeName, String sessionId, String conferenceId, int rating, String comment, FeedbackType type) {
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

    public void setId(String id) {this.id = id;}

    public String getAttendeeId() {
        return this.attendeeId;
    }

    public String getAttendeeName() {
        return this.attendeeName;
    }

    public String getSessionId() {
        return this.sessionId;
    }

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
}
