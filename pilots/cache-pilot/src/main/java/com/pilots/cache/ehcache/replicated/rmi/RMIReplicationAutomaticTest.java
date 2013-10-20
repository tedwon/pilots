package com.pilots.cache.ehcache.replicated.rmi;

import com.tedwon.pilots.cache.ehcache.replicated.JVMUtil;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.distribution.CacheManagerPeerListener;
import net.sf.ehcache.distribution.MulticastKeepaliveHeartbeatSender;
import net.sf.ehcache.util.ClassLoaderUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by IntelliJ IDEA.
 * User: ted
 * Date: 11. 06. 09
 * Time: PM 2:31
 * To change this template use File | Settings | File Templates.
 */
public class RMIReplicationAutomaticTest {

    /**
     * Where the config is
     */
    public static final String SRC_CONFIG_DIR = "D:\\ted\\Data\\pilots\\tedwon-pilot-project\\pilots\\cache-pilot\\src\\main\\resources\\";


    /**
     * Where the test config is
     */
    public static final String TEST_CONFIG_DIR = "/Users/ted/study/ehcache/sources/ehcache-core-2.4.3/src/test/resources/";

    /**
     * A value to represent replicate asynchronously
     */
    protected static final boolean ASYNCHRONOUS = true;

    /**
     * A value to represent replicate synchronously
     */
    protected static final boolean SYNCHRONOUS = false;

    public void process() throws Exception {

        MulticastKeepaliveHeartbeatSender.setHeartBeatInterval(1000);



        CacheManager manager1 = new CacheManager(ClassLoaderUtil.getStandardClassLoader().getResource("ehcache-distributed-rmi-automatic-1.xml"));
        CacheManagerPeerListener cachePeerListener = manager1.getCachePeerListener("RMI");

        CacheManager manager2 = new CacheManager(ClassLoaderUtil.getStandardClassLoader().getResource("ehcache-distributed-rmi-automatic-2.xml"));

        //allow cluster to be established
        Thread.sleep(1020);

        /**
         * The name of the cache under test
         */
        String cacheName = "sampleCache1";
        /**
         * CacheManager 1 of 2s cache being replicated
         */
        Ehcache cache1;

        /**
         * CacheManager 2 of 2s cache being replicated
         */
        Ehcache cache2;

        cache1 = manager1.getCache(cacheName);
        cache1.removeAll();

        cache2 = manager2.getCache(cacheName);
        cache2.removeAll();

        //enable distributed removeAlls to finish
        waitForPropagate();


        // Test Codes here
        /**
         * test removeAll sync
         */
        removeAllTest(manager1.getCache("sampleCache1"), manager2.getCache("sampleCache1"), ASYNCHRONOUS);


        if (manager1 != null) {
            manager1.shutdown();
        }
        if (manager2 != null) {
            manager2.shutdown();
        }

        List<Thread> activeReplicationThreads = new ArrayList<Thread>();

        for (int i = 0; i < 30; i++) {
            activeReplicationThreads.clear();
            for (Thread thread : JVMUtil.enumerateThreads()) {
                if (thread.getName().equals("Replication Thread")) {
                    activeReplicationThreads.add(thread);
                }
            }

            if (activeReplicationThreads.isEmpty()) {
                return;
            } else {
                Thread.sleep(1000);
            }
        }
    }

    /**
     * Tests removeAll initiated from a cache to another cache in a cluster
     * <p/>
     * This test goes into an infinite loop if the chain of notifications is not somehow broken.
     */
    public void removeAllTest(Ehcache fromCache, Ehcache toCache, boolean asynchronous) throws Exception {

        //removeAll is distributed. Stop it colliding with the rest of the test
        waitForPropagate();


        Serializable key = new Date();
        Serializable value = new Date();
        Element element1 = new Element(key, value);

        //Put
        fromCache.put(element1);


        if (asynchronous) {
            waitForPropagate();
        }

        //Should have been replicated to cache2.
        Element element2 = toCache.get(key);
        assertEquals(element1, element2);

        //Remove
        fromCache.removeAll();
        if (asynchronous) {
            waitForPropagate();
        }

        //Should have been replicated to cache2.
        element2 = toCache.get(key);
        assertNull(element2);
        assertEquals(0, toCache.getSize());

    }


    /**
     * Need to wait for async
     *
     * @throws InterruptedException
     */
    protected void waitForPropagate() throws InterruptedException {
        Thread.sleep(1500);
    }

    public static void main(String[] args) throws Exception {
        RMIReplicationAutomaticTest test = new RMIReplicationAutomaticTest();
        test.process();
    }
}
