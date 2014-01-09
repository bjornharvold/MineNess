/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.bootstrap.impl;

import com.mineness.bootstrap.Bootstrapper;
import com.mineness.utils.jackson.CustomObjectMapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * User: bjorn
 * Date: Aug 21, 2008
 * Time: 3:05:40 PM
 */
public abstract class AbstractBootstrapper implements Bootstrapper {
    protected CustomObjectMapper mapper = new CustomObjectMapper();
    protected final DateFormat df = new SimpleDateFormat("yyyyMMdd");

    public abstract Boolean getEnabled();

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
