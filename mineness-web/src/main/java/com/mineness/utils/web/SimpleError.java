/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.utils.web;

/**
 * User: Bjorn Harvold
 * Date: Oct 13, 2010
 * Time: 3:19:42 PM
 * Responsibility:
 */
public class SimpleError {
    private String message;
    private String[] params;

    public SimpleError(String message, String... params) {
        this.message = message;
        this.params = params;
    }

    public String getMessage() {
        return message;
    }

    public String[] getParams() {
        return params;
    }
}
