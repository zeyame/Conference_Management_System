package service;

import domain.model.Feedback;
import dto.FeedbackDTO;
import repository.FeedbackRepository;
import util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FeedbackService {
    private final UserService userService;
    private final FeedbackRepository feedbackRepository;
    public FeedbackService(UserService userService, FeedbackRepository feedbackRepository) {
        this.userService = userService;
        this.feedbackRepository = feedbackRepository;
    }

    public List<FeedbackDTO> findAllById(Set<String> ids) {
        // batch fetch all feedback
        List<Optional<Feedback>> feedbackOptionals = feedbackRepository.findAllById(ids);

        // extract valid feedback objects
        List<Feedback> feedbackList = CollectionUtils.extractValidEntities(feedbackOptionals);

        // get attendee ids of feedback list
        Set<String> attendeeIds = feedbackList.stream()
                .map(Feedback::getAttendeeId)
                .collect(Collectors.toSet());

        // attendee ids to names
        Map<String, String> attendeeIdsToNamesMap = userService.findNamesByIds(attendeeIds);

        System.out.println("Attendee ids to name map size: " + attendeeIdsToNamesMap.size());

        return feedbackList.stream()
                .map(feedback -> mapToDTO(feedback, attendeeIdsToNamesMap.get(feedback.getAttendeeId())))
                .collect(Collectors.toList());
    }

    private FeedbackDTO mapToDTO(Feedback feedback, String attendeeName) {
        System.out.println(attendeeName);
        return new FeedbackDTO(
                feedback.getId(),
                feedback.getAttendeeId(),
                attendeeName,
                feedback.getRating(),
                feedback.getComment(),
                feedback.getType()
        );
    }
}
