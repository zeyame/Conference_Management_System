package domain.model.feedback;

public class SessionFeedback extends Feedback {

    private final String sessionId;


    // no-arg constructor for JSON serialization/deserialization
    private SessionFeedback() {
        super(null, null, null, -1, null, null);
        this.sessionId = null;
    }

    public SessionFeedback(String id, String attendeeId, String attendeeName, int rating, String comment, FeedbackType feedbackType, String sessionId) {
        super(id, attendeeId, attendeeName, rating, comment, feedbackType);
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
