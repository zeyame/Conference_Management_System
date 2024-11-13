package ui.organizer;

import dto.ConferenceDTO;

import java.util.List;


public interface OrganizerObserver {
    List<ConferenceDTO> onGetManagedConferencesRequest(String email);
    void onAddConferenceRequest();

}
