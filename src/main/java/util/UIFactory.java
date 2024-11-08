package util;

import controller.OrganizerController;
import dto.UserDTO;
import repository.ConferenceFileRepository;
import repository.UserFileRepository;
import service.ConferenceService;
import service.UserService;
import ui.attendee.AttendeeUI;
import ui.organizer.OrganizerUI;
import ui.speaker.SpeakerUI;
import ui.UserUI;

public class UIFactory {
    public static UserUI createUserUI(UserDTO userDTO) {
        return switch (userDTO.getRole()) {
            case ORGANIZER -> new OrganizerUI(new OrganizerController(new UserService(new UserFileRepository()), new ConferenceService(new ConferenceFileRepository())), userDTO);
            case ATTENDEE -> new AttendeeUI(userDTO);
            case SPEAKER -> new SpeakerUI(userDTO);
        };
    }
}
