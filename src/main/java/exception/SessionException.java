package exception;

public class SessionException extends RuntimeException {
    private SessionException(String message) {
        super(message);
    }

    public static SessionException invalidData(String message) {return new SessionException(message);}
    public static SessionException notFound(String message) {
        return new SessionException(message);
    }

    public static SessionException invalidConference(String message) {
        return new SessionException(message);
    }

    public static SessionException nameTaken(String message) {
        return new SessionException(message);
    }

    public static SessionException unavailableSpeaker(String message) {return new SessionException(message);}
    public static SessionException assignmentToSpeaker(String message) {return new SessionException(message);}
    public static SessionException timeUnavailable(String message) {
        return new SessionException(message);
    }

    public static SessionException createOrUpdateFailure(String message) {return new SessionException(message);}
    public static SessionException validationFailure(String message) {return new SessionException(message);}
    public static SessionException savingFailure(String message) {
        return new SessionException(message);
    }

    public static SessionException deletingFailure(String message) {
        return new SessionException(message);}

    public static SessionException notificationFailure(String message) {
        return new SessionException(message);
    }

    public static SessionException rollbackFailure(String message) {
        return new SessionException(message);
    }
}
