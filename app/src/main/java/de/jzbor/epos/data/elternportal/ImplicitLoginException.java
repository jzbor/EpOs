package de.jzbor.epos.data.elternportal;

import android.os.Build;
import android.support.annotation.RequiresApi;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected ImplicitLoginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
