package exception;

public class ConferenceException extends RuntimeException {
    private ConferenceException(String message) {
        super(message);
    }

    public static ConferenceException notFound(String message) {
        return new ConferenceException(message);
    }

    public static ConferenceException savingFailure(String message) {
        return new ConferenceException(message);
    }
}
