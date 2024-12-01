package dto;

import domain.model.Session;
import util.validation.SessionValidator;
import util.validation.ValidationUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class SessionDTO {

    private String id;
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
    private final Set<String> presentAttendees;
    private final Set<String> feedback;
    private final float attendanceRecord;

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
        this.presentAttendees = new HashSet<>();
        this.feedback = new HashSet<>();
        this.attendanceRecord = 0;
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
        this.presentAttendees = builder.presentAttendees;
        this.feedback = builder.feedback;
        this.attendanceRecord = getAttendanceRecord();
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
        private Set<String> registeredAttendees;
        private Set<String> presentAttendees;
        private Set<String> feedback;

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
            this.presentAttendees = new HashSet<>();
            this.feedback = new HashSet<>();
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

        public Builder setPresentAttendees(Set<String> presentAttendees) {
            this.presentAttendees = presentAttendees != null ? presentAttendees : new HashSet<>();
            return this;
        }

        public Builder setFeedback(Set<String> feedback) {
            this.feedback = feedback != null ? feedback : new HashSet<>();
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

    public boolean overlapsWith(LocalDateTime otherStart, LocalDateTime otherEnd) {
        LocalDateTime sessionStart = LocalDateTime.of(this.date, this.startTime);
        LocalDateTime sessionEnd = LocalDateTime.of(this.date, this.endTime);

        return !sessionStart.isAfter(otherEnd) && !sessionEnd.isBefore(otherStart);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {this.id = id;}

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

    public Set<String> getPresentAttendees() {
        return new HashSet<>(this.presentAttendees);            // defensive copy
    }

    public Set<String> getFeedback() {
        return new HashSet<>(this.feedback);            // defensive copy
    }

    public float getAttendanceRecord() {
        if (this.registeredAttendees.isEmpty() || this.presentAttendees.isEmpty()) {
            return 0;
        }
        return (float) this.presentAttendees.size() / this.registeredAttendees.size();
    }
}
