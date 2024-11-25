package util.validation;

import exception.InvalidInitializationException;

import java.time.LocalDate;
import java.time.LocalTime;

public class SessionValidator {

    // private no-arg constructor to suppress instantiability
    private SessionValidator() {}

    public static void validateSessionParameters(String id, String conferenceId, String speakerId, String speakerName, String name, LocalDate date, LocalTime startTime, LocalTime endTime, boolean isDTO) {
        if (!isDTO && !ValidationUtil.isNonEmpty(id))
            throw new InvalidInitializationException("Id is required for session creation and cannot be null or empty.");

        if (!ValidationUtil.isNonEmpty(conferenceId))
            throw new InvalidInitializationException("Conference id is required for session creation and cannot be null or empty.");

        if (!ValidationUtil.isNonEmpty(speakerId))
            throw new InvalidInitializationException("Speaker id is required for session creation and cannot be null or empty.");

        if (isDTO && !ValidationUtil.isNonEmpty(speakerName))
            throw new InvalidInitializationException("Speaker name is required for session DTO creation and cannot be null or empty.");

        if (!ValidationUtil.isNonEmpty(name))
            throw new InvalidInitializationException("Name is required for session creation and cannot be null or empty.");

        if (!ValidationUtil.isDateValid(date))
            throw new InvalidInitializationException("Date is required for session creation and cannot be null.");

        if (!ValidationUtil.areTimesValid(startTime, endTime))
            throw new InvalidInitializationException("Start and end times are required for session creation and cannot be null or empty. Start time must be before the end time.");

    }
}
