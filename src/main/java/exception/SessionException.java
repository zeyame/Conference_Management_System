package exception;

public class SessionException extends RuntimeException {
    private SessionException(String message) {
        super(message);
    }

    public static SessionException invalidConference(String message) {
        return new SessionException(message);
    }

    public static SessionException nameTaken(String message) {
        return new SessionException(message);
    }

    public static SessionException invalidSpeaker(String message) {
        return new SessionException(message);
    }

    public static SessionException speakerUnavailable(String message) {
        return new SessionException(message);
    }

    public static SessionException timeUnavailable(String message) {
        return new SessionException(message);
    }

    public static SessionException savingFailure(String message) {
        return new SessionException(message);
    }

    public static SessionException notificationFailure(String message) {
        return new SessionException(message);
    }
}
