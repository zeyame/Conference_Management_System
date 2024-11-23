package util;
import exception.UserLoginException;
import exception.UserRegistrationException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Arrays;

public class PasswordUtil {

    // private no-arg constructor to suppress instantiability
    private PasswordUtil() {}

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

    public static boolean verifyPassword(char[] plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            LoggerUtil.getInstance().logError("Either the plain password or the hashed password or both were null when passed to the verifyPassword method of the PasswordService class.");
            throw new IllegalArgumentException("Neither plain password nor hashed password can be null.");
        }

        String password = new String(plainPassword);
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            LoggerUtil.getInstance().logError("An unexpected error occurred during the password verification operation in the verifyPassword method of the PasswordService class. " + e.getMessage());
            throw UserLoginException.unexpectedError();
        } finally {
            // removing traces of the plain text password from memory
            Arrays.fill(plainPassword, '0');
            password = null;
        }
    }

    public static String checkPasswordComplexity(String password) {
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter.";
        }
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one number.";
        }
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return "Password must contain at least one special character (e.g., !, @, #, $, etc.).";
        }
        return null; // all checks passed, password is strong
    }
}
