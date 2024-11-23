package util.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

public class EmailSessionFactory {

    // private no-arg constructor to suppress instantiability
    private EmailSessionFactory() {}

    public static Session createEmailSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", EmailConfig.SMTP_HOST.getValue());
        properties.put("mail.smtp.port", EmailConfig.SMTP_PORT.getValue());

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailConfig.SENDER_EMAIL.getValue(), EmailConfig.PASSWORD.getValue());
            }
        });
    }
}
