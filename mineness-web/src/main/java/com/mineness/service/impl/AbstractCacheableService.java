/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.service.impl;

import com.mineness.service.CacheService;
import org.springframework.cache.Cache;

import java.util.List;

/**
 * Created by Bjorn Harvold
 * Date: 5/2/12
 * Time: 4:24 PM
 * Responsibility:
 */
public abstract class AbstractCacheableService {
    protected final CacheService cacheService;

    protected AbstractCacheableService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * Method description
     *
     * @param cacheName    cacheName
     * @param cacheKey     cacheKey
     * @param cachedObject cachedObject
     */
    protected void putInCache(String cacheName, String cacheKey, Object cachedObject) {
        cacheService.putInCache(cacheName, cacheKey, cachedObject);
    }

    /**
     * Method description
     *
     * @param cacheName cacheName
     * @param cacheKey  cacheKey
     * @return Return value
     */
    protected Cache.ValueWrapper retrieveFromCache(String cacheName, String cacheKey) {
        return cacheService.retrieveFromCache(cacheName, cacheKey);
    }

    protected void removeFromCache(String cacheName, String cacheKey) {
        cacheService.removeFromCache(cacheName, cacheKey);
    }

    protected void removeAllFromCache(String cacheName) {
        cacheService.removeAllFromCache(cacheName);
    }

    protected void removeFromCache(String cacheName, List<String> cacheKeys) {
        cacheService.removeFromCache(cacheName, cacheKeys);
    }
}
