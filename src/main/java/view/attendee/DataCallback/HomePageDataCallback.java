package view.attendee.DataCallback;

import dto.ConferenceDTO;

import java.util.List;

public interface HomePageDataCallback {

    void onUpcomingConferencesFetched(List<ConferenceDTO> conferenceDTOs);
    void onError(String errorMessage);
}
