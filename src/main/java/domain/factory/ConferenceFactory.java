package domain.factory;

import domain.model.Conference;
import dto.ConferenceDTO;
import util.IdGenerator;

import java.time.LocalDate;

public class ConferenceFactory {
    public static Conference createConference(ConferenceDTO conferenceDTO) {
        String id = IdGenerator.generateUniqueId();
        String organizerId = conferenceDTO.getOrganizerId();
        String name = conferenceDTO.getName();
        String description = conferenceDTO.getDescription();
        LocalDate startDate = conferenceDTO.getStartDate();
        LocalDate endDate = conferenceDTO.getEndDate();

        return new Conference(id, organizerId, name, description, startDate, endDate);
    }
}
