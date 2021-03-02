package com.ooftf.spy.plugin

class SpyException extends RuntimeException {
    SpyException() {
    }

    SpyException(String message) {
        super(message)
    }

    SpyException(String message, Throwable cause) {
        super(message, cause)
    }

    SpyException(Throwable cause) {
        super(cause)
    }

    SpyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace)
    }
}