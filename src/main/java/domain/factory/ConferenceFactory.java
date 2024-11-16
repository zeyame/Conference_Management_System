package domain.factory;

import domain.model.Conference;
import dto.ConferenceFormDTO;
import util.IdGenerator;

import java.util.Date;

public class ConferenceFactory {
    public static Conference createConference(ConferenceFormDTO conferenceFormDTO) {
        String id = IdGenerator.generateUniqueId();
        String organizerId = conferenceFormDTO.getOrganizerId();
        String name = conferenceFormDTO.getName();
        String description = conferenceFormDTO.getDescription();
        Date startDate = conferenceFormDTO.getStartDate();
        Date endDate = conferenceFormDTO.getEndDate();

        return new Conference(id, organizerId, name, description, startDate, endDate);
    }
}
