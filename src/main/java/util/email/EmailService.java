package util.email;

import domain.model.user.UserRole;
import util.LoggerUtil;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
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
        String welcomeAttendee = EmailContentService.getWelcomeMessage(userName, role);
        String welcomeOrganizer = EmailContentService.getWelcomeMessage(userName, role);
        String welcomeSpeaker = EmailContentService.getWelcomeMessage(userName, role);

        switch (role) {
            case ATTENDEE -> CompletableFuture.runAsync(() ->
                    sendEmail(toAddress, userName, welcomeSubject, welcomeAttendee));
            case ORGANIZER -> CompletableFuture.runAsync(() ->
                    sendEmail(toAddress, userName, welcomeSubject, welcomeOrganizer));
            case SPEAKER -> CompletableFuture.runAsync(() ->
                    sendEmail(toAddress, userName, welcomeSubject, welcomeSpeaker));
        }
    }


    public void sendEmail(String toAddress, String userName, String subject, String body) {
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

}



