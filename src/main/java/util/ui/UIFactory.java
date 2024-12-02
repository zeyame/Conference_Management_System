package util.ui;

import controller.OrganizerController;
import dto.UserDTO;
import repository.ConferenceRepository;
import repository.FeedbackRepository;
import repository.SessionRepository;
import repository.UserRepository;
import service.*;
import service.conference.ConferenceService;
import service.session.SessionService;
import util.email.EmailService;
import view.attendee.AttendeeUI;
import view.organizer.OrganizerUI;
import view.speaker.SpeakerUI;
import view.UserUI;

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

        return switch (userDTO.getRole()) {
            case ORGANIZER -> new OrganizerUI(organizerController, userDTO);
            case ATTENDEE -> new AttendeeUI(userDTO);
            case SPEAKER -> new SpeakerUI(userDTO);
        };
    }
}
