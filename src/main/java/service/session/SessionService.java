package service.session;

import domain.factory.SessionFactory;
import domain.model.Session;
import dto.ConferenceDTO;
import dto.SessionDTO;
import exception.*;
import repository.SessionRepository;
import service.conference.ConferenceService;
import service.UserService;
import util.CollectionUtils;
import util.LoggerUtil;
import util.email.EmailService;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SessionService {

    private final UserService userService;
    private final ConferenceService conferenceService;
    private final EmailService emailService;
    private final SessionRepository sessionRepository;
    private final SessionValidator validator;
    private final SessionRollbackService rollbackService;
    private final SessionNotificationService notificationService;

    public SessionService(UserService userService, ConferenceService conferenceService, EmailService emailService, SessionRepository sessionRepository) {
        this.userService = userService;
        this.conferenceService = conferenceService;
        this.emailService = emailService;
        this.sessionRepository = sessionRepository;
        this.validator = createSessionValidator();
        this.rollbackService = createSessionRollbackService();
        this.notificationService = createSessionNotificationService();
    }

    public void create(SessionDTO sessionDTO) {
        if (sessionDTO == null) {
            throw new IllegalArgumentException("SessionDTO cannot be null.");
        }

        boolean sessionSaved = false;
        boolean conferenceUpdated = false;
        boolean speakerAssigned = false;

        try {
            // get existing sessions in conference
            ConferenceDTO conferenceDTO = conferenceService.getById(sessionDTO.getConferenceId());
            List<SessionDTO> conferenceSessions = findAllById(conferenceDTO.getSessions());

            // validate session data for creation
            validator.validateData(sessionDTO, conferenceDTO, conferenceSessions, false);

            // convert DTO to domain object and save
            Session session = SessionFactory.create(sessionDTO);
            sessionSaved = sessionRepository.save(session, session.getId());
            if (!sessionSaved) {
                throw SessionException.savingFailure("An unexpected error occurred while saving session data.");
            }

            // assign session to speaker
            assignSessionToSpeaker(session);
            speakerAssigned = true;

            // add session to conference
            conferenceService.registerSession(conferenceDTO.getId(), mapToDTO(session, userService.getNameById(session.getSpeakerId())));
            conferenceUpdated = true;

            notificationService.notifySessionCreation(sessionDTO, session.getRegisteredAttendees(), session.getSpeakerId());

            LoggerUtil.getInstance().logInfo(String.format("Session '%s' has been successfully created.", session.getName()));
        } catch (RuntimeException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to create session '%s': %s", sessionDTO.getName(), e.getMessage()));

            // rolling back changes
            rollbackCreateOrUpdate(sessionDTO, sessionSaved, speakerAssigned, conferenceUpdated);

            throw SessionException.creationFailure(String.format("An error occurred when creating session: %s", e.getMessage()));
        }
    }

    public void update(SessionDTO sessionDTO) {
        if (sessionDTO == null || sessionDTO.getId() == null || sessionDTO.getId().isEmpty()) {
            throw new IllegalArgumentException("SessionDTO and its ID cannot be null or empty for updates.");
        }

        boolean sessionSaved = false;
        boolean speakerAssigned = false;
        boolean conferenceUpdated = false;

        try {
            // get existing sessions in conference to use for validation
            ConferenceDTO conferenceDTO = conferenceService.getById(sessionDTO.getConferenceId());
            List<SessionDTO> conferenceSessions = findAllById(conferenceDTO.getSessions());

            // validate session data for update
            validator.validateData(sessionDTO, conferenceDTO, conferenceSessions, true);

            // convert DTO to domain object and save
            Session session = SessionFactory.create(sessionDTO);
            sessionSaved = sessionRepository.save(session, session.getId());
            if (!sessionSaved) {
                throw SessionException.savingFailure("An unexpected error occurred while saving session data.");
            }

            // Assign session to speaker
            assignSessionToSpeaker(session);
            speakerAssigned = true;

            // Update session in conference
            conferenceService.registerSession(conferenceDTO.getId(), mapToDTO(session, userService.getNameById(session.getSpeakerId())));
            conferenceUpdated = true;

            notificationService.notifySessionChange(sessionDTO, session.getRegisteredAttendees(), session.getSpeakerId());

            LoggerUtil.getInstance().logInfo(String.format("Session '%s' has been successfully updated.", session.getName()));
        } catch (RuntimeException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to update session '%s': %s", sessionDTO.getName(), e.getMessage()));

            // rolling back changes
            rollbackCreateOrUpdate(sessionDTO, sessionSaved, speakerAssigned, conferenceUpdated);

            throw SessionException.updateFailure(String.format("An error occurred when updating session: %s", e.getMessage()));
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

    public SessionDTO getByName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Session name cannot be null or empty.");
        }

        Optional<Session> sessionOptional = sessionRepository.getByName(name);
        if (sessionOptional.isEmpty()) {
            throw SessionException.notFound(String.format("A session with the name '%s' does not exist.", name));
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

    public boolean isNameTaken(String name, Set<String> conferenceSessions) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Session name cannot be null or empty.");
        }

        if (conferenceSessions == null || conferenceSessions.isEmpty()) {
            return false;
        }

        // Fetch all sessions that belong to the conference
        List<Optional<Session>> sessionOptionals = sessionRepository.findAllById(conferenceSessions);

        // Extract valid sessions
        List<Session> sessions = CollectionUtils.extractValidEntities(sessionOptionals);

        // Check if any session in the conference matches the given name
        return sessions.stream().anyMatch(session -> name.equals(session.getName()));
    }


    public void deleteById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Session id cannot be null or empty.");
        }

        // retrieve session
        Optional<Session> sessionOptional = sessionRepository.findById(id);
        if (sessionOptional.isEmpty()) {
            throw SessionException.notFound(String.format("Failed to delete session with id '%s' as it does not exist.", id));
        }

        Session session = sessionOptional.get();
        SessionDTO sessionDTO = mapToDTO(session, userService.getNameById(session.getSpeakerId()));

        boolean sessionDeleted = false;
        boolean sessionRemovedFromConference = false;
        boolean sessionUnassignedFromSpeaker = false;
        try {
            // delete from repository
            sessionDeleted = sessionRepository.deleteById(id);
            if (!sessionDeleted) {
                throw SessionException.deletingFailure("An unexpected error occurred when deleting session. Please try again later.");
            }

            // remove session from speaker schedule
            userService.unassignSessionFromSpeaker(session.getSpeakerId(), session.getId());
            sessionUnassignedFromSpeaker = true;

            // remove session from conference
            conferenceService.removeSession(session.getConferenceId(), session.getId());
            sessionRemovedFromConference = true;

            notificationService.notifySessionDeletion(sessionDTO, sessionDTO.getRegisteredAttendees(), sessionDTO.getSpeakerId());
        } catch (RuntimeException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to delete session with id '%s': %s", id, e.getMessage()));

            // rolling back changes
            rollbackDeletion(sessionDTO, sessionDeleted, sessionRemovedFromConference, sessionUnassignedFromSpeaker);

            // throwing original exception that caused the error
            throw SessionException.deletingFailure("An error occurred when deleting session: " + e.getMessage());
        }
    }


    private SessionRollbackService createSessionRollbackService() {
        return new SessionRollbackService(
                this.userService,
                this.conferenceService,
                this::deleteSession,
                this::saveSession
        );
    }

    private SessionValidator createSessionValidator() {
        return new SessionValidator(
                this.userService,
                this::isNameTaken
        );
    }

    private SessionNotificationService createSessionNotificationService() {
        return new SessionNotificationService(this.userService, this.emailService);
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

    private void rollbackCreateOrUpdate(SessionDTO sessionDTO, boolean sessionSaved, boolean speakerAssigned, boolean conferenceUpdated) {
        try {
            if (sessionSaved) rollbackService.rollbackSessionSave(sessionDTO);
            if (speakerAssigned) rollbackService.rollbackSpeakerAssignment(sessionDTO);
            if (conferenceUpdated) rollbackService.rollbackConferenceUpdate(sessionDTO);
        } catch (SessionException rollbackException) {
            LoggerUtil.getInstance().logError("Rolling back session creation failed: " + rollbackException.getMessage());
        }
    }

    private void rollbackDeletion(SessionDTO sessionDTO, boolean sessionDeleted, boolean speakerUnassigned, boolean sessionRemovedFromConference) {
        try {
            if (sessionDeleted) rollbackService.rollbackSessionDeletion(sessionDTO);
            if (speakerUnassigned) rollbackService.rollbackSpeakerUnassignment(sessionDTO);
            if (sessionRemovedFromConference) rollbackService.rollbackSessionRemovalFromConference(sessionDTO);
        } catch (SessionException rollbackException) {
            LoggerUtil.getInstance().logError("Rolling back session deletion failed: " + rollbackException.getMessage());
        }
    }

    // helper methods passed as references to rollback service to decouple it from session repository
    private boolean saveSession(SessionDTO sessionDTO) {
        Session session = SessionFactory.create(sessionDTO);
        return sessionRepository.save(session, session.getId());
    }

    private boolean deleteSession(String id) {
        return sessionRepository.deleteById(id);
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
