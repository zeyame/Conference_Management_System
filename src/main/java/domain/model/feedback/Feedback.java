package domain.model.feedback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SessionFeedback.class, name = "Session"),
        @JsonSubTypes.Type(value = ConferenceFeedback.class, name = "Conference"),
        @JsonSubTypes.Type(value = SpeakerFeedback.class, name = "Speaker")
})
public abstract class Feedback {

    private final String id;
    private final String attendeeId;
    private final String attendeeName;
    private final int rating;
    private final String comment;
    @JsonProperty("type")
    private final FeedbackType type;

    // no-arg constructor for JSON serialization/de-serialization
    private Feedback() {
        this.id = null;
        this.attendeeId = null;
        this.attendeeName = null;
        this.rating = 0;
        this.comment = null;
        this.type = null;
    }

    public Feedback(String id, String attendeeId, String attendeeName, int rating, String comment, FeedbackType type) {
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

    public String getAttendeeName() {return this.attendeeName;}

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
