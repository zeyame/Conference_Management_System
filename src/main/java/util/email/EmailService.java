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
        String subject = "A change to one of your registered sessions";
        users.forEach(user ->
                CompletableFuture.runAsync(() ->
                        sendEmail(user.getEmail(), user.getName(), subject, getSessionChangeBody(sessionDTO, user))
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
        return "Dear " + attendeeName + ",\n\n"
                + "Welcome to the University of Hertfordshire's Conference Platform!\n\n"
                + "We are thrilled that you've chosen to explore the exciting and diverse scientific conferences we offer. "
                + "Our platform is designed to connect you with cutting-edge research, inspiring speakers, and thought-provoking sessions.\n\n"
                + "Feel free to browse through the list of upcoming conferences and register for those that capture your interest. "
                + "This is a wonderful opportunity to expand your knowledge, network with professionals, and be a part of the academic community.\n\n"
                + "Should you need any assistance, our team is here to help you every step of the way.\n\n"
                + "We look forward to your participation!\n\n"
                + "Best regards,\n\n"
                + "The University of Hertfordshire Team";
    }

    private String getWelcomeMessageForOrganizer(String organizerName) {
           return "Welcome to the Conference Management Platform, " + organizerName + "!\n\n"
                + "We are thrilled to have you as part of our team! As an organizer, you play a vital role in curating "
                + "exceptional events and bringing together participants for memorable experiences.\n\n"
                + "On this platform, you can manage conferences, create engaging sessions, and collaborate with speakers and attendees. "
                + "Your expertise and dedication will ensure the success of our events and strengthen our community.\n\n"
                + "Feel free to explore the upcoming conferences, manage sessions, and utilize all the tools available to "
                + "streamline your responsibilities. If you have any questions, don’t hesitate to reach out.\n\n"
                + "Thank you for your commitment, and we look forward to seeing the incredible work you’ll do!\n\n"
                + "Warm regards,\n\n"
                + "The University of Hertfordshire Team";
    }

    private String getWelcomeMessageForSpeaker(String speakerName) {
        return "Welcome to the Conference Management Platform, " + speakerName + "!\n\n"
                + "We are delighted to have you join us as a speaker. Your expertise and insights play a key role in "
                + "shaping enriching and inspiring sessions for our attendees.\n\n"
                + "This platform will help you manage your speaking engagements, view session details, and connect with "
                + "event organizers and participants. Feel free to explore the tools and features available to ensure your "
                + "sessions are seamless and impactful.\n\nThank you for being a part of our mission to foster knowledge "
                + "sharing and collaboration. We are excited to witness the value you bring to our events!\n\n"
                + "Warm regards,\n\n"
                + "The University of Hertfordshire Team";
    }

    private String getSessionChangeBody(SessionDTO sessionDTO, UserDTO user) {
        return switch (user.getRole()) {
            case ATTENDEE -> getAttendeeSessionChangeMessage(sessionDTO, user.getName());
            case SPEAKER -> getSpeakerSessionChangeMessage(sessionDTO, user.getName());
            case ORGANIZER -> throw new IllegalArgumentException("User must either have attendee or speaker permissions.");
        };
    }

    private String getAttendeeSessionChangeMessage(SessionDTO sessionDTO, String attendeeName) {
        return String.format("Hello %s,\n\nWe kindly inform you that an update has taken place to one of the sessions you are registered for. " +
                        "These are the updated session details:\n\n" +
                        "Name: %s\nSpeaker: %s\nDescription: %s\nRoom: %s\nDate: %s\nStart Time: %s\nEnd Time: %s\n\n" +
                        "We are sorry if this has caused any inconvenience and we hope that you can still make it to the session.\n\n" +
                        "Kind Regards,\n\nThe University of Hertfordshire Team",
                attendeeName, sessionDTO.getName(), sessionDTO.getSpeakerName(),
                sessionDTO.getDescription(), sessionDTO.getRoom(), sessionDTO.getDate(),
                sessionDTO.getStartTime(), sessionDTO.getEndTime());
    }

    private String getSpeakerSessionChangeMessage(SessionDTO sessionDTO, String speakerName) {
        return String.format("Hello %s,\n\nWe kindly inform you that an update has taken place to one of the sessions you are " +
                        "assigned to speak at. These are the updated session details:\n\n" +
                        "Name: %s\nSpeaker: %s\nDescription: %s\nRoom: %s\nDate: %s\nStart Time: %s\nEnd Time: %s\n\n" +
                        "We want to let you know that this has happened due to unforeseen circumstances and we sincerely apologize " +
                        "if this has caused any inconvenience. We hope that you can still speak at the session, but if not, " +
                        "please contact the UH Conference Management team so we can make the necessary adjustments.\n\n" +
                        "Kind Regards,\n\nThe University of Hertfordshire Team",
                speakerName, sessionDTO.getName(), sessionDTO.getSpeakerName(),
                sessionDTO.getDescription(), sessionDTO.getRoom(), sessionDTO.getDate(),
                sessionDTO.getStartTime(), sessionDTO.getEndTime());
    }

}



