package domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import util.validation.ConferenceValidator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Conference {
    private final String id;
    private final String organizerId;
    private final String name;
    private final String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private final Set<String> sessions;
    private final Set<String> attendees;
    private final Set<String> speakers;
    private final Set<String> feedback;

    // no-arg constructor for JSON serialization/de-serialization
    private Conference() {
        this.id = null;
        this.organizerId = null;
        this.name = null;
        this.description = null;
        this.startDate = null;
        this.endDate = null;
        this.sessions = new HashSet<>();
        this.attendees = new HashSet<>();
        this.speakers = new HashSet<>();
        this.feedback = new HashSet<>();
    }
    private Conference(Builder builder) {
        this.id = builder.id;
        this.organizerId = builder.organizerId;
        this.name = builder.name;
        this.description = builder.description;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.sessions = builder.sessions;
        this.attendees = builder.attendees;
        this.speakers = builder.speakers;
        this.feedback = builder.feedback;
    }

    public static Builder builder(String id, String organizerId, String name, String description, LocalDate startDate, LocalDate endDate) {
        return new Builder(id, organizerId, name, description, startDate, endDate);
    }

    public static class Builder {

        // required parameters
        private final String id;
        private final String organizerId;
        private final String name;
        private final String description;
        private final LocalDate startDate;
        private final LocalDate endDate;

        // optional parameters
        private Set<String> sessions;
        private Set<String> attendees;
        private Set<String> speakers;
        private Set<String> feedback;

        private Builder(String id, String organizerId, String name, String description, LocalDate startDate, LocalDate endDate) {
            this.id = id;
            this.organizerId = organizerId;
            this.name = name;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;

            // assigning optional parameters with default values
            this.sessions = new HashSet<>();
            this.attendees = new HashSet<>();
            this.speakers = new HashSet<>();
            this.feedback = new HashSet<>();
        }

        public Builder setSessions(Set<String> sessions) {
            this.sessions = sessions != null ? sessions : new HashSet<>();
            return this;
        }

        public Builder setAttendees(Set<String> attendees) {
            this.attendees = attendees != null ? attendees : new HashSet<>();
            return this;
        }

        public Builder setSpeakers(Set<String> speakers) {
            this.speakers = speakers != null ? speakers : new HashSet<>();
            return this;
        }

        public Builder setFeedback(Set<String> feedback) {
            this.feedback = feedback != null ? feedback : new HashSet<>();
            return this;
        }

        public Conference build() {
            ConferenceValidator.validateConferenceParameters(this.id, this.organizerId, this.name, this.description, this.startDate, this.endDate, false);
            return new Conference(this);
        }
    }

    public void addSession(String sessionId) {
        this.sessions.add(sessionId);
    }

    public void removeSession(String sessionId) {
        this.sessions.remove(sessionId);
    }

    public void addAttendee(String attendeeId) {
        this.attendees.add(attendeeId);
    }

    public void removeAttendee(String attendeeId) {
        this.attendees.remove(attendeeId);
    }

    public void addSpeaker(String speakerId) {
        this.speakers.add(speakerId);
    }

    public void removeSpeaker(String speakerId) {
        this.speakers.remove(speakerId);
    }

    public void addFeedback(String feedbackId) {
        this.feedback.add(feedbackId);
    }

    public void removeFeedback(String feedbackId) {
        this.feedback.remove(feedbackId);
    }

    public String getId() {
        return this.id;
    }

    public String getOrganizerId() {
        return this.organizerId;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }


    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Set<String> getSessions() {
        return new HashSet<>(this.sessions);         // defensive copy
    }

    public Set<String> getAttendees() {
        return new HashSet<>(this.attendees);        // defensive copy
    }

    public Set<String> getSpeakers() {
        return new HashSet<>(this.speakers);         // defensive copy
    }

    public Set<String> getFeedback() {
        return new HashSet<>(this.feedback);
    }

    @Override
    public String toString() {
        return "Conference{" +
                "id='" + id + '\'' +
                ", organizerId='" + organizerId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", sessions=" + sessions +
                ", attendees=" + attendees +
                ", speakers=" + speakers +
                ", feedback=" + feedback +
                '}';
    }
}
