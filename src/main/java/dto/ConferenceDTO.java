package dto;

import exception.InvalidInitializationException;
import util.ValidationUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ConferenceDTO {
    private final String id;
    private final String organizerId;
    private final String name;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Set<String> sessions;
    private final Set<String> attendees;
    private final Set<String> speakers;

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
        }

        public Builder assignId(String id) {
            this.id = id;
            return this;
        }

        public Builder sessions(Set<String> sessions) {
            this.sessions = sessions != null ? sessions : new HashSet<>();
            return this;
        }

        public Builder attendees(Set<String> attendees) {
            this.attendees = attendees != null ? attendees : new HashSet<>();
            return this;
        }

        public Builder speakers(Set<String> speakers) {
            this.speakers = speakers != null ? speakers : new HashSet<>();
            return this;
        }

        public ConferenceDTO build() {
            validateParameters();
            return new ConferenceDTO(this);
        }

        private void validateParameters() {
            ValidationUtils.requireNonEmpty(this.organizerId, "Organizer ID");
            ValidationUtils.requireNonEmpty(this.name, "Name");
            ValidationUtils.requireNonEmpty(this.description, "Description");
            ValidationUtils.validateDates(this.startDate, this.endDate);
        }
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
}
