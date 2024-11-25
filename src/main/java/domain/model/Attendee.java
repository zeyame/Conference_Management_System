package domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

public class Attendee extends User {
    @JsonProperty("schedule")
    private final Map<LocalDateTime, String> schedule;          // K: Session Time, V: Session ID

    public Attendee() {
        super(null, null, null, null, null);
        this.schedule = new TreeMap<>();
    }

    public Attendee(String id, String email, String name, String hashedPassword, UserRole userRole) {
        super(id, email, name, hashedPassword, userRole);
        this.schedule = new TreeMap<>();
    }
}
