package util.validation;

import exception.InvalidInitializationException;
import java.time.LocalDate;

public class ConferenceValidator {

    // private no-arg constructor to suppress instantiability
    private ConferenceValidator() {}

    public static void validateConferenceParameters(String id, String organizerId, String name, String description, LocalDate startDate, LocalDate endDate, boolean isDTO) {
        if (!isDTO && !ValidationUtil.isNonEmpty(id)) {
            throw new InvalidInitializationException("Id is required for conference creation and cannot be null or empty.");
        }
        if (!ValidationUtil.isNonEmpty(organizerId)) {
            throw new InvalidInitializationException("Organizer Id is required for conference creation and cannot be null or empty.");
        }
        if (!ValidationUtil.isNonEmpty(name)) {
            throw new InvalidInitializationException("Name is required for conference creation and cannot be null or empty.");
        }
        if (!ValidationUtil.isNonEmpty(description)) {
            throw new InvalidInitializationException("Description is required for conference creation and cannot be null or empty.");
        }
        if (!ValidationUtil.areDatesValid(startDate, endDate)) {
            throw new InvalidInitializationException("Start and end dates are required for conference creation and cannot be null or empty. Start date must be before the end date and not in the past.");
        }
    }
}

