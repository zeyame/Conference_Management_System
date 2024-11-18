package view.organizer;

import dto.ConferenceDTO;
import dto.ConferenceFormDTO;

import java.util.List;


public interface OrganizerObserver {

    // Home Page events
    List<ConferenceDTO> onGetManagedConferencesRequest(String email);
    void onManageConferenceRequest(String conferenceId);
    void onAddConferenceRequest();

    // Add Conference Page events
    void onSubmitConferenceFormRequest(ConferenceFormDTO conferenceFormDTO);

    // Manage Conference Page events
    void onEditConferenceRequest();
    void onDeleteConferenceRequest();
    void onViewAttendeesRequest();
    void onViewSessionsRequest();
    void onViewSpeakersRequest();
    void onViewFeedbackRequest();
}
