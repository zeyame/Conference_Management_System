package service.conference;


import dto.ConferenceDTO;
import dto.UserDTO;
import exception.SessionException;
import util.LoggerUtil;
import util.email.EmailContentService;
import util.email.EmailService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConferenceNotificationService {

    private static final EmailService emailService = EmailService.getInstance();

    // no arg constructor to suppress instantiability
    private ConferenceNotificationService() {}

    public static void notifyConferenceCreation(ConferenceDTO conferenceDTO, List<UserDTO> attendees, List<UserDTO> speakers) {
        if (conferenceDTO == null || attendees == null || speakers == null) {
            throw new IllegalArgumentException("ConferenceDTO, attendees, and speakers cannot be null.");
        }

        final String subject = "New Conference";
        try {

            // notify attendees
            attendees.forEach(attendee ->
                    CompletableFuture.runAsync(() ->
                            emailService.sendEmail(attendee.getEmail(), attendee.getName(), subject,
                                    EmailContentService.getConferenceCreationBody(conferenceDTO, attendee))
                    )
            );

            // notify speakers
            speakers.forEach(speaker ->
                    CompletableFuture.runAsync(() ->
                            emailService.sendEmail(speaker.getEmail(), speaker.getName(), subject,
                                    EmailContentService.getConferenceCreationBody(conferenceDTO, speaker))
                    )
            );

        } catch (Exception e) {
            LoggerUtil.getInstance().logError("Notification failure when notifying of conference creation: " + e.getMessage());
        }
    }

    public static void notifyConferenceChange(ConferenceDTO conferenceDTO, List<UserDTO> attendees, List<UserDTO> speakers) {
        String subject = "Conference Change";
        try {
            // notify attendees
            attendees.forEach(attendee ->
                    CompletableFuture.runAsync(() ->
                            emailService.sendEmail(attendee.getEmail(), attendee.getName(), subject,
                                    EmailContentService.getConferenceChangeBody(conferenceDTO, attendee))
                    )
            );

            // notify speakers
            speakers.forEach(speaker ->
                    CompletableFuture.runAsync(() ->
                            emailService.sendEmail(speaker.getEmail(), speaker.getName(), subject,
                                    EmailContentService.getConferenceChangeBody(conferenceDTO, speaker))
                    )
            );

        } catch (Exception e) {
            LoggerUtil.getInstance().logError("Notification failure when notifying of conference change: " + e.getMessage());
        }
    }

    public static void notifyConferenceDeletion(ConferenceDTO conferenceDTO, List<UserDTO> attendees, List<UserDTO> speakers) {
        String subject = "Conference Cancelled";
        try {
            // notify attendees
            attendees.forEach(attendee ->
                    CompletableFuture.runAsync(() ->
                            emailService.sendEmail(attendee.getEmail(), attendee.getName(), subject,
                                    EmailContentService.getConferenceDeletionBody(conferenceDTO, attendee))
                    )
            );

            // notify speakers
            speakers.forEach(speaker ->
                    CompletableFuture.runAsync(() ->
                            emailService.sendEmail(speaker.getEmail(), speaker.getName(), subject,
                                    EmailContentService.getConferenceCreationBody(conferenceDTO, speaker))
                    )
            );
        } catch (Exception e) {
            LoggerUtil.getInstance().logError("Notification failure when notifying of conference deletion: " + e.getMessage());
        }
    }

    public static void notifyAttendeeRegistrationToConference(ConferenceDTO conferenceDTO, UserDTO attendee) {
        if (conferenceDTO == null || attendee == null) {
            throw new IllegalArgumentException("Invalid conference and/or attendee data.");
        }

        String subject = "Registered to Conference";
        try {
            CompletableFuture.runAsync(() ->
                    emailService.sendEmail(
                            attendee.getEmail(),
                            attendee.getName(),
                            subject,
                            EmailContentService.getAttendeeRegisteredToConferenceBody(conferenceDTO, attendee))
            );
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to notify attendee '%s' of his decision to leave conference '%s'.", attendee.getName(), conferenceDTO.getName()));
        }
    }
    public static void notifyAttendeeUnregisteredFromConference(ConferenceDTO conferenceDTO, UserDTO attendee) {
        if (conferenceDTO == null || attendee == null) {
            throw new IllegalArgumentException("Invalid conference and/or attendee data.");
        }

        String subject = "Unregistered from Conference";
        try {
            CompletableFuture.runAsync(() ->
                    emailService.sendEmail(
                            attendee.getEmail(),
                            attendee.getName(),
                            subject,
                            EmailContentService.getAttendeeUnregisteredFromConferenceBody(conferenceDTO, attendee))
            );
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to notify attendee '%s' of his decision to leave conference '%s'.", attendee.getName(), conferenceDTO.getName()));
        }
    }


}
