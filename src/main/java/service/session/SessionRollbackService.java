package service.session;

import domain.model.Session;
import dto.SessionDTO;
import exception.ConferenceException;
import exception.SessionException;
import exception.UserException;
import service.ConferenceService;
import service.UserService;
import util.LoggerUtil;
import java.time.LocalDateTime;
import java.util.function.Predicate;

public class SessionRollbackService {

    private final UserService userService;
    private final ConferenceService conferenceService;
    private final Predicate<String> deleteSessionAction;
    private final Predicate<SessionDTO> saveSessionAction;

    public SessionRollbackService(UserService userService, ConferenceService conferenceService,
                                  Predicate<String> deleteSessionAction, Predicate<SessionDTO> saveSessionAction) {
        this.userService = userService;
        this.conferenceService = conferenceService;
        this.deleteSessionAction = deleteSessionAction;
        this.saveSessionAction = saveSessionAction;
    }

    public void rollbackSessionSave(SessionDTO sessionDTO) {
        boolean sessionDeleted = deleteSessionAction.test(sessionDTO.getId());
        if (!sessionDeleted) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Could not delete session '%s' from repository.", sessionDTO.getId()));
            throw SessionException.rollbackFailure("Failed to delete session from repository during rollback of session creation/update.");
        }
        LoggerUtil.getInstance().logInfo(String.format("Rollback: Session '%s' successfully deleted from repository.", sessionDTO.getId()));
    }
    public void rollbackSessionDeletion(SessionDTO sessionDTO) {
        boolean sessionRestored = saveSessionAction.test(sessionDTO);
        if (!sessionRestored) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Could not restore session '%s' to repository.", sessionDTO.getId()));
            throw SessionException.rollbackFailure("Failed to restore session to repository during rollback of session deletion.");
        }
        LoggerUtil.getInstance().logInfo(String.format("Rollback: Session '%s' successfully restored to repository.", sessionDTO.getId()));
    }

    public void rollbackSpeakerAssignment(SessionDTO sessionDTO) {
        try {
            userService.unassignSessionFromSpeaker(sessionDTO.getSpeakerId(), sessionDTO.getId());
            LoggerUtil.getInstance().logInfo(String.format("Rollback: Session '%s' successfully unassigned from speaker.", sessionDTO.getId()));
        } catch (UserException e) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Could not unassign session '%s' from speaker: %s", sessionDTO.getId(), e.getMessage()));
            throw SessionException.rollbackFailure("Failed to unassign session from speaker during rollback.");
        }
    }

    public void rollbackSpeakerUnassignment(SessionDTO sessionDTO) {
        try {
            userService.assignNewSessionForSpeaker(
                    sessionDTO.getSpeakerId(), sessionDTO.getId(),
                    LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime()),
                    LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getEndTime())
            );
            LoggerUtil.getInstance().logInfo(String.format("Rollback: Session '%s' successfully re-assigned to speaker.", sessionDTO.getId()));
        } catch (UserException e) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Could not re-assign session '%s' to speaker: %s", sessionDTO.getId(), e.getMessage()));
            throw SessionException.rollbackFailure("Failed to re-assign session to speaker during rollback of session deletion.");
        }
    }

    public void rollbackConferenceUpdate(SessionDTO sessionDTO) {
        try {
            conferenceService.removeSession(sessionDTO.getConferenceId(), sessionDTO.getId());
            LoggerUtil.getInstance().logInfo(String.format("Rollback: Session '%s' successfully removed from conference.", sessionDTO.getId()));
        } catch (ConferenceException e) {
            LoggerUtil.getInstance().logError(String.format("Rollback failed: Could not remove session '%s' from conference: %s", sessionDTO.getId(), e.getMessage()));
            throw SessionException.rollbackFailure("Failed to remove session from conference during rollback.");
        }
    }


}
