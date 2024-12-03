package view.attendee.observers;

import view.attendee.DataCallback.HomePageDataCallback;
import view.attendee.DataCallback.ViewRegisteredConferencesCallback;
import view.attendee.DataCallback.ViewUpcomingConferenceDataCallback;

public interface ConferenceEventObserver {
    void onRegisterForAConference(String attendeeId, String conferenceId, ViewUpcomingConferenceDataCallback viewUpcomingConferenceDataCallback);
    void onGetUpcomingConferences(String attendeeId, HomePageDataCallback homePageDataCallback);
    void onConferenceSelected(String conferenceId, ViewUpcomingConferenceDataCallback viewUpcomingConferenceDataCallback);

    void onGetOrganizerName(String organizerId, ViewUpcomingConferenceDataCallback viewUpcomingConferenceDataCallback);

    void onGetRegisteredConferences(String attendeeId, ViewRegisteredConferencesCallback viewRegisteredConferencesCallback);
}
