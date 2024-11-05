package util;
import exception.UserRegistrationException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Arrays;

public class PasswordService {
    public static String hashPassword(char[] plainPassword) {
        // checking input validity
        if (plainPassword == null || plainPassword.length == 0) {
            LoggerUtil.getInstance().logError("Password passed to the hashPassword method of the PasswordService class is either null or empty.");
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }

        // converting password to String for encryption
        String password = new String(plainPassword);
        String hashedPassword;

        try {
            hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        } catch (Exception e) {
            LoggerUtil.getInstance().logError("An unexpected error occurred during the hashing password operation in the hashPassword method of the PasswordService class. " + e.getMessage());
            throw UserRegistrationException.unexpectedError();
        } finally {
            // removing traces of the plain text password from memory
            Arrays.fill(plainPassword, '0');
            password = null;
        }

        return hashedPassword;
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            LoggerUtil.getInstance().logError("Either the plain password or the hashed password or both were null when passed to the verifyPassword method of the PasswordService class.");
            throw new IllegalArgumentException("Neither plain password nor hashed password can be null.");
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
