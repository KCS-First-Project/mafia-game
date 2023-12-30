package network.exception;

public class CustomException extends IllegalArgumentException {
    private static final String PREFIX = "[Exception] ";
    private static final String SUFFIX = " 다시 입력해주세요.";

    private CustomException(String errorMessage) {
        super(PREFIX + errorMessage + SUFFIX);
    }

    public static CustomException errorMessage(String errorMessage) {
        return new CustomException(errorMessage);
    }
}
