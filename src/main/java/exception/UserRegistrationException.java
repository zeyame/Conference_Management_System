package exception;

public class UserRegistrationException extends RuntimeException {

    private UserRegistrationException(String message) {
        super(message);
    }

    public static UserRegistrationException emailExists() {
        throw new UserRegistrationException("An account with this email already exists.");
    }

    public static UserRegistrationException savingData() {
        throw new UserRegistrationException("We are having trouble saving your data. Please try again later.");
    }

    public static UserRegistrationException unexpectedError() {
        throw new UserRegistrationException("An unexpected error occurred during registration. Please try again later.");
    }
}
