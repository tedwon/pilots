#!/bin/sh

# This will start a cache server

COHERENCE_HOME=/Users/ted/study/coherence/coherence

# specify the JVM heap size
MEMORY=128m

java -server -showversion\
 -Xms$MEMORY -Xmx$MEMORY\
 -cp "$COHERENCE_HOME/lib/coherence.jar:$COHERENCE_HOME/lib/coherence-example-1.0.0-SNAPSHOT.jar"\
 -Dcom.sun.management.jmxremote -Dtangosol.coherence.management=all -Dtangosol.coherence.management.remote=true\
 -Dtangosol.coherence.cluster=mycluster\
 -Dtangosol.coherence.clusteraddress=224.3.7.0\
 -Dtangosol.coherence.clusterport=3700\
 -Dtangosol.coherence.cacheconfig=/Users/ted/study/coherence/coherence-example/src/main/resources/config/examples-cache-config.xml\
 -Dtangosol.pof.enabled=true\
 -Dtangosol.pof.config=/Users/ted/study/coherence/coherence-example/src/main/resources/config/pof-config.xml\
 -Dtangosol.coherence.distributed.localstorage=true\
 com.tangosol.net.DefaultCacheServer

#
# -Dtangosol.coherence.log=/tmp/coherence.server.log\
# -Dtangosol.coherence.log.level=4\
# -Dtangosol.coherence.cluster=mycluster -Dtangosol.coherence.clusterport=3700 -Dtangosol.coherence.cacheconfig=/Users/ted/study/coherence/coherence-example/src/main/resources/config/examples-cache-config.xml -Dtangosol.coherence.distributed.localstorage=false -Dtangosol.coherence.log=/tmp/coherence.driver.log