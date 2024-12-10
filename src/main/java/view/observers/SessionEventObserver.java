package view.observers;

import dto.FeedbackDTO;
import dto.SessionDTO;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface SessionEventObserver {
    void onRegisterForSession(String attendeeId, String sessionId, Consumer<String> callback);

    void onSubmitFeedback(FeedbackDTO feedbackDTO, Consumer<String> callback);

    void onGetSessionsInConference(String conferenceId, BiConsumer<List<SessionDTO>, String> callback);

    void onGetPersonalSchedule(String attendeeId, String conferenceId, BiConsumer<List<SessionDTO>, String> callback);

    void onGetUpcomingSessionsForConference(String conferenceId, BiConsumer<List<SessionDTO>, String> callback);

    void onGetSession(String sessionId, BiConsumer<SessionDTO, String> callback);

    void onGetSpeakerSessions(String speakerId, BiConsumer<List<SessionDTO>, String> callback);

    void onLeaveSession(String sessionId, String attendeeId, Consumer<String> callback);

    void onUpdateSpeakerBioRequest(String speakerId, String newBio, Consumer<String> callback);
}
