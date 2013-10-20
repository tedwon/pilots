package com.tedwon.pilots.cache;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Set;

/**
 * Description.
 * <p/>
 * Client should use abstract interface to hide cache implementations.
 *
 * @author Ted Won
 * @since 1.0
 */
public class ServiceClass {

    AbstractApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{
        "classpath:applicationContext.xml"
    });

    String key = "home";
    String value = "seoul";


    public void example1() {

        CacheManager<String, String> cacheManager = (CacheManager<String, String>) applicationContext.getBean("cacheManager");

        Cache<String, String> cache = cacheManager.getCache("mycache");
        cache.put(key, value);

        String myValue = cache.get(key);
        System.out.println(myValue);

        Set<String> keys = cache.keySet();
        System.out.println(keys);

        cache.remove(key);

        String myValue2 = cache.get(key);
        System.out.println(myValue2);


        keys = cache.keySet();
        System.out.println(keys);


    }

    public void example2() {

        CacheManager<String, String> cacheManager = (CacheManager<String, String>) applicationContext.getBean("productSelectCacheManager");

        Cache<String, String> cache = cacheManager.getCache("mycache", Cache.Products.COHERENCE);
        cache.put(key, value);

        String myValue = cache.get(key);
        System.out.println(myValue);

        Set<String> keys = cache.keySet();
        System.out.println(keys);

        cache.remove(key);

        String myValue2 = cache.get(key);
        System.out.println(myValue2);


        keys = cache.keySet();
        System.out.println(keys);


    }

    public static void main(String[] args) {

        ServiceClass service = new ServiceClass();

        service.example1();

        service.example2();

    }


}
