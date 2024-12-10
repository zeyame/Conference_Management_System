package view.observers;

import dto.ConferenceDTO;
import dto.FeedbackDTO;
import dto.UserDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ConferenceEventObserver {
    void onRegisterForAConference(String attendeeId, String conferenceId, Consumer<String> callback);
    void onSubmitFeedback(FeedbackDTO feedbackDTO, Consumer<String> callback);
    void onGetUpcomingConferences(String attendeeId, BiConsumer<List<ConferenceDTO>, String> callback);
    void onConferenceSelected(String conferenceId, BiConsumer<ConferenceDTO, String> callback);
    void onGetOrganizerName(String organizerId, BiConsumer<String, String> callback);
    void onGetSpeakers(String conferenceId, BiConsumer<List<UserDTO>, String> callback);
    void onGetSpeakerBios(Set<String> speakerIds, BiConsumer<Map<String, String>, String> callback);
    void onGetRegisteredConferences(String attendeeId, BiConsumer<List<ConferenceDTO>, String> callback);
    void onLeaveConference(String attendeeId, String conferenceId, Consumer<String> callback);
}
