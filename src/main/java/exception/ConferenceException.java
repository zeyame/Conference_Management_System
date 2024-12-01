package exception;

public class ConferenceException extends RuntimeException {
    private ConferenceException(String message) {
        super(message);
    }

    public static ConferenceException creatingFailure(String message) {return new ConferenceException(message);}

    public static ConferenceException updatingFailure(String message) {return new ConferenceException(message);}

    public static ConferenceException nameTaken(String message) {return new ConferenceException(message);}

    public static ConferenceException notFound(String message) {
        return new ConferenceException(message);
    }

    public static ConferenceException timeUnavailable(String message) {return new ConferenceException(message);}

    public static ConferenceException assignmentToOrganizer(String message) {return new ConferenceException(message);}

    public static ConferenceException registeringSession(String message) {return new ConferenceException(message);}


    public static ConferenceException validationFailure(String message) {return new ConferenceException(message);}

    public static ConferenceException savingFailure(String message) {
        return new ConferenceException(message);
    }

    public static ConferenceException deletingFailure(String message) {
        return new ConferenceException(message);
    }

}
