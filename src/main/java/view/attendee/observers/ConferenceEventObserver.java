package view.attendee.observers;

import view.attendee.DataCallback.HomePageDataCallback;
import view.attendee.DataCallback.ViewRegisteredConferencesCallback;

public interface ConferenceEventObserver {
    void onGetUpcomingConferences(String attendeeId, HomePageDataCallback homePageDataCallback);
    void onConferenceSelected(String conferenceId);

    void onGetRegisteredConferences(String attendeeId, ViewRegisteredConferencesCallback viewRegisteredConferencesCallback);
}
