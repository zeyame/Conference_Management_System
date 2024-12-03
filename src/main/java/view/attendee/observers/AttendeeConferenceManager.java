package view.attendee.observers;

import controller.AttendeeController;
import dto.ConferenceDTO;
import dto.UserDTO;
import response.ResponseEntity;
import util.LoggerUtil;
import view.attendee.DataCallback.HomePageDataCallback;
import view.attendee.DataCallback.ViewRegisteredConferencesCallback;
import view.attendee.DataCallback.ViewUpcomingConferenceDataCallback;

import java.util.List;

public class AttendeeConferenceManager implements ConferenceEventObserver {

    private final AttendeeController attendeeController;

    public AttendeeConferenceManager(AttendeeController attendeeController) {
        this.attendeeController = attendeeController;
    }

    @Override
    public void onRegisterForAConference(String attendeeId, String conferenceId, ViewUpcomingConferenceDataCallback viewUpcomingConferenceDataCallback) {
        LoggerUtil.getInstance().logInfo(String.format("Request to register attendee with id '%s' for conference '%s' received.", attendeeId, conferenceId));

        ResponseEntity<Void> registerAttendeeResponse = attendeeController.registerForConference(attendeeId, conferenceId);
        if (registerAttendeeResponse.isSuccess()) {
            viewUpcomingConferenceDataCallback.onRegisteredForConference();
        } else {
            viewUpcomingConferenceDataCallback.onError(registerAttendeeResponse.getErrorMessage());
        }

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
    public void onConferenceSelected(String conferenceId, ViewUpcomingConferenceDataCallback viewUpcomingConferenceDataCallback) {
        LoggerUtil.getInstance().logInfo(String.format("Request to fetch data for conference with id '%s' received.", conferenceId));

        ResponseEntity<ConferenceDTO> conferenceResponse = attendeeController.getConference(conferenceId);
        if (conferenceResponse.isSuccess()) {
            viewUpcomingConferenceDataCallback.onConferenceFetched(conferenceResponse.getData());
        } else {
            viewUpcomingConferenceDataCallback.onError(conferenceResponse.getErrorMessage());
        }
    }

    @Override
    public void onGetOrganizerName(String organizerId, ViewUpcomingConferenceDataCallback viewUpcomingConferenceDataCallback) {
        LoggerUtil.getInstance().logInfo(String.format("Request to fetch organizer name for organizer with id '%s' received.", organizerId));

        ResponseEntity<String> organizerNameResponse = attendeeController.getOrganizerName(organizerId);
        if (organizerNameResponse.isSuccess()) {
            viewUpcomingConferenceDataCallback.onOrganizerNameFetched(organizerNameResponse.getData());
        } else {
            viewUpcomingConferenceDataCallback.onError(organizerNameResponse.getErrorMessage());
        }
    }

    @Override
    public void onGetRegisteredConferences(String attendeeId, ViewRegisteredConferencesCallback viewRegisteredConferencesCallback) {
        LoggerUtil.getInstance().logInfo(String.format("Request to get registered conferences for attendee with id '%s' received.", attendeeId));

        ResponseEntity<List<ConferenceDTO>> registeredConferencesResponse = attendeeController.getRegisteredConferences(attendeeId);
        if (registeredConferencesResponse.isSuccess()) {
            viewRegisteredConferencesCallback.onRegisteredConferencesFetched(registeredConferencesResponse.getData());
        } else {
            viewRegisteredConferencesCallback.onError(registeredConferencesResponse.getErrorMessage());
        }
    }

}
