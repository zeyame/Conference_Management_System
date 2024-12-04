package view.attendee.observers;

import controller.AttendeeController;
import dto.SessionDTO;
import response.ResponseEntity;
import util.LoggerUtil;

import java.util.List;
import java.util.function.BiConsumer;

public class AttendeeSessionManager implements SessionEventObserver {

    private final AttendeeController attendeeController;

    public AttendeeSessionManager(AttendeeController attendeeController) {
        this.attendeeController = attendeeController;
    }

    @Override
    public void onGetSessionsForConference(String conferenceId, BiConsumer<List<SessionDTO>, String> callback) {
        LoggerUtil.getInstance().logInfo(String.format("Attendee request to view sessions in conference with id '%s' received.", conferenceId));

        ResponseEntity<List<SessionDTO>> sessionsResponse = attendeeController.getConferenceSessions(conferenceId);
        if (sessionsResponse.isSuccess()) {
            callback.accept(sessionsResponse.getData(), null);
        } else {
            callback.accept(null, sessionsResponse.getErrorMessage());
        }
    }
}
