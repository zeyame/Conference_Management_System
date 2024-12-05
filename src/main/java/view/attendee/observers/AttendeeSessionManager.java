package view.attendee.observers;

import controller.AttendeeController;
import dto.SessionDTO;
import response.ResponseEntity;
import util.LoggerUtil;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AttendeeSessionManager implements SessionEventObserver {

    private final AttendeeController attendeeController;

    public AttendeeSessionManager(AttendeeController attendeeController) {
        this.attendeeController = attendeeController;
    }

    @Override
    public void onRegisterForSession(String attendeeId, String sessionId, Consumer<String> callback) {
        LoggerUtil.getInstance().logInfo(String.format("Attendee request to register for session with id '%s' received.", sessionId));

        ResponseEntity<Void> registerForSessionResponse = attendeeController.registerForSession(attendeeId, sessionId);
        if (registerForSessionResponse.isSuccess()) {
            callback.accept(null);
        } else {
            callback.accept(registerForSessionResponse.getErrorMessage());
        }
    }

    @Override
    public void onGetUpcomingSessionsForConference(String conferenceId, BiConsumer<List<SessionDTO>, String> callback) {
        LoggerUtil.getInstance().logInfo(String.format("Attendee request to view sessions in conference with id '%s' received.", conferenceId));

        ResponseEntity<List<SessionDTO>> sessionsResponse = attendeeController.getUpcomingConferenceSessions(conferenceId);
        if (sessionsResponse.isSuccess()) {
            callback.accept(sessionsResponse.getData(), null);
        } else {
            callback.accept(null, sessionsResponse.getErrorMessage());
        }
    }

    @Override
    public void onGetSession(String sessionId, BiConsumer<SessionDTO, String> callback) {
        LoggerUtil.getInstance().logInfo(String.format("Attendee request to view session with id '%s' received.", sessionId));

        ResponseEntity<SessionDTO> sessionResponse = attendeeController.getSession(sessionId);
        if (sessionResponse.isSuccess()) {
            callback.accept(sessionResponse.getData(), null);
        } else {
            callback.accept(null, sessionResponse.getErrorMessage());
        }
    }

    @Override
    public void onLeaveSession(String sessionId, String attendeeId, Consumer<String> callback) {
        LoggerUtil.getInstance().logInfo(String.format("Request from attendee with id '%s' to leave session with id '%s' received.", attendeeId, sessionId));

        ResponseEntity<Void> sessionLeaveResponse = attendeeController.leaveSession(sessionId, attendeeId);
        if (sessionLeaveResponse.isSuccess()) {
            callback.accept(null);
        } else {
            callback.accept(sessionLeaveResponse.getErrorMessage());
        }
    }
}
