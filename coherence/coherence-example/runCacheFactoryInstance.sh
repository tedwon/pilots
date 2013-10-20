#!/bin/sh

# This will start a console application
# demonstrating the functionality of the Coherence(tm) API

# specify the Coherence installation directory
COHERENCE_HOME=/Users/ted/study/coherence/coherence

# specify if the console will also act as a server
STORAGE_ENABLED=false

# specify the JVM heap size
MEMORY=64m



if [ $STORAGE_ENABLED == "true" ]; then
	echo "** Starting storage enabled console **"
else
	echo "** Starting storage disabled console **"
fi


JAVA_OPTS="-Xms$MEMORY -Xmx$MEMORY -Dcom.sun.management.jmxremote -Dtangosol.coherence.management=all -Dtangosol.coherence.management.remote=true -Dtangosol.coherence.distributed.localstorage=$STORAGE_ENABLED $JMXPROPERTIES"

java -server -showversion\
 $JAVA_OPTS -cp "$COHERENCE_HOME/lib/coherence.jar:$COHERENCE_HOME/lib/coherence-example-1.0.0-SNAPSHOT.jar"\
 -Dcom.sun.management.jmxremote -Dtangosol.coherence.management=all -Dtangosol.coherence.management.remote=true\
 -Dtangosol.coherence.cluster=mycluster\
 -Dtangosol.coherence.clusteraddress=224.3.7.0\
 -Dtangosol.coherence.clusterport=3700\
 -Dtangosol.coherence.cacheconfig=/Users/ted/study/coherence/coherence-example/src/main/resources/config/examples-cache-config.xml\
 -Dtangosol.pof.config=/Users/ted/study/coherence/coherence-example/src/main/resources/config/pof-config.xml\
 -Dtangosol.coherence.distributed.localstorage=true\
 com.tangosol.net.CacheFactory

# -Dtangosol.coherence.log=/tmp/coherence.log\