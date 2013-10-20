package com.tna.pilots.jboss.web.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.*;
import java.util.List;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.Cluster;
import com.tangosol.net.NamedCache;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read the contents of the members table.
 */
@Path("/cache")
public class CacheResource {


    @GET
    @Path("/{key}/{value}")
    @Consumes({"text/plain"})
    @Produces({"text/plain"})
    public String get(@PathParam("key") String key, @PathParam("value") String value) {

        NamedCache cache = CacheFactory.getCache("mycache");
        cache.put(key, value);

        System.out.println(cache.get(key));

        return key;
    }
}
