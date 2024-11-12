package ui.organizer;

import dto.ConferenceDTO;

import java.util.List;
import java.util.Map;

public interface OrganizerCallback {
    List<ConferenceDTO> onGetManagedConferencesRequest(String email);
    void onAddConferenceRequest();

}
