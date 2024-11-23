package util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

public class EmailService {
    private final Session session;
    private static EmailService instance;

    private EmailService() {
        this.session = EmailSessionFactory.createEmailSession();
    }

    public static EmailService getInstance() {
        if (instance == null) instance = new EmailService();
        return instance;
    }

    public void sendWelcomeEmail(String toAddress, String userName) {
        CompletableFuture.runAsync(() -> sendWelcomeEmailSync(toAddress, userName));
    }

    private void sendWelcomeEmailSync(String toAddress, String userName) {
        final String WELCOME_SUBJECT = "Welcome to UH Scientific Conferences";
        final String WELCOME_MESSAGE_TEMPLATE = "Hello %s,\n\n"
                + "Welcome to our platform! We are excited that you took interest in the scientific conferences "
                + "that the University of Hertfordshire has to offer.\n\n"
                + "Please feel free to browse through the upcoming conferences and register for any that interest you.\n\n"
                + "Kind regards,\n\n" + "The UH Team";

        final int MAX_ATTEMPTS = 3;
        int attempt = 0;
        while (attempt < MAX_ATTEMPTS) {
            try {
                Message message = new MimeMessage(session);

                message.setFrom(new InternetAddress(EmailConfig.SENDER_EMAIL.getValue(), EmailConfig.DISPLAY_NAME.getValue()));

                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
                message.setSubject(WELCOME_SUBJECT);

                String content = String.format(WELCOME_MESSAGE_TEMPLATE, userName);
                message.setText(content);

                Transport.send(message);

                LoggerUtil.getInstance().logInfo("A welcome email to '" + toAddress + "' has successfully been sent.");
                return;
            } catch (MessagingException | UnsupportedEncodingException e) {
                attempt++;
                if (attempt >= MAX_ATTEMPTS) {
                    LoggerUtil.getInstance().logError("Failed to send email to " + toAddress + " after " + attempt + " attempts: " + e.getMessage());
                }
            }
        }
    }
}
