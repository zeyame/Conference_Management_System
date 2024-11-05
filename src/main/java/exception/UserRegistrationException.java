package exception;

public class UserRegistrationException extends RuntimeException {

    private UserRegistrationException(String message) {
        super(message);
    }

    public static UserRegistrationException invalidRole() {
        throw new UserRegistrationException("Invalid role selected.");
    }

    public static UserRegistrationException emailExists() {
        throw new UserRegistrationException("An account with this email already exists.");
    }

    public static UserRegistrationException unexpectedError() {
        throw new UserRegistrationException("An unexpected error occurred during registration.");
    }
}
