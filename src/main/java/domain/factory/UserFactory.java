package domain.factory;

import domain.model.*;
import dto.RegistrationDTO;
import util.IdGenerator;

public class UserFactory {

    // private no-arg constructor to suppress instantiability
    private UserFactory() {}

    public static User createUser(RegistrationDTO registrationDTO) {
        String id = IdGenerator.generateUniqueId();
        String email = registrationDTO.getEmail();
        String name = registrationDTO.getName();
        String speakerBio = registrationDTO.getSpeakerBio();        // if user is a speaker
        String employeeId = registrationDTO.getEmployeeId();        // if user is an organizer
        String hashedPassword = new String(registrationDTO.getPassword());
        UserRole role = registrationDTO.getUserRole();

        return switch (role) {
            case ORGANIZER -> new Organizer(id, email, name, employeeId, hashedPassword, role);
            case ATTENDEE -> new Attendee(id, email, name, hashedPassword, role);
            case SPEAKER -> new Speaker(id, email, name, speakerBio, hashedPassword, role);
        };
    }
}
