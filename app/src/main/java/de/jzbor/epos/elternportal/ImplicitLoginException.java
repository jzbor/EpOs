package de.jzbor.epos.elternportal;

public class ImplicitLoginException extends Exception {
    public ImplicitLoginException() {
        super();
    }

    public ImplicitLoginException(String message) {
        super(message);
    }

    public ImplicitLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImplicitLoginException(Throwable cause) {
        super(cause);
    }

    protected ImplicitLoginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
