package view.organizer;

import dto.ConferenceDTO;
import dto.SessionDTO;

import java.util.List;


public interface OrganizerObserver {

    // Home Page events
    List<ConferenceDTO> onGetManagedConferencesRequest(String email);
    void onManageConferenceRequest(String conferenceId);
    void onAddConferenceRequest(String organizerName);

    // Add Conference Page events
    void onSubmitConferenceFormRequest(ConferenceDTO conferenceDTO);

    // Manage Conference Page events
    void onEditConferenceRequest();
    void onDeleteConferenceRequest();
    void onViewAttendeesRequest(String conferenceId, String conferenceName);
    void onViewSessionsRequest(String conferenceId, String conferenceName);
    void onViewSpeakersRequest();
    void onViewFeedbackRequest();
    void onNavigateBackRequest();

    // View Sessions Page events
    void onManageSessionRequest(String sessionId);
    void onAddSessionRequest(String conferenceId, String conferenceName);

    // Add Session Page events
    void onSubmitSessionFormRequest(SessionDTO sessionDTO, String conferenceName);

    // Manage Session Page Events
    void onViewSessionAttendeesRequest(String sessionId, String sessionName);

}
