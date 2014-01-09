/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.utils.converter;

/**
 * Created by Bjorn Harvold
 * Date: 1/24/12
 * Time: 11:38 PM
 * Responsibility:
 */

import org.springframework.core.convert.converter.Converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateToStringConverter implements Converter<Date, String> {
    private DateFormat df;

    public DateToStringConverter(String dateFormat) {
        this.df = new SimpleDateFormat(dateFormat);
    }

    @Override
    public String convert(Date date) {
        String result = null;

        if (date != null) {
            result = df.format(date);
        }

        return result;
    }

}
