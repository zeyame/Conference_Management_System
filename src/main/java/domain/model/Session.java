package domain.model;

import util.validation.SessionValidator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Session {

    private final String id;
    private final String conferenceId;
    private final String speakerId;
    private final String name;
    private String description;
    private final String room;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private final Set<String> registeredAttendees;
    private final Map<String, Boolean> attendanceRecord;

    // no-arg constructor for JSON serialization
    private Session() {
        this.id = null;
        this.conferenceId = null;
        this.speakerId = null;
        this.name = null;
        this.description = null;
        this.room = null;
        this.date = null;
        this.startTime = null;
        this.endTime = null;
        this.registeredAttendees = new HashSet<>();
        this.attendanceRecord = new HashMap<>();
    }

    private Session(Builder builder) {
        this.id = builder.id;
        this.conferenceId = builder.conferenceId;
        this.speakerId = builder.speakerId;
        this.name = builder.name;
        this.description = builder.description;
        this.room = builder.room;
        this.date = builder.date;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.registeredAttendees = builder.registeredAttendees;
        this.attendanceRecord = builder.attendanceRecord;
    }

    public static Builder builder(String id, String conferenceId, String speakerId, String name, String room, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return new Builder(id, conferenceId, speakerId, name, room, date, startTime, endTime);
    }


    public static class Builder {
        // required parameters
        private final String id;
        private final String conferenceId;
        private final String speakerId;
        private final String name;
        private final String room;
        private final LocalDate date;
        private final LocalTime startTime;
        private final LocalTime endTime;

        // optional parameters
        private String description;
        private Set<String> registeredAttendees;
        private Map<String, Boolean> attendanceRecord;

        private Builder(String id, String conferenceId, String speakerId, String name, String room, LocalDate date, LocalTime startTime, LocalTime endTime) {
            this.id = id;
            this.conferenceId = conferenceId;
            this.speakerId = speakerId;
            this.name = name;
            this.room = room;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;

            // assigning optional parameters with default values
            this.description = null;
            this.registeredAttendees = new HashSet<>();
            this.attendanceRecord = new HashMap<>();
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setRegisteredAttendees(Set<String> registeredAttendees) {
            this.registeredAttendees = registeredAttendees != null ? registeredAttendees : new HashSet<>();
            return this;
        }

        public Builder setAttendanceRecord(Map<String, Boolean> attendanceRecord) {
            this.attendanceRecord = attendanceRecord != null ? attendanceRecord : new HashMap<>();
            return this;
        }

        public Session build() {
            SessionValidator.validateSessionParameters(
                    this.id, this.conferenceId, this.speakerId,
                    null, this.name,
                    this.date, this.startTime, this.endTime, false);
            return new Session(this);
        }
    }

    public boolean overlapsWith(LocalDateTime otherStart, LocalDateTime otherEnd) {
        LocalDateTime sessionStart = LocalDateTime.of(this.date, this.startTime);
        LocalDateTime sessionEnd = LocalDateTime.of(this.date, this.endTime);

        return !sessionStart.isAfter(otherEnd) && !sessionEnd.isBefore(otherStart);
    }

    public String getId() {
        return this.id;
    }

    public String getConferenceId() {
        return this.conferenceId;
    }

    public String getSpeakerId() {
        return this.speakerId;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {return this.description;}

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoom() {
        return this.room;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Set<String> getRegisteredAttendees() {
        return new HashSet<>(this.registeredAttendees);         // defensive copy
    }

    public Map<String, Boolean> getAttendanceRecord() {
        return new HashMap<>(this.attendanceRecord);            // defensive copy
    }
}
