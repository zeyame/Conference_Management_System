package dto;

import domain.model.feedback.FeedbackType;

public class FeedbackDTO {

    private String id;      // optional
    private String sessionId;       // optional
    private String conferenceId;    // optional
    private String speakerId;       // optional
    private final String attendeeId;
    private final String attendeeName;
    private final int rating;
    private final String comment;
    private final FeedbackType type;

    public FeedbackDTO(String attendeeId, String attendeeName, int rating, String comment, FeedbackType type) {
        this.attendeeId = attendeeId;
        this.attendeeName = attendeeName;
        this.rating = rating;
        this.comment = comment;
        this.type = type;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {this.id = id;}

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    public String getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(String speakerId) {
        this.speakerId = speakerId;
    }

    public String getAttendeeId() {
        return this.attendeeId;
    }

    public String getAttendeeName() {
        return this.attendeeName;
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
