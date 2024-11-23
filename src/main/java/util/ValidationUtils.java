package util;

import exception.InvalidInitializationException;

import java.time.LocalDate;
import java.time.LocalTime;

public class ValidationUtils {

    // suppress default constructor for non-instantiability
    private ValidationUtils() {}

    public static void requireNonEmpty(String value, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new InvalidInitializationException(fieldName + " is required and cannot be null or empty.");
        }
    }

    public static void validateDate(LocalDate date, String fieldName) {
        if (date == null) {
            throw new InvalidInitializationException(fieldName + " is required and cannot be null or empty.");
        }
    }

    public static void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidInitializationException("Start date and end date are required and cannot be null.");
        }
        if (startDate.equals(endDate) || startDate.isAfter(endDate)) {
            throw new InvalidInitializationException("Start date must be before the end date.");
        }
    }

    public static void validateTimes(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new InvalidInitializationException("Start time and end time are required and cannot be null.");
        }
        if (startTime.equals(endTime) || startTime.isAfter(endTime)) {
            throw new InvalidInitializationException("Start time must be before the end time.");
        }
    }
}
