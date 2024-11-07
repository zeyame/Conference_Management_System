package exception;

public class UserRegistrationException extends RuntimeException {

    private UserRegistrationException(String message) {
        super(message);
    }

    public static UserRegistrationException savingData() {
        throw new UserRegistrationException("We are having trouble saving your data. Please try again later.");
    }

    public static UserRegistrationException unexpectedError() {
        throw new UserRegistrationException("An unexpected error occurred while processing your registration. Please try again later.");
    }
}
