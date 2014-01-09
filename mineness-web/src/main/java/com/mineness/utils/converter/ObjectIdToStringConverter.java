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
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

public class ObjectIdToStringConverter implements Converter<ObjectId, String> {

    @Override
    public String convert(ObjectId source) {
        String result = null;

        if (source != null) {
            result = source.toString();
        }

        return result;
    }

}
