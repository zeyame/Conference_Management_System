package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.feedback.Feedback;

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

    @Override
    protected TypeReference<Map<String, Feedback>> getTypeReference() {
        return new TypeReference<Map<String, Feedback>>() {};
    }
}
