package domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Attendee extends User {
    @JsonProperty("schedule")
    private final Map<LocalDateTime, String> schedule;          // K: Session Time, V: Session ID
    private final Set<String> registeredConferences;

    // no-arg constructor for JSON serialization/de-serialization
    private Attendee() {
        super(null, null, null, null, null);
        this.schedule = new TreeMap<>();
        this.registeredConferences = new HashSet<>();
    }

    public Attendee(String id, String email, String name, String hashedPassword, UserRole userRole) {
        super(id, email, name, hashedPassword, userRole);
        this.schedule = new TreeMap<>();
        this.registeredConferences = new HashSet<>();
    }

    public void addRegisteredConference(String conferenceId) {
        this.registeredConferences.add(conferenceId);
    }

    public void removeRegisteredConference(String conferenceId) {
        this.registeredConferences.remove(conferenceId);
    }

    public Map<LocalDateTime, String> getSchedule() {
        return schedule;
    }

    public Set<String> getRegisteredConferences() {
        return registeredConferences;
    }
}
