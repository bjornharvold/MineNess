package com.mineness.service.impl;

/**
 * Created by Bjorn Harvold
 * Date: 4/7/13
 * Time: 8:30 PM
 * Responsibility:
 */
public class CacheException extends Exception {
    public CacheException() {
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }

    public CacheException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
