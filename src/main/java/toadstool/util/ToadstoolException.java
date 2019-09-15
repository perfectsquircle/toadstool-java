package toadstool.util;

public class ToadstoolException extends RuntimeException {
    public ToadstoolException(String message) {
        super(message);
    }

    public ToadstoolException(String message, Throwable cause) {
        super(message, cause);
    }

    private static final long serialVersionUID = 1L;
}
