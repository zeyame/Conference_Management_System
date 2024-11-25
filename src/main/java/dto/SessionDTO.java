package dto;

import util.validation.SessionValidator;
import util.validation.ValidationUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class SessionDTO {

    private final String id;
    private final String conferenceId;
    private final String speakerId;
    private final String speakerName;
    private final String name;
    private String description;
    private final String room;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private final Set<String> registeredAttendees;
    private final Map<String, Boolean> attendanceRecord;

    // No-arg constructor for JSON serialization
    private SessionDTO() {
        this.id = null;
        this.conferenceId = null;
        this.speakerId = null;
        this.speakerName = null;
        this.name = null;
        this.description = null;
        this.room = null;
        this.date = null;
        this.startTime = null;
        this.endTime = null;
        this.registeredAttendees = new HashSet<>();
        this.attendanceRecord = new HashMap<>();
    }

    private SessionDTO(Builder builder) {
        this.id = builder.id;
        this.conferenceId = builder.conferenceId;
        this.speakerId = builder.speakerId;
        this.speakerName = builder.speakerName;
        this.name = builder.name;
        this.description = builder.description;
        this.room = builder.room;
        this.date = builder.date;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.registeredAttendees = builder.registeredAttendees;
        this.attendanceRecord = builder.attendanceRecord;
    }

    public static Builder builder(String conferenceId, String speakerId, String speakerName, String name, String room, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return new Builder(conferenceId, speakerId, speakerName, name, room, date, startTime, endTime);
    }

    public static class Builder {
        // required parameters
        private final String conferenceId;
        private final String speakerId;
        private final String speakerName;
        private final String name;
        private final String room;
        private final LocalDate date;
        private final LocalTime startTime;
        private final LocalTime endTime;

        // optional parameters
        private String id;
        private String description;
        private Set<String> registeredAttendees = new HashSet<>();
        private Map<String, Boolean> attendanceRecord = new HashMap<>();

        public Builder(String conferenceId, String speakerId, String speakerName, String name, String room, LocalDate date, LocalTime startTime, LocalTime endTime) {
            this.conferenceId = conferenceId;
            this.speakerId = speakerId;
            this.speakerName = speakerName;
            this.name = name;
            this.room = room;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;

            // assigning optional parameters with default values
            this.id = null;
            this.description = null;
            this.registeredAttendees = new HashSet<>();
            this.attendanceRecord = new HashMap<>();
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
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

        public SessionDTO build() {
            SessionValidator.validateSessionParameters(
                    this.id, this.conferenceId, this.speakerId,
                    this.speakerName, this.name,
                    this.date, this.startTime, this.endTime, true);
            return new SessionDTO(this);
        }
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

    public String getSpeakerName() {
        return this.speakerName;
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
        return new HashMap<>(this.attendanceRecord);           // defensive copy
    }
}
