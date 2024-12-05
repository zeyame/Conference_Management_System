package service.session;

import dto.SessionDTO;
import exception.SessionException;
import util.LoggerUtil;

import java.time.LocalDateTime;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SessionRollbackService {

    public static void rollbackSessionSave(String sessionId, Consumer<String> deleteSessionAction) {
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty.");
        }

        try {
            deleteSessionAction.accept(sessionId);
            LoggerUtil.getInstance().logInfo(String.format("Rollback: Session '%s' successfully deleted from repository.", sessionId));
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Could not delete session '%s' from repository: %s", sessionId, e.getMessage()));
        }
    }

    public static void rollbackSessionDeletion(SessionDTO sessionDTO, Consumer<SessionDTO> saveSessionAction) {
        if (sessionDTO == null) {
            throw new IllegalArgumentException("SessionDTO cannot be null.");
        }

        try {
            saveSessionAction.accept(sessionDTO);
            LoggerUtil.getInstance().logInfo(String.format("Rollback: Session '%s' successfully restored to repository.", sessionDTO.getId()));
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Could not restore session '%s' to repository: %s", sessionDTO.getId(), e.getMessage()));
        }
    }

    public static void rollbackSpeakerAssignment(String sessionId, String speakerId, BiConsumer<String, String> unassignAction) {
        if (sessionId == null || speakerId == null || sessionId.isEmpty() || speakerId.isEmpty()) {
            throw new IllegalArgumentException("Session and speaker ids cannot be null or empty.");
        }

        try {
            unassignAction.accept(sessionId, speakerId);
            LoggerUtil.getInstance().logInfo(String.format("Rollback: Session '%s' successfully unassigned from speaker '%s'.", sessionId, speakerId));
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Could not unassign session '%s' from speaker '%s': %s", sessionId, speakerId, e.getMessage()));
        }
    }

    public static void rollbackSpeakerUnassignment(SessionDTO sessionDTO, Consumer<SessionDTO> assignAction) {
        if (sessionDTO == null) {
            throw new IllegalArgumentException("SessionDTO cannot be null.");
        }

        try {
            assignAction.accept(sessionDTO);
            LoggerUtil.getInstance().logInfo(String.format("Rollback: Session '%s' successfully re-assigned to speaker.", sessionDTO.getId()));
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Could not re-assign session '%s' to speaker: %s", sessionDTO.getId(), e.getMessage()));
        }
    }

    public static void rollbackConferenceUpdate(String sessionId, String conferenceId, BiConsumer<String, String> removeSessionAction) {
        if (sessionId == null || conferenceId == null || sessionId.isEmpty() || conferenceId.isEmpty()) {
            throw new IllegalArgumentException("Session and speaker ids cannot be null or empty.");
        }

        try {
            removeSessionAction.accept(sessionId, conferenceId);
            LoggerUtil.getInstance().logInfo(String.format("Rollback: Session '%s' successfully removed from conference '%s'.", sessionId, conferenceId ));
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Could not remove session '%s' from conference '%s': %s", sessionId, conferenceId, e.getMessage()));
        }
    }

    public static void rollbackSessionRemovalFromConference(SessionDTO sessionDTO, Consumer<SessionDTO> registerSessionAction) {
        if (sessionDTO == null) {
            throw new IllegalArgumentException("SessionDTO cannot be null.");
        }

        try {
            registerSessionAction.accept(sessionDTO);
            LoggerUtil.getInstance().logInfo(String.format("Rollback: Session '%s' successfully added back to conference '%s'.", sessionDTO.getId(), sessionDTO.getConferenceId()));
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Could not add session '%s' to conference '%s': %s", sessionDTO.getId(), sessionDTO.getConferenceId(), e.getMessage()));
        }
    }

    public static void rollbackSessionAddedToAttendee(String sessionId, String attendeeId, BiConsumer<String, String> removeSessionFromAttendeeAction) {
        if (sessionId == null || attendeeId == null || sessionId.isEmpty() || attendeeId.isEmpty()) {
            throw new IllegalArgumentException("Invalid session id and/or attendee id.");
        }

        try {
            removeSessionFromAttendeeAction.accept(sessionId, attendeeId);
            LoggerUtil.getInstance().logInfo(String.format("Rollback successful: Session '%s' removed from attendee's '%s' schedule.", sessionId, attendeeId));
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Session '%s' could not be removed from attendee's '%s' " +
                    "schedule during rollback operation: %s", sessionId, attendeeId, e.getMessage()));
        }
    }

    public static void rollbackSessionRemovedFromAttendee(SessionDTO sessionDTO, String attendeeId, BiConsumer<SessionDTO, String> addSessionToAttendeeAction) {
        if (attendeeId == null || attendeeId.isEmpty()) {
            throw new IllegalArgumentException("Invalid session id and/or attendee id.");
        }

        if (sessionDTO == null) {
            throw new IllegalArgumentException("Invalid session data.");
        }

        try {
            addSessionToAttendeeAction.accept(sessionDTO, attendeeId);
            LoggerUtil.getInstance().logInfo(String.format("Rollback successful: Session '%s' add to attendee's '%s' schedule" +
                    " during rollback operation.", sessionDTO.getName(), attendeeId));
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Session '%s' could not be added to attendee's '%s' " +
                    "schedule during rollback operation: %s", sessionDTO.getName(), attendeeId, e.getMessage()));
        }
    }
}
