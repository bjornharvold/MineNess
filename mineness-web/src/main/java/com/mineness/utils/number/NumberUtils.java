/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.utils.number;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Bjorn Harvold
 * Date: 11/22/11
 * Time: 11:31 PM
 * Responsibility:
 */
public class NumberUtils {
    public static Double safeDouble(Object object) {
        if (object != null) {
            if (object instanceof Double) {
                return (Double) object;
            } else if (object instanceof Number) {
                return ((Number) object).doubleValue();
            } else if (StringUtils.isNotBlank(object.toString())) {
                try {
                    return Double.valueOf(object.toString());
                } catch (NumberFormatException e) {
                    // Try to strip invalid characters
                    return Double.valueOf(object.toString().replaceAll("[^0-9.]", ""));
                }
            }
        }

        return null;
    }

    public static Integer calculateIterations(Long count, Long maxResults) {
        Double iterations;// this is how many times we are going to paginate through this category if items
        iterations = count.doubleValue() / maxResults.doubleValue();
        iterations = Math.ceil(iterations);

        return iterations.intValue();
    }
}
