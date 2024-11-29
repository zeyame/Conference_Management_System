package exception;

public class UserException extends RuntimeException {

    private UserException(String message) {
        super(message);
    }

    public static UserException notFound(String message) {
        return new UserException(message);
    }

    public static UserException invalidRole(String message) {
        return new UserException(message);
    }

    public static UserException savingFailure(String message) {
        return new UserException(message);
    }

    public static UserException registrationError(String message) {
        return new UserException(message);
    }

    public static UserException unexpectedError(String message) {
        return new UserException(message);
    }
}
