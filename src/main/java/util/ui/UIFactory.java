package util.ui;

import controller.AttendeeController;
import controller.OrganizerController;
import controller.SpeakerController;
import dto.UserDTO;
import repository.ConferenceRepository;
import repository.FeedbackRepository;
import repository.SessionRepository;
import repository.UserRepository;
import service.*;
import service.conference.ConferenceService;
import service.session.SessionService;
import view.attendee.AttendeeUI;
import view.organizer.OrganizerUI;
import view.UserUI;
import view.speaker.SpeakerUI;

public class UIFactory {

    private UIFactory() {
    }

    public static UserUI createUserUI(UserDTO userDTO) {
        // creating repositories
        UserRepository userRepository = UserRepository.getInstance();
        ConferenceRepository conferenceRepository = ConferenceRepository.getInstance();
        SessionRepository sessionRepository = SessionRepository.getInstance();
        FeedbackRepository feedbackRepository = FeedbackRepository.getInstance();

        // creating services
        ConferenceService conferenceService = new ConferenceService(conferenceRepository);
        SessionService sessionService = new SessionService(sessionRepository);
        FeedbackService feedbackService = new FeedbackService(feedbackRepository);
        UserService userService = new UserService(userRepository);

        // creating service mediator and linking it to services
        ServiceMediator serviceMediator = new ServiceMediator(userService, conferenceService, sessionService, feedbackService);
        conferenceService.setServiceMediator(serviceMediator);
        sessionService.setServiceMediator(serviceMediator);
        feedbackService.setServiceMediator(serviceMediator);

        // creating organizer controller for OrganizerUI
        OrganizerController organizerController = new OrganizerController(
                userService,
                conferenceService,
                sessionService,
                feedbackService
        );

        // creating attendee controller for AttendeeUI
        AttendeeController attendeeController = new AttendeeController(userService, conferenceService, sessionService, feedbackService);

        // creating speaker controller for SpeakerUI
        SpeakerController speakerController = new SpeakerController(userService, sessionService);

        return switch (userDTO.getRole()) {
            case ORGANIZER -> new OrganizerUI(organizerController, userDTO);
            case ATTENDEE -> new AttendeeUI(attendeeController, speakerController, userDTO);
            case SPEAKER -> new SpeakerUI(speakerController, attendeeController, userDTO);
        };
    }
}
