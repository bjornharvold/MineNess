package com.mineness.service.impl;

/**
 * Created by Bjorn Harvold
 * Date: 4/7/13
 * Time: 8:30 PM
 * Responsibility:
 */
public class EmailParserException extends Exception {
    public EmailParserException() {
    }

    public EmailParserException(String message) {
        super(message);
    }

    public EmailParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailParserException(Throwable cause) {
        super(cause);
    }

    public EmailParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
