package exception;

public class SessionCreationException extends RuntimeException {
    private SessionCreationException(String message) {
        super(message);
    }

    public static SessionCreationException invalidConference(String message) {
        return new SessionCreationException(message);
    }

    public static SessionCreationException nameTaken(String message) {
        return new SessionCreationException(message);
    }

    public static SessionCreationException invalidSpeaker(String message) {
        return new SessionCreationException(message);
    }

    public static SessionCreationException speakerUnavailable(String message) {
        return new SessionCreationException(message);
    }

    public static SessionCreationException timeUnavailable(String message) {
        return new SessionCreationException(message);
    }

    public static SessionCreationException savingFailure(String message) {
        return new SessionCreationException(message);
    }
}
