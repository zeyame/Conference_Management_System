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

    private final UserService userService;
    private final EmailService emailService;
    public SessionNotificationService(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    public void notifySessionCreation(SessionDTO sessionDTO, Set<String> attendeeIds, String speakerId) {
        final String subject = "New Session";
        try {
            List<UserDTO> attendees = userService.findAllById(attendeeIds);
            UserDTO speaker = userService.getBydId(speakerId);

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

    public void notifySessionChange(SessionDTO sessionDTO, Set<String> attendeeIds, String speakerId) {
        String subject = "Session Change";
        try {
            List<UserDTO> attendees = userService.findAllById(attendeeIds);
            UserDTO speaker = userService.getBydId(speakerId);

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
            throw SessionException.notificationFailure("An unexpected error occurred when notifying users of session changes.");
        }
    }

    public void notifySessionDeletion(SessionDTO sessionDTO, Set<String> attendeeIds, String speakerId) {
        String subject = "Session Cancelled";
        try {
            List<UserDTO> attendees = userService.findAllById(attendeeIds);
            UserDTO speaker = userService.getBydId(speakerId);

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
            throw SessionException.notificationFailure("An unexpected error occurred when notifying users of session cancellation.");
        }
    }


}
