package dto;

import java.util.Date;

public class ConferenceFormDTO {

    private final String organizerId;
    private final String name;
    private final String description;
    private final Date startDate;
    private final Date endDate;

    public ConferenceFormDTO(String organizerId, String name, String description, Date startDate, Date endDate) {
        this.organizerId = organizerId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}
