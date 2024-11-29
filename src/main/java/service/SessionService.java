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
            throw new IllegalArgumentException("SessionDTO cannot be null.");
        }

        boolean sessionSaved = false;
        boolean conferenceUpdated = false;
        boolean speakerAssigned = false;

        try {
            ConferenceDTO conferenceDTO = conferenceService.getById(sessionDTO.getConferenceId());

            // validate session data
            validateData(sessionDTO, conferenceDTO, isUpdate);

            // convert DTO to domain object
            Session session = SessionFactory.create(sessionDTO);

            // attempting to save validated session to file storage with retries
            sessionSaved = sessionRepository.save(session, session.getId());
            if (!sessionSaved) {
                throw SessionException.savingFailure("An unexpected error occurred while saving session data.");
            }

            // assign session to speaker
            assignSessionToSpeaker(session);
            speakerAssigned = true;

            // add a reference to the session to conference
            conferenceService.registerSession(conferenceDTO.getId(), session.getId());
            conferenceUpdated = true;

            if (isUpdate) {
                notifyAttendeesAndSpeaker(sessionDTO, session.getRegisteredAttendees(), session.getSpeakerId());
            }

            LoggerUtil.getInstance().logInfo(String.format("Session '%s' has successfully been created/updated.", session.getName()));
        } catch (IllegalArgumentException e) {
            throw SessionException.invalidData(e.getMessage());
        } catch (ConferenceException e) {
            throw SessionException.invalidConference(e.getMessage());
        } catch (SessionException e) {
            LoggerUtil.getInstance().logError(String.format("Session creation/update failed: %s", e.getMessage()));
            try {
                // rollback changes
                if (conferenceUpdated) {
                    rollbackConferenceUpdate(sessionDTO);
                }

                if (speakerAssigned) {
                    rollbackSpeakerAssignment(sessionDTO);
                }

                if (sessionSaved) {
                    rollbackSessionSave(sessionDTO);
                }
            } catch (SessionException rollbackException) {
                LoggerUtil.getInstance().logError(String.format("Rollback failed after session creation/update error: %s", rollbackException.getMessage()));
            }

            // throw original exception that caused the error
            throw e;
        }
    }

    public SessionDTO getById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Session id cannot be null or empty.");
        }

        Optional<Session> sessionOptional = sessionRepository.findById(id);
        if (sessionOptional.isEmpty()) {
            throw SessionException.notFound(String.format("Session with id '%s' could not be found.", id));
        }

        Session session = sessionOptional.get();
        String speakerName = userService.getNameById(session.getSpeakerId());

        return mapToDTO(session, speakerName);
    }

    public List<SessionDTO> findAllById(Set<String> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("Session ids cannot be null.");
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
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Session name cannot be null or empty.");
        }

        if (ids == null) {
            throw new IllegalArgumentException("Session ids cannot be null.");
        }

        // batch fetch all sessions
        List<Optional<Session>> sessionOptionals = sessionRepository.findAllById(ids);

        // extract valid sessions
        List<Session> sessions = CollectionUtils.extractValidEntities(sessionOptionals);

        return sessions.stream()
                .anyMatch(session -> name.equals(session.getName()));
    }

    public void deleteById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Session id cannot be null or empty.");
        }

        Optional<Session> sessionOptional = sessionRepository.findById(id);
        if (sessionOptional.isEmpty()) {
            throw SessionException.notFound(String.format("Failed to delete session with id '%s' as it does not exist.", id));
        }

        Session session = sessionOptional.get();
        boolean sessionDeleted = false;
        boolean sessionUnassignedFromSpeaker = false;

        try {
            // Delete from repository
            sessionDeleted = sessionRepository.deleteById(id);
            if (!sessionDeleted) {
                throw SessionException.deletingFailure("An unexpected error occurred when deleting session. Please try again later.");
            }

            // Unassign session from speaker
            userService.unassignSessionFromSpeaker(session.getSpeakerId(), session.getId());
            sessionUnassignedFromSpeaker = true;

            // Remove session from conference
            conferenceService.removeSession(session.getConferenceId(), session.getId());
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Session deletion failed for session '%s'. Initiating rollback: %s", id, e.getMessage()));

            try {
                // Rollback operations
                if (sessionDeleted) {
                    rollbackSessionDeletion(session);
                }
                if (sessionUnassignedFromSpeaker) {
                    rollbackSpeakerUnassignment(session);
                }
            } catch (SessionException rollbackException) {
                LoggerUtil.getInstance().logError(String.format("Rollback failed after session deletion error: %s", rollbackException.getMessage()));
            }

            LoggerUtil.getInstance().logError("Session deletion failed. Rollback successful.");
            throw SessionException.deletingFailure("An unexpected error occurred when deleting session. Please try again later.");
        }
    }


    private void validateData(SessionDTO sessionDTO, ConferenceDTO conferenceDTO, boolean isUpdate) {
        if (sessionDTO == null || conferenceDTO == null) {
            throw new IllegalArgumentException("SessionDTO and ConferenceDTO cannot be null.");
        }

        Set<String> conferenceSessions = conferenceDTO.getSessions();

        if (isUpdate) {
            // removing session id from conference sessions, if it exists, for correct validation of session name and time in the case of updates
            conferenceSessions.remove(sessionDTO.getId());

            // removing session from speaker's schedule for correct speaker availability validation in case of updates
            try {
                userService.unassignSessionFromSpeaker(sessionDTO.getSpeakerId(), sessionDTO.getId());
            } catch (IllegalArgumentException | UserException e) {
                LoggerUtil.getInstance().logError(String.format("Session validation failed: %s", e.getMessage()));
                throw SessionException.validationFailure("An unexpected error occurred when validating session data. Please try again later.");
            }
        }

        validateSessionName(sessionDTO.getName(), conferenceSessions, conferenceDTO.getName());
        validateSpeakerAvailability(sessionDTO);
        validateSessionTime(sessionDTO, conferenceSessions, conferenceDTO.getStartDate());
    }

    private void validateSessionName(String sessionName, Set<String> sessionIds, String conferenceName) {
        if (sessionName == null || conferenceName == null || sessionName.isEmpty() || conferenceName.isEmpty()) {
            throw new IllegalArgumentException("Session and conference names cannot be null or empty.");
        }

        if (sessionIds == null) {
            throw new IllegalArgumentException("Session ids cannot be null.");
        }

        if (isNameTaken(sessionName, sessionIds)) {
            LoggerUtil.getInstance().logError("Session name validation failed: Session name is taken.");
            throw SessionException.nameTaken(String.format("A session with this name is already registered in '%s'. Please choose a different name.", conferenceName));
        }
    }

    private void validateSpeakerAvailability(SessionDTO sessionDTO) {
        if (sessionDTO == null) {
            throw new IllegalArgumentException("SessionDTO cannot be null.");
        }

        String speakerId = sessionDTO.getSpeakerId();
        LocalDateTime sessionStart = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime());
        LocalDateTime sessionEnd = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getEndTime());

        try {
            boolean isAvailable = userService.isSpeakerAvailable(speakerId, sessionStart, sessionEnd);
            if (!isAvailable) {
                LoggerUtil.getInstance().logError("Speaker is not available to give session.");
                throw SessionException.unavailableSpeaker("The chosen speaker is not available for the selected time. " +
                        "Please choose a different speaker or change the session timing.");
            }
        } catch (IllegalArgumentException | UserException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to validate speaker availability: %s", e.getMessage()));
            throw SessionException.validationFailure("An unexpected error occurred when validation speaker availability for session. Please try again later.");
        }
    }

    private void validateSessionTime(SessionDTO sessionDTO, Set<String> conferenceSessions, LocalDate conferenceStartDate) {
        if (sessionDTO == null || conferenceSessions == null || conferenceStartDate == null) {
            throw new IllegalArgumentException("Invalid parameters provided when validating session time. SessionDTO, ConferenceSessions, and ConferenceStartDate cannot be null.");
        }

        if (sessionDTO.getDate().isBefore(conferenceStartDate)) {
            throw SessionException.validationFailure(String.format("The session date you selected is earlier than the start date of the conference '%s'. " +
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
                    LoggerUtil.getInstance().logError("Session date and time validation failed as another session is " +
                            "already set to take place in the selected time.");
                    throw SessionException.timeUnavailable(String.format(
                            "The session '%s' is already registered to take place within the time period you selected. " +
                                    "Please choose a different time slot.", conflictingSession.getName()));
                });
    }

    private void assignSessionToSpeaker(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null.");
        }

        try {
            userService.assignNewSessionForSpeaker(
                    session.getSpeakerId(),
                    session.getId(),
                    LocalDateTime.of(session.getDate(), session.getStartTime()),
                    LocalDateTime.of(session.getDate(), session.getEndTime())
            );
        } catch (IllegalArgumentException | UserException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to assign session to speaker: %s", e.getMessage()));
            throw SessionException.assignmentToSpeaker("An unexpected error occurred when assigning session to speaker. Please try again later.");
        }
    }

    private void notifyAttendeesAndSpeaker(SessionDTO sessionDTO, Set<String> attendeeIds, String speakerId) {
        if (speakerId == null || speakerId.isEmpty()) {
            throw new IllegalArgumentException("Speaker id cannot be null or empty.");
        }

        if (sessionDTO == null || attendeeIds == null) {
            throw new IllegalArgumentException("SessionDTO and attendee ids cannot be null.");
        }

        try {
            List<UserDTO> users = userService.findAllById(attendeeIds);
            UserDTO user = userService.getBydId(speakerId);
            users.add(user);
            emailService.notifyAttendeesAndSpeakerOfSessionChange(sessionDTO, users);
        } catch (UserException e) {
            throw SessionException.notificationFailure("Could not notify speaker of session change due to invalid id.");
        }
    }



    // ROLLBACK OPERATIONS

    private void rollbackSessionSave(SessionDTO sessionDTO) {
        boolean sessionDeleted = sessionRepository.deleteById(sessionDTO.getId());
        if (!sessionDeleted) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Could not delete session '%s' from repository.", sessionDTO.getId()));
            throw SessionException.rollbackFailure("Failed to delete session from repository during rollback of session creation/update.");
        }
        LoggerUtil.getInstance().logInfo(String.format("Rollback: Session '%s' successfully deleted from repository.", sessionDTO.getId()));
    }
    private void rollbackSessionDeletion(Session session) {
        boolean sessionRestored = sessionRepository.save(session, session.getId());
        if (!sessionRestored) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Could not restore session '%s' to repository.", session.getId()));
            throw SessionException.rollbackFailure("Failed to restore session to repository during rollback of session deletion.");
        }
        LoggerUtil.getInstance().logInfo(String.format("Rollback: Session '%s' successfully restored to repository.", session.getId()));
    }

    private void rollbackSpeakerAssignment(SessionDTO sessionDTO) {
        try {
            userService.unassignSessionFromSpeaker(sessionDTO.getSpeakerId(), sessionDTO.getId());
            LoggerUtil.getInstance().logInfo(String.format("Rollback: Session '%s' successfully unassigned from speaker.", sessionDTO.getId()));
        } catch (UserException e) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Could not unassign session '%s' from speaker: %s", sessionDTO.getId(), e.getMessage()));
            throw SessionException.rollbackFailure("Failed to unassign session from speaker during rollback.");
        }
    }

    private void rollbackSpeakerUnassignment(Session session) {
        try {
            userService.assignNewSessionForSpeaker(
                    session.getSpeakerId(), session.getId(),
                    LocalDateTime.of(session.getDate(), session.getStartTime()),
                    LocalDateTime.of(session.getDate(), session.getEndTime())
            );
            LoggerUtil.getInstance().logInfo(String.format("Rollback: Session '%s' successfully re-assigned to speaker.", session.getId()));
        } catch (UserException e) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Could not re-assign session '%s' to speaker: %s", session.getId(), e.getMessage()));
            throw SessionException.rollbackFailure("Failed to re-assign session to speaker during rollback of session deletion.");
        }
    }

    private void rollbackConferenceUpdate(SessionDTO sessionDTO) {
        try {
            conferenceService.removeSession(sessionDTO.getConferenceId(), sessionDTO.getId());
            LoggerUtil.getInstance().logInfo(String.format("Rollback: Session '%s' successfully removed from conference.", sessionDTO.getId()));
        } catch (ConferenceException e) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Could not remove session '%s' from conference: %s", sessionDTO.getId(), e.getMessage()));
            throw SessionException.rollbackFailure("Failed to remove session from conference during rollback.");
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
