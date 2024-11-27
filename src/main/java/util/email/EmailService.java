package util.email;

import domain.model.UserRole;
import dto.SessionDTO;
import dto.UserDTO;
import util.LoggerUtil;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EmailService {
    private final Session session;
    private static EmailService instance;

    private final String welcomeSubject = "Welcome to UH Scientific Conferences";


    private EmailService() {
        this.session = EmailSessionFactory.createEmailSession();
    }

    public static EmailService getInstance() {
        if (instance == null) instance = new EmailService();
        return instance;
    }

    public void sendWelcomeEmail(String toAddress, String userName, UserRole role) {
        switch (role) {
            case ATTENDEE -> CompletableFuture.runAsync(() ->
                    sendEmail(toAddress, userName, welcomeSubject, getWelcomeMessageForAttendee(userName)));
            case ORGANIZER -> CompletableFuture.runAsync(() ->
                    sendEmail(toAddress, userName, welcomeSubject, getWelcomeMessageForOrganizer(userName)));
            case SPEAKER -> CompletableFuture.runAsync(() ->
                    sendEmail(toAddress, userName, welcomeSubject, getWelcomeMessageForSpeaker(userName)));
        }
    }

    public void notifyAttendeesAndSpeakerOfSessionChange(SessionDTO sessionDTO, List<UserDTO> users) {
        System.out.println("Number of users (attendees+speakers) received by email service: " + users.size());
        String subject = "A change to one of your registered sessions";
        users.forEach(user ->
                CompletableFuture.runAsync(() ->
                        sendEmail(user.getEmail(), user.getName(), subject, getSessionChangeBody(sessionDTO, user.getName()))
                )
        );

        LoggerUtil.getInstance().logInfo("Attendees and speaker successfully notified of session changes.");
    }

    private void sendEmail(String toAddress, String userName, String subject, String body) {
        final int MAX_ATTEMPTS = 3;
        int attempt = 0;
        while (attempt < MAX_ATTEMPTS) {
            try {
                Message message = new MimeMessage(session);

                message.setFrom(new InternetAddress(EmailConfig.SENDER_EMAIL.getValue(), EmailConfig.DISPLAY_NAME.getValue()));

                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
                message.setSubject(subject);

                String content = String.format(body, userName);
                message.setText(content);

                Transport.send(message);

                LoggerUtil.getInstance().logInfo(String.format("An email has successfully been sent to '%s'.", toAddress));
                return;
            } catch (MessagingException | UnsupportedEncodingException e) {
                attempt++;
                if (attempt >= MAX_ATTEMPTS) {
                    LoggerUtil.getInstance().logError("Failed to send email to " + toAddress + " after " + attempt + " attempts: " + e.getMessage());
                } else {
                    try {
                        long backOffTime = (long) Math.pow(2, attempt) * 1000;          // exponential backoff
                        Thread.sleep(backOffTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();;
                    }
                }
            }
        }
    }

    private String getWelcomeMessageForAttendee(String attendeeName) {
         return "Hello " + attendeeName + ",\n\n"
                + "Welcome to our platform! We are excited that you took interest in the scientific conferences "
                + "that the University of Hertfordshire has to offer.\n\n"
                + "Please feel free to browse through the upcoming conferences and register for any that interest you.\n\n"
                + "Kind regards,\n\n" + "The UH Team";
    }

    private String getWelcomeMessageForOrganizer(String organizerName) {
        return "";
    }

    private String getWelcomeMessageForSpeaker(String speakerName) {
        return "";
    }

    private String getSessionChangeBody(SessionDTO sessionDTO, String attendeeName) {
        return String.format("Hello %s,\n\nWe kindly inform you that an update has taken place to one of the sessions you are registered for. " +
                        "These are the updated session details:\n\n" +
                        "Name: %s\nSpeaker: %s\nDescription: %s\nRoom: %s\nDate: %s\nStart Time: %s\nEnd Time: %s\n\n" +
                        "We are sorry if this has caused any inconvenience and we hope that you can still make it to the session.\n\n" +
                        "Kind Regards,\n\nThe UH Team", attendeeName, sessionDTO.getName(), sessionDTO.getSpeakerName(), sessionDTO.getDescription(),
                                                        sessionDTO.getRoom(), sessionDTO.getDate(), sessionDTO.getStartTime(), sessionDTO.getEndTime());
    }


}
