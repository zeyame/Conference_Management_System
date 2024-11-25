package util.validation;

import java.time.LocalDate;
import java.time.LocalTime;

public class ValidationUtil {

    // Suppress default constructor for non-instantiability
    private ValidationUtil() {}

    // Simple boolean utility methods
    public static boolean isNonEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    public static boolean isDateValid(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }

    public static boolean areDatesValid(LocalDate startDate, LocalDate endDate) {
        return startDate != null && endDate != null && startDate.isBefore(endDate);
    }

    public static boolean areTimesValid(LocalTime startTime, LocalTime endTime) {
        return startTime != null && endTime != null && startTime.isBefore(endTime);
    }
}

