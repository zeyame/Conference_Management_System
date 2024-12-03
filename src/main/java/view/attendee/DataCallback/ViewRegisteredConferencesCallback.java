package view.attendee.DataCallback;

import dto.ConferenceDTO;

import java.util.List;

public interface ViewRegisteredConferencesCallback {

    void onRegisteredConferencesFetched(List<ConferenceDTO> conferenceDTO);

    void onError(String errorMessage);
}
