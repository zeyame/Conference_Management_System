package domain.model;

import exception.InvalidInitializationException;
import util.ValidationUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Conference {
    private final String id;
    private final String organizerId;
    private final String name;
    private final String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private final Set<String> sessions;
    private final Set<String> attendees;
    private final Set<String> speakers;

    // private no-args constructor for JSON serialization
    private Conference() {
        this.id = null;
        this.organizerId = null;
        this.name = null;
        this.description = null;
        this.startDate = null;
        this.endDate = null;
        this.sessions = Collections.emptySet();
        this.attendees = Collections.emptySet();
        this.speakers = Collections.emptySet();
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

        public Conference build() {
            validateParameters();
            return new Conference(this);
        }

        private void validateParameters() {
            ValidationUtils.requireNonEmpty(this.id,  "Conference ID");
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
        return new HashSet<>(sessions);         // defensive copy
    }

    public Set<String> getAttendees() {
        return new HashSet<>(attendees);        // defensive copy
    }

    public Set<String> getSpeakers() {
        return new HashSet<>(speakers);         // defensive copy
    }
}
