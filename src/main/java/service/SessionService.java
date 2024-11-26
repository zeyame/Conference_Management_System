package service;

import domain.factory.SessionFactory;
import domain.model.Session;
import dto.ConferenceDTO;
import dto.SessionDTO;
import exception.*;
import repository.SessionRepository;
import util.CollectionUtils;
import util.LoggerUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SessionService {

    private final UserService userService;
    private final SessionRepository sessionRepository;

    public SessionService(UserService userService, SessionRepository sessionRepository) {
        this.userService = userService;
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
        boolean isSessionSaved = sessionRepository.save(session, session.getId());
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

    public List<SessionDTO> findAllById(Set<String> ids) {
        if (ids == null) {
            LoggerUtil.getInstance().logWarning("Session ids set provided to findByIds in ServiceService is null.");
            return Collections.emptyList();
        }

        // batch fetch all sessions
        List<Optional<Session>> sessionOptionals = sessionRepository.findAllById(ids);

        // extract valid sessions
        List<Session> sessions = CollectionUtils.extractValidEntities(sessionOptionals);

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

        // batch fetch all sessions
        List<Optional<Session>> sessionOptionals = sessionRepository.findAllById(ids);

        // extract valid sessions
        List<Session> sessions = CollectionUtils.extractValidEntities(sessionOptionals);

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
        validateSessionTime(sessionDTO, conferenceDTO);
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

    private void validateSessionTime(SessionDTO sessionDTO, ConferenceDTO conferenceDTO) {
        if (sessionDTO == null || conferenceDTO == null) {
            throw new IllegalArgumentException("Invalid parameters provided when validating session time. SessionDTO and ConferenceDTO cannot be null.");
        }

        if (sessionDTO.getDate().isBefore(conferenceDTO.getStartDate())) {
            throw SessionCreationException.timeUnavailable(String.format("The session date you selected is earlier than the start date of the conference '%s'. Please select a date on or after the conference's start date.", conferenceDTO.getStartDate()));
        }

        LocalDateTime sessionStart = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime());
        LocalDateTime sessionEnd = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getEndTime());

        // batch fetch all sessions
        List<Optional<Session>> sessionOptionals = sessionRepository.findAllById(conferenceDTO.getSessions());

        // extract valid sessions
        List<Session> sessions = CollectionUtils.extractValidEntities(sessionOptionals);

        sessions.stream()
                .filter(session -> session.overlapsWith(sessionStart, sessionEnd))
                .findFirst()
                .ifPresent(conflictingSession -> {
                    LoggerUtil.getInstance().logError("Session date and time validation failed.");
                    throw SessionCreationException.timeUnavailable(String.format(
                            "The session '%s' is already registered to take place within the time period you selected. Please choose a different time slot.",
                            conflictingSession.getName()));
                });
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
         .setFeedback(session.getFeedback())
         .build();
    }
}
