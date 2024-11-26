package dto;

import domain.model.FeedbackType;

public class FeedbackDTO {

    private final String id;
    private final String attendeeId;
    private final String attendeeName;
    private final int rating;
    private final String comment;
    private final FeedbackType type;

    public FeedbackDTO(String id, String attendeeId, String attendeeName, int rating, String comment, FeedbackType type) {
        this.id = id;
        this.attendeeId = attendeeId;
        this.attendeeName = attendeeName;
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
