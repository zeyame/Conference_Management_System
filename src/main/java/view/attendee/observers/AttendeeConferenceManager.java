package view.attendee.observers;

import controller.AttendeeController;
import dto.ConferenceDTO;
import response.ResponseEntity;
import util.LoggerUtil;
import view.attendee.DataCallback.HomePageDataCallback;

import java.util.List;

public class AttendeeConferenceManager implements ConferenceEventObserver {

    private final AttendeeController attendeeController;

    public AttendeeConferenceManager(AttendeeController attendeeController) {
        this.attendeeController = attendeeController;
    }

    @Override
    public void onGetUpcomingConferences(String attendeeId, HomePageDataCallback homePageDataCallback) {
        LoggerUtil.getInstance().logInfo(String.format("Request to fetch upcoming conferences for attendee with id  '%s' received.", attendeeId));

        ResponseEntity<List<ConferenceDTO>> conferencesResponse = attendeeController.getUpcomingConferences(attendeeId);
        if (conferencesResponse.isSuccess()) {
            homePageDataCallback.onUpcomingConferencesFetched(conferencesResponse.getData());
        } else {
            homePageDataCallback.onError(conferencesResponse.getErrorMessage());
        }
    }

    @Override
    public void onConferenceSelected(String conferenceId) {

    }

}
