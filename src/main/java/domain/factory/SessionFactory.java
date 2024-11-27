package domain.factory;

import domain.model.Session;
import dto.SessionDTO;
import util.IdGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class SessionFactory {

    // private no-arg constructor to suppress instantiability
    private SessionFactory() {}

    public static Session create(SessionDTO sessionDTO) {
        String id = sessionDTO.getId() != null ? sessionDTO.getId() : IdGenerator.generateUniqueId();
        String conferenceId = sessionDTO.getConferenceId();
        String speakerId = sessionDTO.getSpeakerId();
        String name = sessionDTO.getName();
        String description = sessionDTO.getDescription();
        String room = sessionDTO.getRoom();
        LocalDate date = sessionDTO.getDate();
        LocalTime startTime = sessionDTO.getStartTime();
        LocalTime endTime = sessionDTO.getEndTime();
        Set<String> registeredAttendees = sessionDTO.getRegisteredAttendees();
        Set<String> presentAttendees = sessionDTO.getPresentAttendees();
        Set<String> feedback = sessionDTO.getFeedback();

        return Session.builder(id, conferenceId, speakerId, name, room, date, startTime, endTime)
                .setDescription(description)
                .setRegisteredAttendees(registeredAttendees)
                .setPresentAttendees(presentAttendees)
                .setFeedback(feedback)
                .build();
    }
}
