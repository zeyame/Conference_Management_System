package util;

import dto.UserDTO;
import ui.AttendeeUI;
import ui.OrganizerUI;
import ui.SpeakerUI;
import ui.UserUI;

public class UIFactory {

    public static UserUI createUserUI(UserDTO userDTO) {
        return switch (userDTO.getRole()) {
            case ORGANIZER -> new OrganizerUI(userDTO);
            case ATTENDEE -> new AttendeeUI(userDTO);
            case SPEAKER -> new SpeakerUI(userDTO);
        };
    }
}
