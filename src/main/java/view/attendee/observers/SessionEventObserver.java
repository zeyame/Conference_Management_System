package view.attendee.observers;

import dto.SessionDTO;

import java.util.List;
import java.util.function.BiConsumer;

public interface SessionEventObserver {

    void onGetSessionsForConference(String conferenceId, BiConsumer<List<SessionDTO>, String> callback);
    void onGetSession(String sessionId, BiConsumer<SessionDTO, String> callback);
}
