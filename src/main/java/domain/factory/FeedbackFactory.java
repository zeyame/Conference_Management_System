package domain.factory;

import domain.model.feedback.*;
import dto.FeedbackDTO;
import util.IdGenerator;

public class FeedbackFactory {

    // private constructor to suppress instantiability
    private FeedbackFactory() {}

    public static Feedback create(FeedbackDTO feedbackDTO) {
        String id = feedbackDTO.getId() != null ? feedbackDTO.getId() : IdGenerator.generateUniqueId();
        String attendeeId = feedbackDTO.getAttendeeId();
        String attendeeName = feedbackDTO.getAttendeeName();
        int rating = feedbackDTO.getRating();
        String comment = feedbackDTO.getComment();
        FeedbackType feedbackType = feedbackDTO.getType();

        return switch (feedbackDTO.getType()) {
            case SESSION -> new SessionFeedback(id, attendeeId, attendeeName, rating, comment, feedbackType, feedbackDTO.getSessionId());
            case CONFERENCE -> new ConferenceFeedback(id, attendeeId, attendeeName, rating, comment, feedbackType, feedbackDTO.getConferenceId());
            case SPEAKER -> new SpeakerFeedback(id, attendeeId, attendeeName, rating, comment, feedbackType, feedbackDTO.getSpeakerId());
        };
    }
}
