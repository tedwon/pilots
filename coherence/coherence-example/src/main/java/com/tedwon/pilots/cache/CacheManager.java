package com.tedwon.pilots.cache;

/**
 * Description.
 *
 * @author Ted Won
 * @since 1.0
 */
public abstract class CacheManager<K, V> {

    /**
     * Gets a named cache.
     *
     * @param cacheName
     * @return the Cahce or null if it does exist
     */
    abstract Cache<K, V> getCache(String cacheName);


    public Cache<K, V> getCache(String cacheName, Cache.Products cacheProductName) {
        return getCache(cacheName);
    }

    /**
     * Adds a {@link Cache} to the CacheManager.
     * <p/>
     *
     * @param cache
     */
    abstract void addCache(Cache<K, V> cache) throws CacheException;

    /**
     * Checks whether a cache of type ehcache exists.
     * <p/>
     *
     * @param cacheName the cache name to check for
     * @return true if it exists
     */
    abstract boolean cacheExists(String cacheName);


    /**
     * Remove a cache from the CacheManager. The cache is disposed of.
     *
     * @param cacheName the cache name
     */
    abstract public void removeCache(String cacheName);
}
