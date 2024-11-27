package service;

import domain.factory.SessionFactory;
import domain.model.Session;
import dto.ConferenceDTO;
import dto.SessionDTO;
import dto.UserDTO;
import exception.*;
import repository.SessionRepository;
import util.CollectionUtils;
import util.LoggerUtil;
import util.email.EmailService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SessionService {

    private final UserService userService;
    private final ConferenceService conferenceService;
    private final EmailService emailService;
    private final SessionRepository sessionRepository;

    public SessionService(UserService userService, ConferenceService conferenceService, EmailService emailService, SessionRepository sessionRepository) {
        this.userService = userService;
        this.conferenceService = conferenceService;
        this.emailService = emailService;
        this.sessionRepository = sessionRepository;
    }

    public void createOrUpdate(SessionDTO sessionDTO, boolean isUpdate) {
        if (sessionDTO == null) {
            throw new IllegalArgumentException("SessionDTO and ConferenceDTO cannot be null.");
        }

        try {
            System.out.println("Conference id received: " + sessionDTO.getConferenceId());

            ConferenceDTO conferenceDTO = conferenceService.getById(sessionDTO.getConferenceId());

            // validate session data
            validateData(sessionDTO, conferenceDTO, isUpdate);

            // convert DTO to domain object
            Session session = SessionFactory.create(sessionDTO);

            // attempting to save validated session to file storage with retries
            boolean isSessionSaved = sessionRepository.save(session, session.getId());
            if (!isSessionSaved) {
                LoggerUtil.getInstance().logError("Session creation failed due to a data saving error.");
                throw SessionException.savingFailure("An unexpected error occurred while saving session data.");
            }

            // assign session to speaker
            assignSessionToSpeaker(session);

            // add a reference to the session to conference
            conferenceService.registerSession(conferenceDTO.getId(), session.getId());

            if (isUpdate) {
                System.out.println("Registered attendee size in create/update: " + session.getRegisteredAttendees().size());
                notifyAttendeesAndSpeaker(sessionDTO, session.getRegisteredAttendees(), session.getSpeakerId());
            }

            sessionRepository.save(session, session.getId());

            LoggerUtil.getInstance().logInfo(String.format("Session '%s' has successfully been created/updated.", session.getName()));
        } catch (ConferenceNotFoundException e) {
            throw SessionException.invalidConference(e.getMessage());
        }
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

    private void validateData(SessionDTO sessionDTO, ConferenceDTO conferenceDTO, boolean isUpdate) {
        Set<String> conferenceSessions = conferenceDTO.getSessions();

        if (isUpdate) {
            // removing session id from conference sessions, if it exists, for correct validation of session name and time
            conferenceSessions.remove(sessionDTO.getId());

            // removing session from speaker's schedule for correct speaker availability validation
            userService.unassignSessionFromSpeaker(sessionDTO.getSpeakerId(), sessionDTO.getId());
        }

        validateSessionName(sessionDTO.getName(), conferenceSessions, conferenceDTO.getName());
        validateSpeakerAvailability(sessionDTO);
        validateSessionTime(sessionDTO, conferenceSessions, conferenceDTO.getStartDate());
    }

    private void validateSessionName(String sessionName, Set<String> sessionIds, String conferenceName) {
        if (isNameTaken(sessionName, sessionIds)) {
            LoggerUtil.getInstance().logError("Session name validation failed.");
            throw SessionException.nameTaken(String.format("A session with this name is already registered in '%s'. Please choose a different name.", conferenceName));
        }
    }

    private void validateSpeakerAvailability(SessionDTO sessionDTO) {
        String speakerId = sessionDTO.getSpeakerId();
        LocalDateTime sessionStart = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime());
        LocalDateTime sessionEnd = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getEndTime());

        try {
            boolean isAvailable = userService.isSpeakerAvailable(speakerId, sessionStart, sessionEnd);
            if (!isAvailable) {
                LoggerUtil.getInstance().logError("Speaker availability validation failed.");
                throw SessionException.speakerUnavailable("The chosen speaker is not available for the selected time. " +
                        "Please choose a different speaker or change the session timing.");
            }
        } catch (UserNotFoundException | InvalidUserRoleException e) {
            throw SessionException.invalidSpeaker(e.getMessage());
        }
    }

    private void validateSessionTime(SessionDTO sessionDTO, Set<String> conferenceSessions, LocalDate conferenceStartDate) {
        if (sessionDTO == null || conferenceSessions == null || conferenceStartDate == null) {
            throw new IllegalArgumentException("Invalid parameters provided when validating session time. SessionDTO, ConferenceSessions, and ConferenceStartDate cannot be null.");
        }

        if (sessionDTO.getDate().isBefore(conferenceStartDate)) {
            throw SessionException.timeUnavailable(String.format("The session date you selected is earlier than the start date of the conference '%s'. " +
                    "Please select a date on or after the conference's start date.", conferenceStartDate));
        }

        LocalDateTime sessionStart = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime());
        LocalDateTime sessionEnd = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getEndTime());

        // batch fetch all sessions
        List<Optional<Session>> sessionOptionals = sessionRepository.findAllById(conferenceSessions);

        // extract valid sessions
        List<Session> sessions = CollectionUtils.extractValidEntities(sessionOptionals);

        // check for any conflicting sessions within the conference
        sessions.stream()
                .filter(session -> session.overlapsWith(sessionStart, sessionEnd))
                .findFirst()
                .ifPresent(conflictingSession -> {
                    LoggerUtil.getInstance().logError("Session date and time validation failed.");
                    throw SessionException.timeUnavailable(String.format(
                            "The session '%s' is already registered to take place within the time period you selected. " +
                                    "Please choose a different time slot.", conflictingSession.getName()));
                });
    }

    private void assignSessionToSpeaker(Session session) {
        try {
            userService.assignNewSessionForSpeaker(
                    session.getSpeakerId(),
                    session.getId(),
                    LocalDateTime.of(session.getDate(), session.getStartTime()),
                    LocalDateTime.of(session.getDate(), session.getEndTime())
            );
        } catch (UserNotFoundException | InvalidUserRoleException | SavingDataException e) {
            // rollback session creation if assigning session to speaker fails
            deleteById(session.getId());
            LoggerUtil.getInstance().logError("Session creation failed: " + e.getMessage());
            throw handleAssignmentError(e, session.getSpeakerId());
        }
    }

    private SessionException handleAssignmentError(Exception e, String speakerId) {
        if (e instanceof UserNotFoundException) {
            return SessionException.invalidSpeaker("Speaker with id '" + speakerId + "' does not exist.");
        } else if (e instanceof InvalidUserRoleException) {
            return SessionException.invalidSpeaker("User with id '" + speakerId + "' does not have speaker permissions.");
        } else {
            return SessionException.savingFailure("An unexpected error occurred when assigning session to speaker.");
        }
    }

    private void notifyAttendeesAndSpeaker(SessionDTO sessionDTO, Set<String> attendeeIds, String speakerId) {
        try {
            List<UserDTO> users = userService.findAllById(attendeeIds);
            UserDTO user = userService.getBydId(speakerId);
            users.add(user);
            emailService.notifyAttendeesAndSpeakerOfSessionChange(sessionDTO, users);
        } catch (UserNotFoundException e) {
            throw SessionException.notificationFailure("Could not notify speaker of session change due to invalid id.");
        }
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
