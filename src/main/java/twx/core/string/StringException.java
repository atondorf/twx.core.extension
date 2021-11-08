package twx.core.string;

public class StringException extends RuntimeException {

    public StringException() {
    }

    public StringException(String message) {
        super(message);
    }

    public StringException(String message, Throwable cause) {
        super(message, cause);
    }

    public StringException(Throwable cause) {
        super(cause);
    }

    public StringException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
