package exception;

public class UserLoginException extends RuntimeException {
    private UserLoginException(String message) {
        super(message);
    }

    public static UserLoginException unexpectedError() {
        throw new UserLoginException("An unexpected error occurred during login. Please try again later.");
    }
}
