package util.validation;


import domain.model.UserRole;
import dto.UserDTO;
import exception.FormValidationException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

public class FormValidator {

    // Suppress default constructor for non-instantiability
    private FormValidator() {}

    public static void validateSessionForm(String name, String description, UserDTO speaker, String room, LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (!ValidationUtil.isNonEmpty(name)) {
            throw new FormValidationException("Session Name must not be empty.");
        }
        if (!ValidationUtil.isNonEmpty(description)) {
            throw new FormValidationException("Session Description must not be empty.");
        }
        if (speaker == null) {
            throw new FormValidationException("Session Speaker is required.");
        }
        if (!ValidationUtil.isNonEmpty(room)) {
            throw new FormValidationException("Session Room must not be empty.");
        }
        if (!ValidationUtil.isDateValid(date)) {
            throw new FormValidationException("Session Date cannot be in the past.");
        }
        if (!ValidationUtil.areTimesValid(startTime, endTime)) {
            throw new FormValidationException("Start Time must be before End Time.");
        }
//        if (Duration.between(startTime, endTime).toMinutes() < 60) {
//            throw new FormValidationException("A session must be at least an hour long.");
//        }
    }

    public static void validateConferenceForm(String name, String description, LocalDate startDate, LocalDate endDate) {
        if (!ValidationUtil.isNonEmpty(name)) {
            throw new FormValidationException("Conference Name must not be empty.");
        }
        if (!ValidationUtil.isNonEmpty(description)) {
            throw new FormValidationException("Conference Description must not be empty.");
        }
        if (!ValidationUtil.isDateValid(startDate) || !ValidationUtil.areDatesValid(startDate, endDate)) {
            throw new FormValidationException("Start Date must be before End Date and not in the past.");
        }
    }

    public static void validateLoginForm(String email, char[] password) {
        if (!ValidationUtil.isNonEmpty(email)) {
            throw new FormValidationException("Email must not be empty.");
        }
        if (password == null || password.length == 0) {
            throw new FormValidationException("Password must be filled out.");
        }
    }

    public static void validateRegistrationForm(String email, String name, String speakerBio, String employeeId, char[] password, char[] confirmPassword, UserRole userRole) {
        if (userRole == null) {
            throw new FormValidationException("A role must be selected.");
        }

        if (!ValidationUtil.isNonEmpty(email)) {
            throw new FormValidationException("Email must not be empty.");
        }

        if (!ValidationUtil.isNonEmpty(name)) {
            throw new FormValidationException("Name must not be empty.");
        }

        if (userRole == UserRole.SPEAKER && !ValidationUtil.isNonEmpty(speakerBio)) {
            throw new FormValidationException("Bio must not be empty.");
        }

        if (userRole == UserRole.ORGANIZER && !ValidationUtil.isNonEmpty(employeeId)) {
            throw new FormValidationException("Employee ID must not be empty.");
        }

        if (password == null || password.length == 0) {
            throw new FormValidationException("Password must be filled out.");
        }

        if (!Arrays.equals(password, confirmPassword)) {
            throw new FormValidationException("Passwords must match.");
        }

        validateEmailFormat(email);
    }

    private static void validateEmailFormat(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException e) {
            throw new FormValidationException("Invalid email format.");
        }
    }
}

