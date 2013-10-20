/**
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
 *
 * @author Pierre Monestie (pmonestie__REMOVE__THIS__@gmail.com)
 * @author <a href="mailto:gluck@gregluck.com">Greg Luck</a>
 * @version $Id: JGroupsReplicationTest.java 2931 2010-10-14 02:09:50Z gluck $
 */

package com.pilots.cache.ehcache.replicated.jms;


import net.sf.ehcache.*;
import net.sf.ehcache.util.ClassLoaderUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.pilots.cache.ehcache.replicated.jms.TestUtil.forceVMGrowth;
import static org.junit.Assert.*;

/**
 * ActiveMQ seems to have a bug in 5.1 where it does not cleanup temporary queues, even though they have been
 * deleted. That bug appears to be long standing. 5.2 as of 10/08 was not released.
 * http://www.nabble.com/Memory-Leak-Using-Temporary-Queues-td11218217.html#a11218217
 * http://issues.apache.org/activemq/browse/AMQ-1255
 */
public class ActiveMQJMSReplicationTest {


    static final int NBR_ELEMENTS = 1000;

    static final String SAMPLE_CACHE_ASYNC = "sampleCacheAsync";
    static final String SAMPLE_CACHE_SYNC = "sampleCacheSync";
    static final String SAMPLE_CACHE_NOREP = "sampleCacheNorep";
    static final String SAMPLE_CACHE_JMS_REPLICATION_BOOTSTRAP = "sampleJMSReplicateRMIBootstrap";

    String cacheName;

    private static final Logger LOG = Logger.getLogger(ActiveMQJMSReplicationTest.class.getName());

    protected CacheManager manager1, manager2, manager3, manager4;

    protected String getConfigurationFile() {
        return "ehcache-distributed-jms-activemq.xml";
    }

    @Before
    public void setUp() throws Exception {

        manager1 = new CacheManager(ClassLoaderUtil.getStandardClassLoader().getResource(getConfigurationFile()));
        manager1.setName("manager1");
        manager2 = new CacheManager(ClassLoaderUtil.getStandardClassLoader().getResource(getConfigurationFile()));
        manager2.setName("manager2");
        manager3 = new CacheManager(ClassLoaderUtil.getStandardClassLoader().getResource(getConfigurationFile()));
        manager3.setName("manager3");
        manager4 = new CacheManager(ClassLoaderUtil.getStandardClassLoader().getResource(getConfigurationFile()));
        manager4.setName("manager4");
        cacheName = SAMPLE_CACHE_ASYNC;
        Thread.sleep(200);
    }

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
        Thread.sleep(20);
    }


//    @Test
//    public void testBasicReplicationAsynchronous() throws Exception {
//        cacheName = SAMPLE_CACHE_ASYNC;
//        basicReplicationTest();
//    }

//    @Test
//    public void testBasicReplicationSynchronous() throws Exception {
//        cacheName = SAMPLE_CACHE_SYNC;
//        basicReplicationTest();
//    }


    public void basicReplicationTest() throws Exception {

        //put
        for (int i = 0; i < NBR_ELEMENTS; i++) {
            manager1.getCache(cacheName).put(new Element(i, "testdat"));
        }
        Thread.sleep(3000);

        LOG.info(manager1.getCache(cacheName).getKeys().size() + "  " + manager2.getCache(cacheName).getKeys().size()
                + " " + manager3.getCache(cacheName).getKeys().size()
                + " " + manager4.getCache(cacheName).getKeys().size());

        assertTrue(manager1.getCache(cacheName).getKeys().size() == manager2.getCache(cacheName).getKeys().size() &&
                manager1.getCache(cacheName).getKeys().size() == manager3.getCache(cacheName).getKeys().size() &&
                manager1.getCache(cacheName).getKeys().size() == manager4.getCache(cacheName).getKeys().size() &&
                manager1.getCache(cacheName).getKeys().size() == NBR_ELEMENTS);

        //update via copy
        for (int i = 0; i < NBR_ELEMENTS; i++) {
            manager1.getCache(cacheName).put(new Element(i, "testdat"));
        }
        Thread.sleep(3000);

        LOG.info(manager1.getCache(cacheName).getKeys().size() + "  " + manager2.getCache(cacheName).getKeys().size()
                + " " + manager3.getCache(cacheName).getKeys().size()
                + " " + manager4.getCache(cacheName).getKeys().size());

        assertTrue(manager1.getCache(cacheName).getKeys().size() == manager2.getCache(cacheName).getKeys().size() &&
                manager1.getCache(cacheName).getKeys().size() == manager3.getCache(cacheName).getKeys().size() &&
                manager1.getCache(cacheName).getKeys().size() == manager4.getCache(cacheName).getKeys().size() &&
                manager1.getCache(cacheName).getKeys().size() == NBR_ELEMENTS);


        //remove
        manager1.getCache(cacheName).remove(0);
        Thread.sleep(1010);

        LOG.info(manager1.getCache(cacheName).getKeys().size() + "  " + manager2.getCache(cacheName).getKeys().size()
                + " " + manager3.getCache(cacheName).getKeys().size()
                + " " + manager4.getCache(cacheName).getKeys().size());

        assertTrue(manager1.getCache(cacheName).getKeys().size() == manager2.getCache(cacheName).getKeys().size() &&
                manager1.getCache(cacheName).getKeys().size() == manager3.getCache(cacheName).getKeys().size() &&
                manager1.getCache(cacheName).getKeys().size() == manager4.getCache(cacheName).getKeys().size() &&
                manager1.getCache(cacheName).getKeys().size() == NBR_ELEMENTS - 1);

        //removeall
        manager1.getCache(cacheName).removeAll();
        Thread.sleep(1010);

        LOG.info(manager1.getCache(cacheName).getKeys().size() + "  " + manager2.getCache(cacheName).getKeys().size()
                + " " + manager3.getCache(cacheName).getKeys().size()
                + " " + manager4.getCache(cacheName).getKeys().size());

        assertTrue(manager1.getCache(cacheName).getKeys().size() == manager2.getCache(cacheName).getKeys().size() &&
                manager1.getCache(cacheName).getKeys().size() == manager3.getCache(cacheName).getKeys().size() &&
                manager1.getCache(cacheName).getKeys().size() == manager4.getCache(cacheName).getKeys().size() &&
                manager1.getCache(cacheName).getKeys().size() == 0);

    }


    @Test
    public void run() throws Exception {
        cacheName = SAMPLE_CACHE_SYNC;


        //put
        for (int i = 0; i < NBR_ELEMENTS; i++) {
            manager1.getCache(cacheName).put(new Element(i, "testdat"));
        }
        Thread.sleep(6000);

        LOG.info(manager1.getCache(cacheName).getKeys().size() + "  " + manager2.getCache(cacheName).getKeys().size()
                + " " + manager3.getCache(cacheName).getKeys().size()
                + " " + manager4.getCache(cacheName).getKeys().size());

        assertTrue(manager1.getCache(cacheName).getKeys().size() == manager2.getCache(cacheName).getKeys().size() &&
                manager1.getCache(cacheName).getKeys().size() == manager3.getCache(cacheName).getKeys().size() &&
                manager1.getCache(cacheName).getKeys().size() == manager4.getCache(cacheName).getKeys().size() &&
                manager1.getCache(cacheName).getKeys().size() == NBR_ELEMENTS);

    }


    public static void main(String[] args) throws Exception {

        ActiveMQJMSReplicationTest test = new ActiveMQJMSReplicationTest();
        test.setUp();
        test.run();
        test.tearDown();

    }

}

