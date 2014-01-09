package com.mineness.service;

import com.mineness.domain.Cacheable;
import com.mineness.service.impl.CacheException;
import org.springframework.cache.Cache;

import java.util.List;

/**
 * Created by Bjorn Harvold
 * Date: 4/20/12
 * Time: 1:29 AM
 * Responsibility:
 */
public interface CacheService {
	
	Cacheable retrieveCacheable(String cacheName, String cacheKey);
	
	List<? extends Cacheable> retrieveCacheableList(String cacheName, String cacheKey);
	
    Cache.ValueWrapper retrieveFromCache(String cacheName, String cacheKey);

    void putInCache(String cacheName, String cacheKey, Object cachedObject);

    void removeFromCache(String cacheName, String cacheKey);

    void removeAllFromCache(String cacheName);
    
    String retrieveFromCacheAsJSON(String cacheName, String cacheKey) throws CacheException;

    void removeFromCache(String cacheName, List<String> cacheKeyList);
}
