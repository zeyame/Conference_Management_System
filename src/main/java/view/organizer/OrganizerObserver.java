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
    void onViewAttendeesRequest(String conferenceId);
    void onViewSessionsRequest(String conferenceId);
    void onViewSpeakersRequest(String conferenceId);
    void onViewConferenceFeedbackRequest(String conferenceId);
    void onNavigateBackRequest();

    // View Sessions Page events
    void onManageSessionRequest(String sessionId);
    void onAddSessionRequest(String conferenceId);

    // Add Session Page events
    void onSubmitSessionFormRequest(SessionDTO sessionDTO);

    // Manage Session Page Events
    void onViewSessionAttendeesRequest(String sessionId);
    void onViewSessionAttendanceRequest(String sessionId);
    void onViewSessionFeedbackRequest(String sessionId);
    void onEditSessionRequest(SessionDTO sessionDTO);
    void onDeleteSessionRequest(String sessionId);

    // Edit Session Page events
    void onUpdateSessionFormRequest(SessionDTO updatedSessionDTO);

}
