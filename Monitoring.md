# Pajamas-Proxy Monitoring In a Nutshell #

Monitoring and managing pajamas-proxy is fairly simple. We expose [JMX](http://en.wikipedia.org/wiki/Java_Management_Extensions) MBeans via HTTP and RMI.

It should be native to Java developers.

## Monitoring details ##

By default pajamas-proxy exposes its MBeans on JMX port 9999, and on HTTP port 8081, and doesn't perform authentication.

### Exposed MBeans ###

Apart from the standard Java MBeans, the daemon also exposes the following MBeans:

  * **com.outbrain.pajamasproxy.memcached.monitor.CacheCluster:type=Manager** - provides means for interacting, and controlling the cache client.
  * **com.outbrain.pajamasproxy.memcached.monitor.Statistics:type=Statistics** - Aggregates the proxy statistics metrics, like hits / misses / errors / etc.
metrics:name=XXXCommand**- performance statistics for XXX memcached command** many more JVM metrics MBeans

### Configuring JMX ###

  * In order to change remote connection details, edit `<install-dir>/conf/wrapper.conf`:
```
wrapper.java.additional.2=-Dcom.sun.management.jmxremote.port=9999
wrapper.java.additional.3=-Dcom.sun.management.jmxremote.authenticate=false
wrapper.java.additional.4=-Dcom.sun.management.jmxremote.ssl=false
```

You may change the JMX remote port, authentication and method.

  * In order to change the HTTP host and port, edit `<install-dir>/conf/pajamas.properties`:
```
# used for JMX HTTP adaptor. (default=0.0.0.0)
#pajamas.jmx.http.host=0.0.0.0

# Port used for JMX HTTP adaptor. (default=8081)
#pajamas.jmx.http.port=8081
```

You may change the HTTP host and port the application will listen to.

### Using a web browser ###

In order to access the pajamas-proxy JMX HTTP adaptor via a web browser, browse to the host:port as specified above. For example: http://localhost:8081/

### Using JConsole ###

In order to access the pajamas-proxy JMX from [JConsole](http://java.sun.com/developer/technicalArticles/J2SE/jconsole.html) specify the host:port as provided above. For example:
```
jconsole localhost:9999
```

### Configure Graphite ###

Since 1.0 metrics can now be sent to graphite.
Edit the `pajamas.properties` file located at `<install-dir>/conf`:
```
pajamas.graphite.host=my.graphite.com
pajamas.graphite.port=2003
# set to true to enable (false by default) ;)
pajamas.graphite.enabled=true
pajamas.graphite.prefix=root.of.pajamas.proxy.metrics
```

Graphite metrics will be added under the specified prefix appended with the host name. For example:
```
services.prod.pajamas-proxy.pjm-00001.AsyncGetCommand.mean
```
Where pjm-00001 is the host name.