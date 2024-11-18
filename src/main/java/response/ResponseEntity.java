package response;

public class ResponseEntity<T> {
    private final T data;
    private final String errorMessage;

    // private constructor to enforce use of static factory methods
    private ResponseEntity(T data, String errorMessage) {
        this.data = data;
        this.errorMessage = errorMessage;
    }

    // factory method for successful responses
    public static ResponseEntity<Void> success() {
        return new ResponseEntity<>(null, null);
    }

    public static <T> ResponseEntity<T> success(T data) {
        return new ResponseEntity<>(data, null);
    }

    // factory method for error responses
    public static <T> ResponseEntity<T> error(String errorMessage) {
        return new ResponseEntity<>(null, errorMessage);
    }

    public T getData() {
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return errorMessage == null;
    }
}
