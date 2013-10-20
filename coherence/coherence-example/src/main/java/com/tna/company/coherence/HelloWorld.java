package com.tna.company.coherence;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.Cluster;
import com.tangosol.net.NamedCache;

import java.util.Enumeration;

public class HelloWorld {

    public static void main(String[] args) {

        String key = "k1";
        String value = "Hello World!2";

//        Cluster cluster = CacheFactory.ensureCluster();
//        String clusterName = cluster.getClusterName();
//        Enumeration enums = cluster.getServiceNames();

//        NamedCache cache = CacheFactory.getCache("hello-example");
        NamedCache cache = CacheFactory.getCache("contacts");

        cache.put(key, value);
        System.out.println((String) cache.get(key));
        cache.remove(key);
        System.out.println((String) cache.get(key));

//        CacheFactory.shutdown();
    }
}