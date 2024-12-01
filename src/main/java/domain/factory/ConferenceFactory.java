package domain.factory;

import domain.model.Conference;
import dto.ConferenceDTO;
import util.IdGenerator;

import java.time.LocalDate;

public class ConferenceFactory {

    // private no-arg constructor to suppress instantiability
    private ConferenceFactory() {}

    public static Conference createConference(ConferenceDTO conferenceDTO) {
        String id = conferenceDTO.getId() != null ? conferenceDTO.getId() : IdGenerator.generateUniqueId();
        String organizerId = conferenceDTO.getOrganizerId();
        String name = conferenceDTO.getName();
        String description = conferenceDTO.getDescription();
        LocalDate startDate = conferenceDTO.getStartDate();
        LocalDate endDate = conferenceDTO.getEndDate();

        return Conference.builder(id, organizerId, name, description, startDate, endDate).build();
    }
}
