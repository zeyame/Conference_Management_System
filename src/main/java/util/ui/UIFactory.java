package util.ui;

import controller.OrganizerController;
import dto.UserDTO;
import repository.ConferenceFileRepository;
import repository.SessionFileRepository;
import repository.UserFileRepository;
import service.ConferenceService;
import service.SessionService;
import service.UserService;
import view.attendee.AttendeeUI;
import view.organizer.OrganizerUI;
import view.speaker.SpeakerUI;
import view.UserUI;

public class UIFactory {

    // private no-arg constructor to suppress instantiability
    private UIFactory() {}
    public static UserUI createUserUI(UserDTO userDTO) {
        return switch (userDTO.getRole()) {
            case ORGANIZER -> new OrganizerUI(new OrganizerController(new UserService(new UserFileRepository()), new ConferenceService(new ConferenceFileRepository()), new SessionService(new UserService(new UserFileRepository()), new SessionFileRepository())), userDTO);
            case ATTENDEE -> new AttendeeUI(userDTO);
            case SPEAKER -> new SpeakerUI(userDTO);
        };
    }
}
