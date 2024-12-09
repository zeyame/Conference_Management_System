package domain.model;

public class Certificate {

    private final String id;
    private final String attendeeId;
    private final String conferenceId;

    public Certificate(String id, String attendeeId, String conferenceId) {
        this.id = id;
        this.attendeeId = attendeeId;
        this.conferenceId = conferenceId;
    }

    public String getId() {
        return id;
    }

    public String getAttendeeId() {
        return attendeeId;
    }

    public String getConferenceId() {
        return conferenceId;
    }
}
