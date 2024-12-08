package domain.model.feedback;

public class SpeakerFeedback extends Feedback {

    private final String speakerId;

    // no-arg constructor for JSON serializatio/deserialization
    private SpeakerFeedback() {
        super(null, null, null, -1, null, null);
        this.speakerId = null;
    }

    public SpeakerFeedback(String id, String attendeeId, String attendeeName, int rating, String comment, FeedbackType feedbackType, String speakerId) {
        super(id, attendeeId, attendeeName, rating, comment, feedbackType);
        this.speakerId = speakerId;
    }

    public String getSpeakerId() {
        return speakerId;
    }
}
