package exception;

public class PasswordException extends RuntimeException {

    private PasswordException(String message) {
        super(message);
    }

    public static PasswordException hashingError(String message) {
        return new PasswordException(message);
    }

    public static PasswordException verificationError(String message) {
        return new PasswordException(message);
    }
}
