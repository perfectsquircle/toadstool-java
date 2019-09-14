package toadstool;

public class ToadstoolException extends Exception {
    public ToadstoolException(String message) {
        super(message);
    }

    public ToadstoolException(String message, Throwable cause) {
        super(message, cause);
    }

    private static final long serialVersionUID = 1L;
}
