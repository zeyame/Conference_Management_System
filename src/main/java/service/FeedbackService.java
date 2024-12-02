package service;

import domain.model.Feedback;
import dto.FeedbackDTO;
import exception.FeedbackException;
import repository.FeedbackRepository;
import util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FeedbackService {
    private ServiceMediator serviceMediator;
    private final FeedbackRepository feedbackRepository;
    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public void setServiceMediator(ServiceMediator serviceMediator) {
        this.serviceMediator = serviceMediator;
    }

    public List<FeedbackDTO> findAllById(Set<String> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("Feedback ids cannot be null.");
        }

        // batch fetch all feedback
        List<Optional<Feedback>> feedbackOptionals = feedbackRepository.findAllById(ids);

        // extract valid feedback objects
        List<Feedback> feedbackList = CollectionUtils.extractValidEntities(feedbackOptionals);

        return feedbackList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(String id ) {
        boolean isDeleted = feedbackRepository.deleteById(id);
        if (!isDeleted) {
            throw new FeedbackException(String.format("Failed to delete feedback with id '%s'.", id));
        }
    }

    public void deleteAllById(Set<String> ids) {
        ids.forEach(this::deleteById);
    }

    private FeedbackDTO mapToDTO(Feedback feedback) {
        return new FeedbackDTO(
                feedback.getId(),
                feedback.getAttendeeId(),
                feedback.getAttendeeName(),
                feedback.getRating(),
                feedback.getComment(),
                feedback.getType()
        );
    }
}
