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

    // private no-arg constructor to suppress instantiability
    private UIFactory() {}
    public static UserUI createUserUI(UserDTO userDTO) {
        return switch (userDTO.getRole()) {
            case ORGANIZER -> new OrganizerUI(new OrganizerController(new UserService(UserRepository.getInstance()), new ConferenceService(new UserService(UserRepository.getInstance()), ConferenceRepository.getInstance()), new SessionService(new UserService(UserRepository.getInstance()), new ConferenceService(new UserService(UserRepository.getInstance()), ConferenceRepository.getInstance()), EmailService.getInstance(), SessionRepository.getInstance()), new FeedbackService(new UserService(UserRepository.getInstance()), FeedbackRepository.getInstance())), userDTO);
            case ATTENDEE -> new AttendeeUI(userDTO);
            case SPEAKER -> new SpeakerUI(userDTO);
        };
    }
}
