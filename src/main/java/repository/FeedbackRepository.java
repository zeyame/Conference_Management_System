package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.feedback.*;

import java.util.*;

public class FeedbackRepository extends BaseRepository<Feedback> {

    private static FeedbackRepository instance;

    private FeedbackRepository() {
        super("src/main/resources/data/feedback.json");
    }

    public static synchronized FeedbackRepository getInstance() {
        if (instance == null) {
            instance = new FeedbackRepository();
        }
        return instance;
    }

    public List<ConferenceFeedback> findAllConferenceFeedback() {
        return cache.values()
                .stream()
                .filter(feedback -> feedback.getType() == FeedbackType.CONFERENCE)
                .map(feedback -> (ConferenceFeedback) feedback)
                .toList();
    }

    public List<SessionFeedback> findAllSessionFeedback() {
        return cache.values()
                .stream()
                .filter(feedback -> feedback.getType() == FeedbackType.SESSION)
                .map(feedback -> (SessionFeedback) feedback)
                .toList();
    }

    public List<SpeakerFeedback> findAllSpeakerFeedback() {
        return cache.values()
                .stream()
                .filter(feedback -> feedback.getType() == FeedbackType.SPEAKER)
                .map(feedback -> (SpeakerFeedback) feedback)
                .toList();
    }

    @Override
    protected TypeReference<Map<String, Feedback>> getTypeReference() {
        return new TypeReference<Map<String, Feedback>>() {};
    }
}
