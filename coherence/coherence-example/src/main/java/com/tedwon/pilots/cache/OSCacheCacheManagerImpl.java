package com.tedwon.pilots.cache;

import com.opensymphony.oscache.base.AbstractCacheAdministrator;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

import java.util.Properties;

/**
 * Description.
 *
 * @author Ted Won
 * @since 1.0
 */
public class OSCacheCacheManagerImpl<K, V> extends CacheManager<K, V> {

    public Cache<K, V> getCache(String cacheName) {

        // set up mycache properties
        Properties config = new Properties();
        int CACHE_SIZE = 100;
        config.setProperty("cache.key", "mycache");
        config.setProperty(AbstractCacheAdministrator.CACHE_CAPACITY_KEY, Integer.toString(CACHE_SIZE));
        GeneralCacheAdministrator customAdmin = new GeneralCacheAdministrator(config);
        com.opensymphony.oscache.base.Cache myCache = customAdmin.getCache();

        Cache cache = new OSCacheCacheImpl<K, V>(myCache);

        return cache;
    }

    /**
     * Adds a {@link Cache} to the CacheManager.
     * <p/>
     *
     * @param cache
     */
    @Override
    void addCache(Cache<K, V> kvCache) throws CacheException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Checks whether a cache of type ehcache exists.
     * <p/>
     *
     * @param cacheName the cache name to check for
     * @return true if it exists
     */
    @Override
    boolean cacheExists(String cacheName) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Remove a cache from the CacheManager. The cache is disposed of.
     *
     * @param cacheName the cache name
     */
    @Override
    public void removeCache(String cacheName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
