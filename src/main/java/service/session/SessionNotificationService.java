package service.session;

import dto.SessionDTO;
import dto.UserDTO;
import exception.SessionException;
import service.UserService;
import util.LoggerUtil;
import util.email.EmailContentService;
import util.email.EmailService;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SessionNotificationService {

    private static final EmailService emailService = EmailService.getInstance();

    // no arg constructor to suppress instantiability
    private SessionNotificationService() {}
    public static void notifySessionCreation(SessionDTO sessionDTO, List<UserDTO> attendees, UserDTO speaker) {
        final String subject = "New Session";
        try {

            // notify attendees
            attendees.forEach(attendee ->
                    CompletableFuture.runAsync(() ->
                            emailService.sendEmail(attendee.getEmail(), attendee.getName(), subject,
                                           EmailContentService.getSessionCreationBody(sessionDTO, attendee))
                    )
            );

            // notify speaker
            CompletableFuture.runAsync(() ->
                    emailService.sendEmail(speaker.getEmail(), speaker.getName(), subject,
                                           EmailContentService.getSessionCreationBody(sessionDTO, speaker))
            );
        } catch (Exception e) {
            LoggerUtil.getInstance().logError("Notification failure: " + e.getMessage());
        }
    }

    public static void notifySessionChange(SessionDTO sessionDTO, List<UserDTO> attendees, UserDTO speaker) {
        String subject = "Session Change";
        try {
            // notify attendees
            attendees.forEach(attendee ->
                    CompletableFuture.runAsync(() ->
                            emailService.sendEmail(attendee.getEmail(), attendee.getName(), subject,
                                    EmailContentService.getSessionChangeBody(sessionDTO, attendee))
                    )
            );

            // notify speaker
            CompletableFuture.runAsync(() ->
                    emailService.sendEmail(speaker.getEmail(), speaker.getName(), subject,
                            EmailContentService.getSessionChangeBody(sessionDTO, speaker))
            );
        } catch (Exception e) {
            LoggerUtil.getInstance().logError("Notification failure: " + e.getMessage());
            throw new SessionException("An unexpected error occurred when notifying users of session changes.");
        }
    }

    public static void notifySessionDeletion(SessionDTO sessionDTO, List<UserDTO> attendees, UserDTO speaker) {
        String subject = "Session Cancelled";
        try {
            // notify attendees
            attendees.forEach(attendee ->
                    CompletableFuture.runAsync(() ->
                            emailService.sendEmail(attendee.getEmail(), attendee.getName(), subject,
                                    EmailContentService.getSessionDeletionBody(sessionDTO, attendee))
                    )
            );

            // notify speaker
            CompletableFuture.runAsync(() ->
                    emailService.sendEmail(speaker.getEmail(), speaker.getName(), subject,
                            EmailContentService.getSessionDeletionBody(sessionDTO, speaker))
            );
        } catch (Exception e) {
            LoggerUtil.getInstance().logError("Notification failure: " + e.getMessage());
            throw new SessionException("An unexpected error occurred when notifying users of session cancellation.");
        }
    }


}
