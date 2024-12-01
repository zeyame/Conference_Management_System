package dto;

import util.validation.ConferenceValidator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class ConferenceDTO {
    private String id;
    private final String organizerId;
    private final String name;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Set<String> sessions;
    private final Set<String> attendees;
    private final Set<String> speakers;
    private final Set<String> feedback;

    private ConferenceDTO(Builder builder) {
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

    public static Builder builder(String organizerId, String name, String description, LocalDate startDate, LocalDate endDate) {
        return new Builder(organizerId, name, description, startDate, endDate);
    }

    public static class Builder {

        // required parameters
        private final String organizerId;
        private final String name;
        private final String description;
        private final LocalDate startDate;
        private final LocalDate endDate;

        // optional parameters
        private String id;
        private Set<String> sessions;
        private Set<String> attendees;
        private Set<String> speakers;
        private Set<String> feedback;

        private Builder(String organizerId, String name, String description, LocalDate startDate, LocalDate endDate) {
            this.organizerId = organizerId;
            this.name = name;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;

            // initializing optional parameters with default values
            this.id = null;
            this.sessions = new HashSet<>();
            this.attendees = new HashSet<>();
            this.speakers = new HashSet<>();
            this.feedback = new HashSet<>();
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
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

        public ConferenceDTO build() {
            ConferenceValidator.validateConferenceParameters(this.id, this.organizerId, this.name, this.description, this.startDate, this.endDate, true);
            return new ConferenceDTO(this);
        }
    }


    public String getId() {
        return this.id;
    }
    public void setId(String id) {this.id = id;}
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
    public LocalDate getEndDate() {
        return this.endDate;
    }
    public Set<String> getSessions() {
        return new HashSet<>(this.sessions);         // defensive copy
    }
    public Set<String> getAttendees() {
        return new HashSet<>(this.attendees);       // defensive copy
    }
    public Set<String> getSpeakers() {
        return new HashSet<>(this.speakers);        // defensive copy
    }

    public Set<String> getFeedback() {return new HashSet<>(this.feedback);}
}
