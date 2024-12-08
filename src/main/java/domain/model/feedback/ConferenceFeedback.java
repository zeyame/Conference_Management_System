package domain.model.feedback;

public class ConferenceFeedback extends Feedback {

    private final String conferenceId;

    // no-arg constructor for JSON serializatio/deserialization
    private ConferenceFeedback() {
        super(null, null, null, -1, null, null);
        this.conferenceId = null;
    }

    public ConferenceFeedback(String id, String attendeeId, String attendeeName, int rating, String comment, FeedbackType feedbackType, String conferenceId) {
        super(id, attendeeId, attendeeName, rating, comment, feedbackType);
        this.conferenceId = conferenceId;
    }

    public String getConferenceId() {
        return conferenceId;
    }
}
