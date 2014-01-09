package com.mineness.utils.amazon;

import java.util.Map;

/**
 * Created by Bjorn Harvold
 * Date: 5/18/13
 * Time: 8:02 PM
 * Responsibility:
 */
public interface SignedRequestsHelper {
    String sign(Map<String, String> params);

    String sign(String queryString);
}
