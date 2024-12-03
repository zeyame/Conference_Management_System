package view.attendee.observers;

import dto.ConferenceDTO;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ConferenceEventObserver {
    void onRegisterForAConference(String attendeeId, String conferenceId, Consumer<String> callback);
    void onGetUpcomingConferences(String attendeeId, BiConsumer<List<ConferenceDTO>, String> callback);
    void onConferenceSelected(String conferenceId, BiConsumer<ConferenceDTO, String> callback);
    void onGetOrganizerName(String organizerId, BiConsumer<String, String> callback);

    void onGetRegisteredConferences(String attendeeId, BiConsumer<List<ConferenceDTO>, String> callback);
}
