package com.tedwon.pilots.cache;

/**
 * Description.
 *
 * @author Ted Won
 * @since 1.0
 */
public class EhcacheCacheManagerImpl<K, V> extends CacheManager<K, V> {

    public Cache<K, V> getCache(String cacheName) {

        // Singleton style
        net.sf.ehcache.CacheManager cacheManager = net.sf.ehcache.CacheManager.create();
        net.sf.ehcache.Cache ehCache = cacheManager.getCache(cacheName);

        Cache cache = new EhcacheCacheImpl<K, V>(ehCache);

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
