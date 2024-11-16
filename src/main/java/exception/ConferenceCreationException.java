package exception;

public class ConferenceCreationException extends RuntimeException {

    private ConferenceCreationException(String message) {
        super(message);
    }

    public static ConferenceCreationException nameTaken() {
        throw new ConferenceCreationException("The provided conference name is already taken.");
    }

    public static ConferenceCreationException dateUnavailable() {
        throw new ConferenceCreationException("Another conference is already registered to take place within the time period provided.");
    }

}
