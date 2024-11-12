package domain.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Conference {
    String id;
    String organizerId;
    String name;
    String description;
    Date startDate;
    Date endDate;
    Set<String> sessions;
    Set<String> attendees;
    Set<String> speakers;

    public Conference() {}

    public Conference(String id, String organizerId, String name, String description, Date startDate, Date endDate) {
        this.id = id;
        this.organizerId = organizerId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sessions = new HashSet<>();
        this.attendees = new HashSet<>();
        this.speakers = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Set<String> getSessions() {
        return sessions;
    }

    public Set<String> getAttendees() {
        return attendees;
    }

    public Set<String> getSpeakers() {
        return speakers;
    }
}
