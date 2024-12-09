package view.organizer;

import dto.ConferenceDTO;
import dto.FeedbackDTO;
import dto.SessionDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;


public interface OrganizerObserver {

    void onNavigateBackRequest();

    // Home Page events
    List<ConferenceDTO> onGetManagedConferencesRequest(String email);
    void onManageConferenceRequest(String conferenceId);
    void onAddConferenceRequest(String organizerName);

    // Add Conference Page events
    void onSubmitConferenceFormRequest(ConferenceDTO conferenceDTO);

    // Manage Conference Page events
    void onEditConferenceRequest(ConferenceDTO conferenceDTO);
    void onDeleteConferenceRequest(String conferenceId);
    void onViewConferenceAttendeesRequest(String conferenceId);
    void onViewSessionsRequest(String conferenceId);
    void onViewSpeakersRequest(String conferenceId);
    void onViewConferenceFeedbackRequest(String conferenceId);

    // Edit Conference Page Events
    void onUpdateConferenceRequest(ConferenceDTO conferenceDTO);

    // View Sessions Page events
    void onManageUpcomingSessionRequest(String sessionId);
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
    void onUpdateSessionRequest(SessionDTO updatedSessionDTO);

    // View Session Registered Attendees Page events
    void onMarkAttendeeAsPresentRequest(String sessionId, String attendeeId);
    void onMarkAttendeeAsAbsentRequest(String sessionId, String attendeeId);

    // View Speakers Page events
    void onGetSpeakerBiosRequest(Set<String> speakerIds, BiConsumer<Map<String, String>, String> callback);
    void onViewSpeakerFeedbackRequest(String speakerId, String speakerName);

}
