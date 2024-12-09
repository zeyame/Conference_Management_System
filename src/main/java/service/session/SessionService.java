package service.session;

import domain.factory.SessionFactory;
import domain.model.Session;
import dto.ConferenceDTO;
import dto.SessionDTO;
import dto.UserDTO;
import exception.*;
import repository.SessionRepository;
import service.ServiceMediator;
import util.CollectionUtils;
import util.LoggerUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SessionService {

    private ServiceMediator serviceMediator;
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void setServiceMediator(ServiceMediator serviceMediator) {
        this.serviceMediator = serviceMediator;
    }

    public void create(SessionDTO sessionDTO) {
        if (sessionDTO == null) {
            throw new IllegalArgumentException("SessionDTO cannot be null.");
        }

        boolean sessionSaved = false;
        boolean conferenceUpdated = false;
        boolean speakerAssigned = false;

        try {
            // prepare data for validation - conference data, conference sessions
            ConferenceDTO conferenceDTO = serviceMediator.getConferenceById(sessionDTO.getConferenceId());
            List<SessionDTO> conferenceSessions = findAllById(conferenceDTO.getSessions());

            // validate session data for creation
            SessionValidatorService.validateData(sessionDTO, conferenceDTO, conferenceSessions, false);

            // convert DTO to domain object and save
            Session session = SessionFactory.create(sessionDTO);
            sessionSaved = sessionRepository.save(session, session.getId());
            if (!sessionSaved) {
                throw new SessionException("An unexpected error occurred while saving session data.");
            }

            // assign session to speaker
            serviceMediator.assignNewSessionForSpeaker(sessionDTO);
            speakerAssigned = true;

            // add session to conference
            serviceMediator.registerSessionInConference(sessionDTO);
            conferenceUpdated = true;


            // notify all system attendees and assign speaker of session creation
            SessionNotificationService.notifySessionCreation(
                    sessionDTO,
                    serviceMediator.findAllUsersById(conferenceDTO.getAttendees()),
                    serviceMediator.getUserById(sessionDTO.getSpeakerId())
            );

            LoggerUtil.getInstance().logInfo(String.format("Session '%s' has been successfully created.", session.getName()));
        } catch (RuntimeException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to create session '%s': %s", sessionDTO.getName(), e.getMessage()));

            // rolling back changes
            rollbackCreateOrUpdate(sessionDTO, sessionSaved, speakerAssigned, conferenceUpdated);

            throw new SessionException(e.getMessage());
        }
    }

    public void registerAttendee(String id, String attendeeId) {
        if (id == null || attendeeId == null || id.isEmpty() || attendeeId.isEmpty()) {
            throw new IllegalArgumentException("Invalid session id and/or attendee id.");
        }

        Optional<Session> sessionOptional = sessionRepository.findById(id);
        if (sessionOptional.isEmpty()) {
            throw new SessionException(String.format("Session with id '%s' does not exist.", id));
        }

        Session session = sessionOptional.get();
        session.registerAttendee(attendeeId);

        boolean sessionAddedToAttendee = false;
        try {
            // adding session reference to attendee's session schedule
            serviceMediator.addSessionToAttendee(mapToDTO(session), attendeeId);
            sessionAddedToAttendee = true;

            sessionRepository.save(session, session.getId());
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to register attendee '%s' to session '%s': %s", attendeeId, session.getName(), e.getMessage()));

            // rollback changes
            if (sessionAddedToAttendee) SessionRollbackService.rollbackSessionAddedToAttendee(session.getId(), attendeeId, serviceMediator::removeSessionFromAttendee);

            throw new SessionException(String.format("An error occurred when registering attendee to session: %s", e.getMessage()));
        }

        LoggerUtil.getInstance().logInfo(String.format("Successfully registered attendee '%s' to session '%s'.", attendeeId, id));
    }

    public void update(SessionDTO sessionDTO) {
        if (sessionDTO == null || sessionDTO.getId() == null || sessionDTO.getId().isEmpty()) {
            throw new IllegalArgumentException("SessionDTO and its ID cannot be null or empty for updates.");
        }

        boolean sessionSaved = false;
        boolean speakerAssigned = false;
        boolean conferenceUpdated = false;

        try {
            // prepare data for validation
            ConferenceDTO conferenceDTO = serviceMediator.getConferenceById(sessionDTO.getConferenceId());
            List<SessionDTO> conferenceSessions = findAllById(conferenceDTO.getSessions());

            // validate session data for update
            SessionValidatorService.validateData(sessionDTO, conferenceDTO, conferenceSessions, true);

            // convert DTO to domain object and save
            Session session = SessionFactory.create(sessionDTO);
            sessionSaved = sessionRepository.save(session, session.getId());
            if (!sessionSaved) {
                throw new SessionException("An unexpected error occurred while saving session data.");
            }

            // Assign session to speaker
            serviceMediator.assignNewSessionForSpeaker(sessionDTO);
            speakerAssigned = true;

            // Update session in conference
            serviceMediator.registerSessionInConference(sessionDTO);
            conferenceUpdated = true;

            SessionNotificationService.notifySessionChange(
                    sessionDTO,
                    serviceMediator.findAllUsersById(sessionDTO.getRegisteredAttendees()),
                    serviceMediator.getUserById(sessionDTO.getSpeakerId())
            );

            LoggerUtil.getInstance().logInfo(String.format("Session '%s' has been successfully updated.", session.getName()));
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to update session '%s': %s", sessionDTO.getName(), e.getMessage()));

            // rolling back changes
            rollbackCreateOrUpdate(sessionDTO, sessionSaved, speakerAssigned, conferenceUpdated);

            throw new SessionException(String.format("An error occurred when updating session: %s", e.getMessage()));
        }
    }

    public void addFeedback(String id, String feedbackId) {
        if (id == null || feedbackId == null || id.isEmpty() || feedbackId.isEmpty()) {
            throw new IllegalArgumentException("Invalid session id and/or feedback id.");
        }

        Optional<Session> sessionOptional = sessionRepository.findById(id);
        if (sessionOptional.isEmpty()) {
            throw new SessionException(String.format("Session with id '%s' does not exist.", id));
        }

        Session session = sessionOptional.get();
        session.addFeedback(feedbackId);

        sessionRepository.save(session, session.getId());
    }

    public void markAttendeeAsPresent(String id, String attendeeId) {
        if (id == null || attendeeId == null || id.isEmpty() || attendeeId.isEmpty()) {
            throw new IllegalArgumentException("Invalid session id and/or attendee id.");
        }

        Optional<Session> sessionOptional = sessionRepository.findById(id);
        if (sessionOptional.isEmpty()) {
            throw new SessionException(String.format("Session with id '%s' does not exist.", id));
        }

        Session session = sessionOptional.get();
        session.markAttendeeAsPresent(attendeeId);

        sessionRepository.save(session, session.getId());
    }

    public void markAttendeeAsAbsent(String id, String attendeeId) {
        if (id == null || attendeeId == null || id.isEmpty() || attendeeId.isEmpty()) {
            throw new IllegalArgumentException("Invalid session id and/or attendee id.");
        }

        Optional<Session> sessionOptional = sessionRepository.findById(id);
        if (sessionOptional.isEmpty()) {
            throw new SessionException(String.format("Session with id '%s' does not exist.", id));
        }

        Session session = sessionOptional.get();
        session.markAttendeeAsAbsent(attendeeId);

        sessionRepository.save(session, session.getId());
    }

    public SessionDTO getById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Invalid session id provided.");
        }

        return sessionRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new SessionException(String.format("A session with id '%s' could not be found.", id)));
    }

    public SessionDTO getUpcomingById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Invalid session id provided.");
        }

        return sessionRepository.findById(id)
                .filter(this::isUpcoming)
                .map(this::mapToDTO)
                .orElseThrow(() -> new SessionException(String.format("An upcoming session with id '%s' could not be found.", id)));
    }

    public List<SessionDTO> findAllById(Set<String> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("Invalid session ids.");
        }

        // batch fetch all sessions
        List<Optional<Session>> sessionOptionals = sessionRepository.findAllById(ids);

        // extract valid sessions
        List<Session> sessions = CollectionUtils.extractValidEntities(sessionOptionals);

        // map the session objects to session data transfer objects (DTO)
        return sessions.stream()
                       .map(this::mapToDTO)
                       .collect(Collectors.toList());
    }

    public List<SessionDTO> findAllUpcomingById(Set<String> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("Invalid session ids.");
        }

        // batch fetch all sessions
        List<Optional<Session>> sessionOptionals = sessionRepository.findAllById(ids);

        // extract valid sessions
        List<Session> sessions = CollectionUtils.extractValidEntities(sessionOptionals);

        // map the session objects to session data transfer objects (DTO)
        return sessions.stream()
                .filter(this::isUpcoming)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Session id cannot be null or empty.");
        }

        // retrieve session
        Optional<Session> sessionOptional = sessionRepository.findById(id);
        if (sessionOptional.isEmpty()) {
            throw new SessionException(String.format("Failed to delete session with id '%s' as it does not exist.", id));
        }

        Session session = sessionOptional.get();
        SessionDTO sessionDTO = mapToDTO(session);

        boolean sessionDeleted = false;
        boolean sessionRemovedFromConference = false;
        boolean sessionUnassignedFromSpeaker = false;
        try {
            // remove session reference from speaker schedule
            serviceMediator.unassignSessionFromSpeaker(session.getId(), session.getSpeakerId());
            sessionUnassignedFromSpeaker = true;

            // remove session reference from conference
            serviceMediator.removeSessionFromConference(session.getId(), session.getConferenceId());
            sessionRemovedFromConference = true;

            // delete session from repository
            sessionDeleted = sessionRepository.deleteById(id);
            if (!sessionDeleted) {
                throw new SessionException("An unexpected error occurred when deleting session. Please try again later.");
            }

            // notify attendees and speaker of session cancellation
            SessionNotificationService.notifySessionDeletion(
                    sessionDTO, serviceMediator.findAllUsersById(sessionDTO.getRegisteredAttendees()),
                    serviceMediator.getUserById(sessionDTO.getSpeakerId()));

        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to delete session with id '%s': %s", id, e.getMessage()));

            // rolling back changes
            rollbackDeletion(sessionDTO, sessionDeleted, sessionRemovedFromConference, sessionUnassignedFromSpeaker);

            // throwing original exception that caused the error
            throw new SessionException("An error occurred when deleting session: " + e.getMessage());
        }
    }

    public void deleteAllById(Set<String> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("Ids cannot be null.");
        }

        // deleting each session and all its associated references
        ids.forEach(this::deleteById);
    }

    public void unregisterAttendee(String id, String attendeeId) {
        if (id == null || attendeeId == null || id.isEmpty() || attendeeId.isEmpty()) {
            throw new IllegalArgumentException("Invalid session id and/or attendee id.");
        }

        Optional<Session> sessionOptional = sessionRepository.findById(id);
        if (sessionOptional.isEmpty()) {
            throw new SessionException(String.format("Session with id '%s' does not exist.", id));
        }

        Session session = sessionOptional.get();
        session.unregisterAttendee(attendeeId);

        boolean sessionRemovedFromAttendee = false;
        try {
            // remove session reference from attendee's registered sessions
            serviceMediator.removeSessionFromAttendee(id, attendeeId);
            sessionRemovedFromAttendee = true;

            sessionRepository.save(session, session.getId());
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to unregister attendee '%s' from session '%s': %s", attendeeId, id, e.getMessage() + e));

            // rollback changes
            if (sessionRemovedFromAttendee) SessionRollbackService.rollbackSessionRemovedFromAttendee(mapToDTO(session), attendeeId, serviceMediator::addSessionToAttendee);

            throw new SessionException(String.format("An error occurred when unregistering attendee from session: %s", e.getMessage() + e));
        }
    }

    // helper rollback methods
    private void rollbackCreateOrUpdate(SessionDTO sessionDTO, boolean sessionSaved, boolean speakerAssigned, boolean conferenceUpdated) {
        if (sessionSaved) SessionRollbackService.rollbackSessionSave(sessionDTO.getId(), this::delete);
        if (speakerAssigned) SessionRollbackService.rollbackSpeakerAssignment(sessionDTO.getId(), sessionDTO.getSpeakerId(), serviceMediator::unassignSessionFromSpeaker);
        if (conferenceUpdated) SessionRollbackService.rollbackConferenceUpdate(sessionDTO.getId(), sessionDTO.getConferenceId(), serviceMediator::removeSessionFromConference);
    }

    private void rollbackDeletion(SessionDTO sessionDTO, boolean sessionDeleted, boolean speakerUnassigned, boolean sessionRemovedFromConference) {
        if (sessionDeleted) SessionRollbackService.rollbackSessionDeletion(sessionDTO, this::save);
        if (speakerUnassigned) SessionRollbackService.rollbackSpeakerUnassignment(sessionDTO, serviceMediator::assignNewSessionForSpeaker);
        if (sessionRemovedFromConference) SessionRollbackService.rollbackSessionRemovalFromConference(sessionDTO, serviceMediator::registerSessionInConference);
    }


    // helper save and delete
    private void save(SessionDTO sessionDTO) {
        if (sessionDTO == null) {
            throw new IllegalArgumentException("SessionDTO cannot be null.");
        }

        Session session = SessionFactory.create(sessionDTO);
        boolean saved = sessionRepository.save(session, session.getId());
        if (!saved) {
            throw new SessionException("An error occurred when saving session.");
        }
    }

    private void delete(String id) {
        boolean isDeleted = sessionRepository.deleteById(id);
        if (!isDeleted) {
            throw new SessionException("An error occurred when deleting session.");
        }
    }

    private boolean isUpcoming(Session session) {
        return LocalDateTime.of(session.getDate(), session.getStartTime()).isAfter(LocalDateTime.now());
    }

    private SessionDTO mapToDTO(Session session) {
        return SessionDTO.builder(
                session.getConferenceId(),
                session.getSpeakerId(),
                session.getSpeakerName(),
                session.getName(),
                session.getRoom(),
                session.getDate() != null ? session.getDate() : LocalDate.now(),
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
