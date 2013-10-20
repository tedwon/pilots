package com.tedwon.pilots.cache.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class HellowEhcache {

    public static void main(String[] args) {

        // Singleton style
        CacheManager cacheManager = CacheManager.create();
        Cache cache = cacheManager.getCache("mycache");
        Element element = new Element("key1", "value1");
        cache.put(element);
        System.out.println(cache.get("key1"));
        element = new Element("key1", "value2");
        cache.put(element);
        System.out.println(cache.get("key1"));
    }
}
