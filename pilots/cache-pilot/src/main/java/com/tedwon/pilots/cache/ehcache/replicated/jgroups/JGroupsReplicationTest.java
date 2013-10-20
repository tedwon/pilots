package com.tedwon.pilots.cache.ehcache.replicated.jgroups;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: ted
 * Date: 11. 06. 09
 * Time: PM 3:23
 * To change this template use File | Settings | File Templates.
 */
public class JGroupsReplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(JGroupsReplicationTest.class.getName());

    private static final long MAX_WAIT_TIME = 60000;

    private static final String SAMPLE_CACHE_NOREP = "sampleCacheNorep";

    private static final int NBR_ELEMENTS = 100;

    private static final String SAMPLE_CACHE1 = "sampleCacheAsync";
    private static final String SAMPLE_CACHE2 = "sampleCacheAsync2";

    public void process() throws Exception {

        URL asyncConfigUrl1 = CacheTestUtilities.ASYNC_CONFIG_URL1;
        CacheManager manager1 = new CacheManager(asyncConfigUrl1);
        CacheTestUtilities.waitForBootstrap(manager1, MAX_WAIT_TIME);

        CacheManager manager2 = new CacheManager(CacheTestUtilities.ASYNC_CONFIG_URL2);
        CacheTestUtilities.waitForBootstrap(manager2, MAX_WAIT_TIME);

        CacheManager manager3 = new CacheManager(CacheTestUtilities.ASYNC_CONFIG_URL3);
        CacheTestUtilities.waitForBootstrap(manager3, MAX_WAIT_TIME);

        CacheManager manager4 = new CacheManager(CacheTestUtilities.ASYNC_CONFIG_URL4);
        CacheTestUtilities.waitForBootstrap(manager4, MAX_WAIT_TIME);

        String cacheName = SAMPLE_CACHE1;

        // Test codes here

        LOG.info("START TEST");

        final Ehcache cache1 = manager1.getEhcache(cacheName);
        final Ehcache cache2 = manager2.getEhcache(cacheName);
        final Ehcache cache3 = manager3.getEhcache(cacheName);
        final Ehcache cache4 = manager4.getEhcache(cacheName);

        for (int i = 0; i < NBR_ELEMENTS; i++) {
            cache1.put(new Element(i, "testdat"));
        }

        //Wait up to 3 seconds for the caches to become coherent
        CacheTestUtilities.waitForReplication(NBR_ELEMENTS, MAX_WAIT_TIME, cache2, cache3, cache4);

        assertEquals(NBR_ELEMENTS, cache1.getKeys().size());
        assertEquals(NBR_ELEMENTS, cache2.getKeys().size());
        assertEquals(NBR_ELEMENTS, cache3.getKeys().size());
        assertEquals(NBR_ELEMENTS, cache4.getKeys().size());

        cache1.removeAll();

        //Wait up to 3 seconds for the caches to become coherent
        CacheTestUtilities.waitForReplication(0, MAX_WAIT_TIME, cache2, cache3, cache4);

        assertEquals(0, cache1.getKeys().size());
        assertEquals(0, cache2.getKeys().size());
        assertEquals(0, cache3.getKeys().size());
        assertEquals(0, cache4.getKeys().size());

        LOG.info("END TEST");


        manager1.shutdown();
        LOG.debug("Tearing down manager1");
        manager2.shutdown();
        LOG.debug("Tearing down manager2");

        LOG.info("TEARDOWN");
        CacheTestUtilities.endTest();
    }


    public static void main(String[] args) throws Exception {
        JGroupsReplicationTest test = new JGroupsReplicationTest();
        test.process();
    }
}


