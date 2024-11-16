package ui.organizer;

import dto.ConferenceDTO;
import dto.ConferenceFormDTO;

import java.util.List;


public interface OrganizerObserver {
    List<ConferenceDTO> onGetManagedConferencesRequest(String email);
    void onAddConferenceRequest();

    void onSubmitConferenceFormRequest(ConferenceFormDTO conferenceFormDTO);

}
