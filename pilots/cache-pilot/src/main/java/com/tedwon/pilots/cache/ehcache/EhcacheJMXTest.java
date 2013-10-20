package com.tedwon.pilots.cache.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import com.tedwon.pilots.cache.ehcache.management.ManagementService;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;

/**
 * Created by IntelliJ IDEA.
 * User: ted
 * Date: 11. 06. 06
 * Time: PM 4:26
 * To change this template use File | Settings | File Templates.
 */
public class EhcacheJMXTest {

    public static void main(String[] args) {

        // Singleton style
        CacheManager cacheManager = CacheManager.create();

        // http://ehcache.org/documentation/jmx.html#JConsole_Example
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ManagementService.registerMBeans(cacheManager, mBeanServer, true, true, true, true);

        Cache cache = cacheManager.getCache("mycache");

        for (int i = 0; i < 100; i++) {
            Element element = new Element("key1" + i, "value1");
            cache.put(element);
//            System.out.println(cache.get("key1" + i));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }

            element = new Element("key1" + i, "value2");
            cache.put(element);
//            System.out.println(cache.get("key1" + i));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }
    }
}
