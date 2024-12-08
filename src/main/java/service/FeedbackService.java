package service;

import domain.factory.FeedbackFactory;
import domain.model.feedback.*;
import dto.FeedbackDTO;
import exception.FeedbackException;
import repository.FeedbackRepository;
import util.CollectionUtils;
import util.LoggerUtil;

import java.util.List;
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

    public void submit(FeedbackDTO feedbackDTO) {
        Feedback feedback = FeedbackFactory.create(feedbackDTO);

        try {
            // save references to feedback in relevant domains
            switch (feedback.getType()) {
                case CONFERENCE -> serviceMediator.addFeedbackToConference(feedback.getId(), feedbackDTO.getConferenceId());
                case SESSION -> serviceMediator.addFeedbackToSession(feedback.getId(), feedbackDTO.getSessionId());
                case SPEAKER -> serviceMediator.addFeedbackToSpeaker(feedback.getId(), feedbackDTO.getSpeakerId());
            }

            feedbackRepository.save(feedback, feedback.getId());
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to submit feedback '%s': %s %s", feedback.getId(), e.getMessage(), e));

            // implement rollback

            throw new FeedbackException("An unexpected error occurred when submitting feedback. Please try again later.");
        }
    }

    public List<FeedbackDTO> findAllById(Set<String> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("Feedback ids cannot be null.");
        }

        // batch fetch all feedback
        List<Optional<Feedback>> feedbackOptionals = feedbackRepository.findAllById(ids);

        // extract valid feedback objects
        List<Feedback> feedbackList = CollectionUtils.extractValidEntities(feedbackOptionals);

        System.out.println("Feedback found in feedback service: " + feedbackList.size());

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
        FeedbackDTO feedbackDTO = new FeedbackDTO(
                feedback.getAttendeeId(),
                feedback.getAttendeeName(),
                feedback.getRating(),
                feedback.getComment(),
                feedback.getType());
        feedbackDTO.setId(feedbackDTO.getId());

        switch (feedback.getType()) {
            case SESSION -> feedbackDTO.setSessionId(((SessionFeedback) feedback).getSessionId());
            case CONFERENCE -> feedbackDTO.setConferenceId(((ConferenceFeedback) feedback).getConferenceId());
            case SPEAKER -> feedbackDTO.setSpeakerId(((SpeakerFeedback) feedback).getSpeakerId());
        }

        return feedbackDTO;
    }
}
