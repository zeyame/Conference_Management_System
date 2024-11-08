package exception;

public class InvalidUserRoleException extends RuntimeException {
    public InvalidUserRoleException(String messaage) {
        super(messaage);
    }
}
