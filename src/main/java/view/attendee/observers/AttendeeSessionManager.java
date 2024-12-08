package view.attendee.observers;

import controller.AttendeeController;
import dto.FeedbackDTO;
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
    public void onSubmitFeedback(FeedbackDTO feedbackDTO, Consumer<String> callback) {
        LoggerUtil.getInstance().logInfo(String.format("Attendee request to submit feedback for session '%s' received.", feedbackDTO.getSessionId()));

        ResponseEntity<Void> submitSessionFeedbackResponse = attendeeController.submitFeedback(feedbackDTO);
        if (submitSessionFeedbackResponse.isSuccess()) {
            callback.accept(null);
        } else {
            callback.accept(submitSessionFeedbackResponse.getErrorMessage());
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
    public void onGetPersonalSchedule(String attendeeId, String conferenceId, BiConsumer<List<SessionDTO>, String> callback) {
        LoggerUtil.getInstance().logInfo(String.format("Attendee request to retrieve personal schedule for conference '%s' received.", conferenceId));

        ResponseEntity<List<SessionDTO>> personalScheduleResponse = attendeeController.getPersonalSchedule(attendeeId, conferenceId);
        if (personalScheduleResponse.isSuccess()) {
            callback.accept(personalScheduleResponse.getData(), null);
        } else {
            callback.accept(null, personalScheduleResponse.getErrorMessage());
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
