/**
 *  Copyright 2003-2010 Terracotta, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.sf.ehcache.distribution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.sf.ehcache.AbstractCacheTest;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.ThreadKiller;
import net.sf.ehcache.event.CountingCacheEventListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests replication of Cache events
 * <p/>
 * Note these tests need a live network interface running in multicast mode to work
 * <p/>
 * If running involving RMIAsynchronousCacheReplicator individually the test will fail because
 * the VM will gobble up the SoftReferences rather than allocating more memory. Uncomment the
 * forceVMGrowth() method usage in setup.
 *
 * @author Greg Luck
 * @version $Id: RMICacheReplicatorTest.java 3409 2011-01-04 03:36:52Z gluck $
 */


//
// Please close jira MNK-1377 after fixing ignored tests below
//

public class RMICacheReplicatorTest extends AbstractCacheTest {


    /**
     * A value to represent replicate asynchronously
     */
    protected static final boolean ASYNCHRONOUS = true;

    /**
     * A value to represent replicate synchronously
     */
    protected static final boolean SYNCHRONOUS = false;

    private static final Logger LOG = LoggerFactory.getLogger(RMICacheReplicatorTest.class.getName());


    /**
     * CacheManager 1 in the cluster
     */
    protected CacheManager manager1;
    /**
     * CacheManager 2 in the cluster
     */
    protected CacheManager manager2;
    /**
     * CacheManager 3 in the cluster
     */
    protected CacheManager manager3;
    /**
     * CacheManager 4 in the cluster
     */
    protected CacheManager manager4;
    /**
     * CacheManager 5 in the cluster
     */
    protected CacheManager manager5;
    /**
     * CacheManager 6 in the cluster
     */
    protected CacheManager manager6;

    /**
     * The name of the cache under test
     */
    protected String cacheName = "sampleCache1";
    /**
     * CacheManager 1 of 2s cache being replicated
     */
    protected Ehcache cache1;

    /**
     * CacheManager 2 of 2s cache being replicated
     */
    protected Ehcache cache2;

    /**
     * Allows setup to be the same
     */
    protected String cacheNameBase = "ehcache-distributed";

    /**
     * {@inheritDoc}
     * Sets up two caches: cache1 is local. cache2 is to be receive updates
     *
     * @throws Exception
     */
    @Override
    @Before
    public void setUp() throws Exception {

        //Required to get SoftReference tests to pass. The VM clean up SoftReferences rather than allocating
        // memory to -Xmx!
//        forceVMGrowth();
//        System.gc();
        MulticastKeepaliveHeartbeatSender.setHeartBeatInterval(1000);

        net.sf.ehcache.event.CountingCacheEventListener.resetCounters();
        manager1 = new CacheManager(AbstractCacheTest.TEST_CONFIG_DIR + "distribution/ehcache-distributed1.xml");
        manager2 = new CacheManager(AbstractCacheTest.TEST_CONFIG_DIR + "distribution/ehcache-distributed2.xml");
        manager3 = new CacheManager(AbstractCacheTest.TEST_CONFIG_DIR + "distribution/ehcache-distributed3.xml");
        manager4 = new CacheManager(AbstractCacheTest.TEST_CONFIG_DIR + "distribution/ehcache-distributed4.xml");
        manager5 = new CacheManager(AbstractCacheTest.TEST_CONFIG_DIR + "distribution/ehcache-distributed5.xml");

        //manager6 = new CacheManager(AbstractCacheTest.TEST_CONFIG_DIR + "distribution/ehcache-distributed-jndi6.xml");

        //allow cluster to be established
        Thread.sleep(1020);

        cache1 = manager1.getCache(cacheName);
        cache1.removeAll();

        cache2 = manager2.getCache(cacheName);
        cache2.removeAll();

        //enable distributed removeAlls to finish
        waitForPropagate();


    }

    /**
     * {@inheritDoc}
     *
     * @throws Exception
     */
    @Override
    @After
    public void tearDown() throws Exception {

        if (manager1 != null) {
            manager1.shutdown();
        }
        if (manager2 != null) {
            manager2.shutdown();
        }
        if (manager3 != null) {
            manager3.shutdown();
        }
        if (manager4 != null) {
            manager4.shutdown();
        }
        if (manager5 != null) {
            manager5.shutdown();
        }
        if (manager6 != null) {
            manager6.shutdown();
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

        fail("There should not be any replication threads running after shutdown [" + activeReplicationThreads.size() + " still running]");
    }

    /**
     * test removeAll sync
     */
    @Test
    public void testRemoveAllAsynchronous() throws Exception {
        removeAllTest(manager1.getCache("sampleCache1"), manager2.getCache("sampleCache1"), ASYNCHRONOUS);
    }

    /**
     * test removeAll async
     */
    @Test
    public void testRemoveAllSynchronous() throws Exception {
        removeAllTest(manager1.getCache("sampleCache3"), manager2.getCache("sampleCache3"), SYNCHRONOUS);
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
     * 5 cache managers should means that each cache has four remote peers
     */

    @Test
    public void testRemoteCachePeersEqualsNumberOfCacheManagersInCluster() {

        CacheManagerPeerProvider provider = manager1.getCacheManagerPeerProvider("RMI");
        List remotePeersOfCache1 = provider.listRemoteCachePeers(cache1);
        assertEquals(4, remotePeersOfCache1.size());
    }


    /**
     * Need to wait for async
     *
     * @throws InterruptedException
     */
    protected void waitForPropagate() throws InterruptedException {
        Thread.sleep(1500);
    }

    /**
     * Need to wait for async
     *
     * @throws InterruptedException
     */
    protected void waitForSlowPropagate() throws InterruptedException {
        Thread.sleep(6000);
    }


    /**
     * Distributed operations create extra scope for deadlock.
     * This test checks whether a distributed deadlock scenario exists for synchronous replication
     * of each distributed operation all at once.
     * It shows that no distributed deadlock exists for asynchronous replication. It is multi thread
     * and multi process safe.
     * <p/>
     * Carefully tailored to exercise:
     * <ol>
     * <li>overflow to disk. We put in 20 things and the memory size is 10
     * <li>each peer is working on the same set of keys thus maximising contention
     * <li>we do puts, gets and removes to explore all the execution paths
     * </ol>
     * If a deadlock occurs, processing will stop until a SocketTimeout exception is thrown and
     * the deadlock will be released.
     */
    @Test
    public void testCacheOperationsSynchronousMultiThreaded() throws Exception, InterruptedException {

        // Run a set of threads, that attempt to fetch the elements
        final List executables = new ArrayList();

        executables.add(new ClusterExecutable(manager1, "sampleCache3"));
        executables.add(new ClusterExecutable(manager2, "sampleCache3"));
        executables.add(new ClusterExecutable(manager3, "sampleCache3"));

        runThreads(executables);
    }


    /**
     * Distributed operations create extra scope for deadlock.
     * This test checks whether a distributed deadlock scenario exists for asynchronous replication
     * of each distributed operation all at once.
     * It shows that no distributed deadlock exists for asynchronous replication. It is multi thread
     * and multi process safe.
     * It uses sampleCache2, which is configured to be asynchronous
     * <p/>
     * Carefully tailored to exercise:
     * <ol>
     * <li>overflow to disk. We put in 20 things and the memory size is 10
     * <li>each peer is working on the same set of keys thus maximising contention
     * <li>we do puts, gets and removes to explore all the execution paths
     * </ol>
     */
    @Test
    public void testCacheOperationsAynchronousMultiThreaded() throws Exception, InterruptedException {

        // Run a set of threads, that attempt to fetch the elements
        final List executables = new ArrayList();

        executables.add(new ClusterExecutable(manager1, "sampleCache2"));
        executables.add(new ClusterExecutable(manager2, "sampleCache2"));
        executables.add(new ClusterExecutable(manager3, "sampleCache2"));

        runThreads(executables);
    }

    /**
     * An Exececutable which allows the CacheManager to be set
     */
    class ClusterExecutable implements Executable {

        private final CacheManager manager;
        private final String cacheName;

        /**
         * Construct with CacheManager
         *
         * @param manager
         */
        public ClusterExecutable(CacheManager manager, String cacheName) {
            this.manager = manager;
            this.cacheName = cacheName;
        }

        /**
         * Execute
         *
         * @throws Exception
         */
        public void execute() throws Exception {
            Random random = new Random();

            for (int i = 0; i < 20; i++) {
                Integer key = Integer.valueOf((i));
                int operationSelector = random.nextInt(4);
                Cache cache = manager.getCache(cacheName);
                if (operationSelector == 100) {
                    cache.get(key);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(cache.getGuid() + ": get " + key);
                    }
                } else if (operationSelector == 100) {
                    cache.remove(key);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(cache.getGuid() + ": remove " + key);
                    }
                } else if (operationSelector == 2) {
                    cache.put(new Element(key,
                            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                                    + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                                    + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                                    + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                                    + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(cache.getGuid() + ": put " + key);
                    }
                } else {
                    //every twelfth time 1/4 * 1/3 = 1/12
                    if (random.nextInt(3) == 1) {
                        LOG.debug("cache.removeAll()");
                        cache.removeAll();
                    }
                }
            }

        }
    }

}
