package view.organizer;

import dto.ConferenceDTO;

import java.util.List;


public interface OrganizerObserver {

    // Home Page events
    List<ConferenceDTO> onGetManagedConferencesRequest(String email);
    void onManageConferenceRequest(String conferenceId);
    void onAddConferenceRequest();

    // Add Conference Page events
    void onSubmitConferenceFormRequest(ConferenceDTO conferenceDTO);

    // Manage Conference Page events
    void onEditConferenceRequest();
    void onDeleteConferenceRequest();
    void onViewAttendeesRequest(String conferenceId, String conferenceName);
    void onViewSessionsRequest();
    void onViewSpeakersRequest();
    void onViewFeedbackRequest();
    void onNavigateBackRequest();
}
