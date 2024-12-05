package view.attendee.observers;

import dto.SessionDTO;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface SessionEventObserver {
    void onRegisterForSession(String attendeeId, String sessionId, Consumer<String> callback);
    void onGetUpcomingSessionsForConference(String conferenceId, BiConsumer<List<SessionDTO>, String> callback);
    void onGetSession(String sessionId, BiConsumer<SessionDTO, String> callback);
    void onLeaveSession(String sessionId, String attendeeId, Consumer<String> callback);
}
