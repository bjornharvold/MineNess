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

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

public class StringToDateConverter implements Converter<String, Date> {
    private DateTimeFormatter df;

    public StringToDateConverter(String dateFormat) {
        this.df = DateTimeFormat.forPattern(dateFormat);
    }

    @Override
    public Date convert(String date) {
        Date result = null;

        if (StringUtils.isNotBlank(date)) {
            result = df.parseDateTime(date).toDate();
        }

        return result;
    }

}
