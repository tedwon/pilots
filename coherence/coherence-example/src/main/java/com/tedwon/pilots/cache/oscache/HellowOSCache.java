package com.tedwon.pilots.cache.oscache;

import com.opensymphony.oscache.base.AbstractCacheAdministrator;
import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

import java.util.Properties;

/**
 * Description.
 *
 * @author Edward KIM
 * @since 1.0
 */
public class HellowOSCache {

    public static void main(String[] args) throws Exception {

        String key = "mykey";
        String value = "myvalue";


        // classpath:oscache.properties
        GeneralCacheAdministrator admin = new GeneralCacheAdministrator();
        Cache cache = admin.getCache();
        System.out.println(cache);
        cache.putInCache(key, value);
        System.out.println(cache.getFromCache(key));


        // set up custom properties
        Properties config = new Properties();
        int CACHE_SIZE = 100;
        config.setProperty("cache.key", "MyCustomCache");
        config.setProperty(AbstractCacheAdministrator.CACHE_CAPACITY_KEY, Integer.toString(CACHE_SIZE));
        GeneralCacheAdministrator customAdmin = new GeneralCacheAdministrator(config);
        Cache customCache = customAdmin.getCache();
        System.out.println(customCache);
        customCache.putInCache(key, value);
        System.out.println(customCache.getFromCache(key));


    }
}
