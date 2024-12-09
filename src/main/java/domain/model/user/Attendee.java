package domain.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import exception.SessionException;

import java.time.LocalDateTime;
import java.util.*;

public class Attendee extends User {
    @JsonProperty("schedule")
    private final Map<LocalDateTime, String> schedule;          // K: Session Time, V: Session ID
    private final Set<String> registeredConferences;
    private final Set<String> submittedFeedback;

    // no-arg constructor for JSON serialization/de-serialization
    private Attendee() {
        super(null, null, null, null, null);
        this.schedule = new TreeMap<>();
        this.registeredConferences = new HashSet<>();
        this.submittedFeedback = new HashSet<>();
    }

    public Attendee(String id, String email, String name, String hashedPassword, UserRole userRole) {
        super(id, email, name, hashedPassword, userRole);
        this.schedule = new TreeMap<>();
        this.registeredConferences = new HashSet<>();
        this.submittedFeedback = new HashSet<>();
    }

    public void addRegisteredConference(String conferenceId) {
        this.registeredConferences.add(conferenceId);
    }

    public void removeRegisteredConference(String conferenceId) {
        this.registeredConferences.remove(conferenceId);
    }

    public void addSession(String sessionId, LocalDateTime sessionStartTime) {
        this.schedule.put(sessionStartTime, sessionId);
    }

    public void removeSession(String sessionId) {
        Optional<LocalDateTime> optionalLocalDateTime = this.schedule.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(sessionId))
                .map(Map.Entry::getKey)
                .findFirst();

        if (optionalLocalDateTime.isPresent()) {
            this.schedule.remove(optionalLocalDateTime.get());
        } else {
            throw new SessionException(String.format("Session id '%s' is not in attendee's '%s' personal schedule.", sessionId, this.getName()));
        }
    }
    public Map<LocalDateTime, String> getSchedule() {
        return new TreeMap<>(this.schedule);
    }           // defensive copy

    public Set<String> getRegisteredConferences() {
        return new HashSet<>(this.registeredConferences);
    }       // defensive copy
}
