package exception;

public class FormValidationException extends RuntimeException {
    public FormValidationException(String message) {
        super(message);
    }
}
