package service;

import domain.factory.SessionFactory;
import domain.model.Session;
import dto.ConferenceDTO;
import dto.SessionDTO;
import exception.*;
import repository.SessionRepository;
import response.ResponseEntity;
import util.LoggerUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class SessionService {

    private final UserService userService;
    private final ConferenceService conferenceService;
    private final SessionRepository sessionRepository;

    public SessionService(UserService userService, ConferenceService conferenceService, SessionRepository sessionRepository) {
        this.userService = userService;
        this.conferenceService = conferenceService;
        this.sessionRepository = sessionRepository;
    }

    public String create(SessionDTO sessionDTO, ConferenceDTO conferenceDTO) {
        if (sessionDTO == null) {
            LoggerUtil.getInstance().logWarning("SessionDTO object provided to the create method in SessionService is null.");
            return "";
        }

        String sessionName = sessionDTO.getName();

        // validate session data
        validateData(sessionDTO, conferenceDTO);

        // creating session
        Session session = SessionFactory.create(sessionDTO);

        // attempting to save validated session to file storage with retries
        boolean isSessionSaved = sessionRepository.save(session);
        if (!isSessionSaved) {
            LoggerUtil.getInstance().logError("Session creation failed due to a data saving error.");
            throw SessionCreationException.savingFailure("An unexpected error occurred while saving session data.");
        }

        LoggerUtil.getInstance().logInfo(String.format("Session '%s' has successfully been created.", sessionName));

        return session.getId();
    }

    public SessionDTO getById(String id) {
        Optional<Session> sessionOptional = sessionRepository.findById(id);
        if (sessionOptional.isEmpty()) {
            throw new SessionNotFoundException(String.format("Session with id '%s' does not exist.", id));
        }

        Session session = sessionOptional.get();
        String speakerName = userService.getNameById(session.getSpeakerId());

        return mapToDTO(session, speakerName);
    }

    public List<SessionDTO> findByIds(Set<String> ids) {
        if (ids == null) {
            LoggerUtil.getInstance().logWarning("Session ids set provided to findByIds in ServiceService is null.");
            return Collections.emptyList();
        }

        // batch fetch all sessions and extract the valid ones
        List<Session> sessions = extractValidSessions(sessionRepository.findAllById(ids));

        // retrieve the speaker id for each session
        Set<String> speakerIds = sessions.stream()
                .map(Session::getSpeakerId)
                .collect(Collectors.toSet());

        // retrieve the speaker name corresponding to each speaker id
        Map<String, String> speakerIdToNameMap = userService.findNamesByIds(speakerIds);

        // map the session objects to session data transfer objects (DTO)
        return sessions.stream()
                       .map(session -> mapToDTO(session, speakerIdToNameMap.get(session.getSpeakerId())))
                       .collect(Collectors.toList());
    }

    public boolean isNameTaken(String name, Set<String> ids) {
        if (name == null || ids == null || name.isEmpty()) {
            LoggerUtil.getInstance().logWarning("Invalid parameters name or ids provided to isNameTaken in SessionService.");
            return false;
        }

        // batch fetch sessions
        List<Session> sessions = extractValidSessions(sessionRepository.findAllById(ids));

        return sessions.stream()
                .anyMatch(session -> name.equals(session.getName()));
    }

    public void deleteById(String id) {
        sessionRepository.deleteById(id);
    }

    private void validateData(SessionDTO sessionDTO, ConferenceDTO conferenceDTO) {
        String sessionName = sessionDTO.getName();

        // validate session name
        validateSessionName(sessionName, conferenceDTO.getSessions(), conferenceDTO.getName());

        // validate speaker availability
        validateSpeakerAvailability(sessionDTO);

        // validate session time availability within the conference
        validateSessionTime(sessionDTO, conferenceDTO.getSessions());
    }

    private void validateSessionName(String sessionName, Set<String> sessionIds, String conferenceName) {
        if (isNameTaken(sessionName, sessionIds)) {
            LoggerUtil.getInstance().logError("Session name validation failed.");
            throw SessionCreationException.nameTaken(String.format("A session with this name is already registered in '%s'. Please choose a different name.", conferenceName));
        }
    }

    private void validateSpeakerAvailability(SessionDTO sessionDTO) {
        String speakerId = sessionDTO.getSpeakerId();
        LocalDateTime sessionStart = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime());
        LocalDateTime sessionEnd = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getEndTime());

        boolean isAvailable = userService.isSpeakerAvailable(speakerId, sessionStart, sessionEnd);
        if (!isAvailable) {
            LoggerUtil.getInstance().logError("Speaker availability validation failed.");
            throw SessionCreationException.speakerUnavailable("The chosen speaker is not available for the selected time. Please choose a different speaker or change the session timing.");
        }
    }

    private void validateSessionTime(SessionDTO sessionDTO, Set<String> sessionIds) {
        List<Session> sessions = extractValidSessions(sessionRepository.findAllById(sessionIds));
        LocalDateTime sessionStart = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime());
        LocalDateTime sessionEnd = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getEndTime());

        for (Session session : sessions) {
            if (session.overlapsWith(sessionStart, sessionEnd)) {
                LoggerUtil.getInstance().logError("Session date and time validation failed.");
                throw SessionCreationException.timeUnavailable(String.format("The session '%s' is already registered to take place within the time period you selected. Please choose a different time slot.", session.getName()));
            }
        }
    }

    private List<Session> extractValidSessions(List<Optional<Session>> sessionOptionals) {
        // extract valid session
        return sessionOptionals.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private SessionDTO mapToDTO(Session session, String speakerName) {
        return SessionDTO.builder(
                session.getConferenceId(),
                session.getSpeakerId(),
                speakerName,
                session.getName(),
                session.getRoom(),
                session.getDate(),
                session.getStartTime(),
                session.getEndTime()
        ).setId(session.getId())
         .setDescription(session.getDescription())
         .setRegisteredAttendees(session.getRegisteredAttendees())
         .setPresentAttendees(session.getPresentAttendees())
         .build();
    }
}
