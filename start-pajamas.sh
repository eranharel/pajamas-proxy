mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt
export CP=`cat classpath.txt`

java -cp $CP:target/pajamas-proxy-0.1-SNAPSHOT.jar -Dpajamas.properties=src/test/resources/pajamas-local.properties -Xmx2G com.outbrain.pajamasproxy.memcached.PajamasProxyDaemon
