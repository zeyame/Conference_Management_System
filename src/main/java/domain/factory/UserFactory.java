package domain.factory;

import domain.model.*;
import dto.RegistrationDTO;
import exception.UserRegistrationException;
import util.IdGenerator;
import util.LoggerUtil;

public class UserFactory {
    public static User createUser(RegistrationDTO registrationDTO) {
        String id = IdGenerator.generateUniqueId();
        String email = registrationDTO.getEmail();
        String name = registrationDTO.getName();
        String hashedPassword = new String(registrationDTO.getPassword());
        UserRole role = registrationDTO.getUserRole();

        return switch (role) {
            case ORGANIZER -> new Organizer(id, email, name, hashedPassword, role);
            case ATTENDEE -> new Attendee(id, email, name, hashedPassword, role);
            case SPEAKER -> new Speaker(id, email, name, hashedPassword, role);
            default -> {
                LoggerUtil.getInstance().logError("Invalid role '" + role + "' found in the registrationDTO passed to createUser in UserFactory.");
                throw UserRegistrationException.invalidRole();
            }
        };
    }
}
