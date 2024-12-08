package service.session;

import dto.ConferenceDTO;
import dto.SessionDTO;
import exception.SessionException;
import util.LoggerUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SessionValidatorService {

    public static void validateData(SessionDTO sessionDTO, ConferenceDTO conferenceDTO, List<SessionDTO> conferenceSessions, boolean isUpdate) {
        if (sessionDTO == null || conferenceDTO == null || conferenceSessions == null) {
            throw new IllegalArgumentException("SessionDTO, ConferenceDTO, conference sessions, and existing session names cannot be null.");
        }

        if (isUpdate) {
            // remove the session from lists to validate correctly during an update
            conferenceSessions.removeIf(session -> session.getId().equals(sessionDTO.getId()));
        }

        validateSessionName(sessionDTO.getName(), getSessionNames(conferenceSessions));
        validateSpeakerAvailability(sessionDTO, conferenceSessions);
        validateSessionTime(sessionDTO, conferenceSessions, conferenceDTO.getStartDate(), conferenceDTO.getEndDate());
    }

    private static void validateSessionName(String sessionName, Set<String> existingSessionNames) {
        if (sessionName == null || sessionName.isEmpty()) {
            throw new IllegalArgumentException("Session name cannot be null or empty.");
        }

        if (existingSessionNames.contains(sessionName)) {
            LoggerUtil.getInstance().logError("Session name validation failed: Name is already taken.");
            throw new SessionException("A session with this name is already registered to this conference. Please choose a different name.");
        }
    }

    private static void validateSpeakerAvailability(SessionDTO sessionDTO, List<SessionDTO> conferenceSessions) {
        String speakerId = sessionDTO.getSpeakerId();
        LocalDateTime sessionStart = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime());
        LocalDateTime sessionEnd = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getEndTime());

        boolean isSpeakerUnavailable = conferenceSessions.stream()
                .filter(session -> session.getSpeakerId().equals(speakerId))
                .anyMatch(session -> session.overlapsWith(sessionStart, sessionEnd));

        if (isSpeakerUnavailable) {
            LoggerUtil.getInstance().logError("Speaker availability validation failed: Speaker is unavailable.");
            throw new SessionException("The speaker is already assigned to another session during the selected time. " +
                    "Please choose a different speaker or time.");
        }
    }

    private static void validateSessionTime(SessionDTO sessionDTO, List<SessionDTO> conferenceSessions, LocalDate conferenceStartDate, LocalDate conferenceEndDate) {
        if (sessionDTO == null || conferenceSessions == null || conferenceStartDate == null || conferenceEndDate == null) {
            throw new IllegalArgumentException("SessionDTO, conference sessions, conference start and end dates cannot be null.");
        }

        LocalDateTime conferenceStartDateTime = LocalDateTime.of(conferenceStartDate, LocalTime.MIN);
        LocalDateTime conferenceEndDateTime = LocalDateTime.of(conferenceEndDate, LocalTime.MAX);
        LocalDateTime sessionStart = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime());
        LocalDateTime sessionEnd = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getEndTime());

        if (sessionStart.isBefore(conferenceStartDateTime)) {
            throw new SessionException("Session start time is before the conference start time. Please adjust the session timing.");
        }

        if (sessionEnd.isAfter(conferenceEndDateTime)) {
            throw new SessionException("Session end time is after the conference end time. Please adjust the session timing.");
        }

        boolean timeConflict = conferenceSessions.stream()
                .anyMatch(session -> session.overlapsWith(sessionStart, sessionEnd));

        if (timeConflict) {
            LoggerUtil.getInstance().logError("Session time validation failed: Time slot is already occupied.");
            throw new SessionException("The selected time slot is already occupied by another session. Please choose a different time.");
        }
    }

    private static Set<String> getSessionNames(List<SessionDTO> sessionDTOs) {
        return sessionDTOs.stream()
                .map(SessionDTO::getName)
                .collect(Collectors.toSet());
    }
}
