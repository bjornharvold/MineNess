/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.service.impl;

import com.google.code.ssm.spring.SSMCache;
import com.google.code.ssm.spring.SSMCacheManager;
import com.mineness.domain.Cacheable;
import com.mineness.service.CacheService;
import com.mineness.utils.jackson.CustomObjectMapper;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Bjorn Harvold
 * Date: 4/20/12
 * Time: 1:27 AM
 * Responsibility:
 */
@Service("cacheService")
public class CacheServiceImpl implements CacheService {
    private final static Logger log = LoggerFactory.getLogger(CacheServiceImpl.class);
    private final SSMCacheManager cacheManager;
    private final static ObjectMapper mapper = new CustomObjectMapper();

    @Autowired
    public CacheServiceImpl(SSMCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * This method returns a <code>Cacheable</code> object after ensuring that each is valid
     * 
     * When the ehCache runs out of memory, cached elements are partially returned because 
     * soft references are held by the cache and since memory runs out, the soft references end up returning 
     * null values.
     * 
     * This method ensures that each Cacheable element meets the condition implemented in the <code>validateCacheable</code>
     * method of the Cacheable implementation.
     * 
     */
    @Override
    public Cacheable retrieveCacheable(String cacheName, String cacheKey) {
    	Cacheable v = null;
    	boolean invalid = false;
    	Cache.ValueWrapper wrapper = this.retrieveFromCache(cacheName, cacheKey);
    	if (wrapper != null & wrapper.get() != null){
    		Object o = wrapper.get();
            if (o instanceof Cacheable){
    			if (!((Cacheable)o).validateCacheable()){
    				invalid = true;
    			}
    		}
    		if (!invalid){
    			v = (Cacheable)wrapper.get();
    		} else {
    			v = null;
    			log.warn(String.format("The cache has got corrupt for cache name %s and cache key %s. " +
                        "This could happen because the cache memory is low. Try increasing the value of " +
                        "maxBytesLocalHeap in ehcache.xml.", cacheName, cacheKey));
    		}
    		
    	}
    	return v;
    }
    
    /**
     * This method returns a List of <code>Cacheable</code> objects after ensuring that each is valid
     * 
     * When the ehCache runs out of memory, cached elements are partially returned because 
     * soft references are held by the cache and since memory runs out, the soft references end up returning 
     * null values.
     * 
     * This method ensures that each Cacheable element in the returned List meets the condition implemented in the <code>validateCacheable</code>
     * method of the Cacheable implementation.
     */
    @Override
    public List<? extends Cacheable> retrieveCacheableList(String cacheName, String cacheKey) {

    	List<Cacheable> v = null;
    	boolean invalid = false;
    	Cache.ValueWrapper wrapper = this.retrieveFromCache(cacheName, cacheKey);
    	if (wrapper != null & wrapper.get() != null){
    		Object o = wrapper.get();
    		if (o instanceof List<?>){
    			List<Cacheable> l = (List<Cacheable>)o;
    			for (Cacheable val : l) {
					if (!val.validateCacheable()) {
						invalid = true;
						break;
					}
				}
    		} 
    		if (!invalid){
    			v = (List<Cacheable>)wrapper.get();
    		} else {
    			v = null;
    			log.warn(String.format("The cache has got corrupt for cache name %s and cache key %s. " +
                        "This could happen because the cache memory is low. Try increasing the value of " +
                        "maxBytesLocalHeap in ehcache.xml.", cacheName, cacheKey));
    		}
    		
    	}
    	return v;
    }
    @Override
    public Cache.ValueWrapper retrieveFromCache(String cacheName, String cacheKey) {
        Cache.ValueWrapper result = null;
        Cache cache  = cacheManager.getCache(cacheName);

        // try to retrieve from cache
        if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get(cacheKey);

            if ((wrapper != null) && (wrapper.get() != null)) {
                result = wrapper;
            }
        } else {
            log.warn("Cache with name: " + cacheName + " does not exist.");
        }

        return result;
    }

    @Override
    public void putInCache(String cacheName, String cacheKey, Object cachedObject) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            if ((cachedObject != null) && (cachedObject instanceof Serializable)) {
                cache.put(cacheKey, cachedObject);
            } else {
                log.warn("Cache object is either null or does not implement Serializable: " + cachedObject);
            }
        } else {
            log.warn("Cache with name: " + cacheName + " does not exist.");
        }
    }
    
    /**
     * Returns the cached value in JSON format if it exists in the cache, null if it does not exist
     */
    @Override
    public String retrieveFromCacheAsJSON(String cacheName, String cacheKey) throws CacheException {
        String result = "";
        Cache cache  = cacheManager.getCache(cacheName);

        // try to retrieve from cache
        if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get(cacheKey);
            Object o = null;
            if ((wrapper != null) && (wrapper.get() != null)) {
            	try {
            		o =  wrapper.get();
            		result = mapper.writeValueAsString(o);
            	} catch (Exception e) {
            		throw new CacheException("Cannot convert: " + o.toString());
            	}
            }
        } else {
            log.warn("Cache with name: " + cacheName + " does not exist.");
        }

        return result;
    }

    @Override
    public void removeFromCache(String cacheName, String cacheKey) {
        Cache cache = cacheManager.getCache(cacheName);

        if (cache != null) {
            if (cache.get(cacheKey) != null) {
                cache.evict(cacheKey);
                log.debug("Removed from cache: " + cacheName + ", key: " + cacheKey);
            } else {
                if (log.isInfoEnabled()) {
                    log.info("Object not cached. Cannot remove for key: " + cacheKey);
                }
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("Cache with name: " + cacheName + " does not exist.");
            }
        }
    }

    @Override
    public void removeFromCache(String cacheName, List<String> cacheKeyList){
        for (String key : cacheKeyList) {
            removeFromCache(cacheName, key);
        }
    }

    /**
     * Removes every element for the specific cache
     *
     * @param cacheName cacheName
     */
    @Override
    public void removeAllFromCache(String cacheName) {

        log.info("Remove all from cache: " + cacheName);
        Cache cache = cacheManager.getCache(cacheName);

        if (cache != null) {
            SSMCache ssmCache = (SSMCache) cache.getNativeCache();
            ssmCache.clear();
        } else {
            if (log.isInfoEnabled()) {
                log.info("Cache with name: " + cacheName + " does not exist.");
            }
        }
    }

}
