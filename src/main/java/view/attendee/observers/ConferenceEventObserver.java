package view.attendee.observers;

import view.attendee.DataCallback.HomePageDataCallback;

public interface ConferenceEventObserver {
    void onGetUpcomingConferences(String attendeeId, HomePageDataCallback homePageDataCallback);
    void onConferenceSelected(String conferenceId);
}
