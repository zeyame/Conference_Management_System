package view.attendee.DataCallback;

import dto.ConferenceDTO;

public interface ViewUpcomingConferenceDataCallback {

    void onConferenceFetched(ConferenceDTO conferenceDTO);

    void onOrganizerNameFetched(String organizerName);

    void onError(String errorMessage);
}
