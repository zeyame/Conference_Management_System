package service.conference;

import exception.ConferenceException;
import util.LoggerUtil;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ConferenceRollbackService {

    public static void rollbackSave(String conferenceId, Consumer<String> deleteAction) {
        try {
            deleteAction.accept(conferenceId);
            LoggerUtil.getInstance().logInfo(String.format("Successfully deleted conference with id '%s' during rollback save operation.", conferenceId));
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("An unexpected error occurred when deleting conference with " +
                    "id '%s' during rollback save operation: %s", conferenceId, e.getMessage()));
        }
    }

    public static void rollbackAssignmentToOrganzer(String conferenceId, String organizerId, BiConsumer<String, String> unassignAction) {
        try {
            unassignAction.accept(conferenceId, organizerId);
            LoggerUtil.getInstance().logInfo(String.format("Successfully unassigned conference with id '%s' from organizer's '%s' managed conferences during rollback operation.", conferenceId, organizerId));
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("An unexpected error occurred when unassigning conference with id " +
                    "'%s' from organizer '%s' managed conferences during rollback operation: %s", conferenceId, organizerId, e.getMessage()));
        }
    }

    public static void rollbackAddingConferenceToAttendee(String conferenceId, String attendeeId, BiConsumer<String, String> removeConferenceAction) {
        try {
            removeConferenceAction.accept(conferenceId, attendeeId);
            LoggerUtil.getInstance().logInfo(String.format("Successfully removed conference with id '%s' from attendee " +
                    "'%s' registered conferences during rollback operation.", conferenceId, attendeeId));
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("An unexpected error occurred when removing conference '%s' " +
                    "from attendee '%s' during rollback operation: %s", conferenceId, attendeeId, e.getMessage()));
        }
    }
}
