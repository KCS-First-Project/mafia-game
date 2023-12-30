package network.exception;

public class NoResourceException extends RuntimeException {
    private static final String PREFIX = "[NoResourceException] ";

    private NoResourceException(String errorMessage) {
        super(PREFIX + errorMessage);
    }

    public static NoResourceException errorMessage(String errorMessage) {
        return new NoResourceException(errorMessage);
    }
}
