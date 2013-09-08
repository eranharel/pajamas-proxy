package com.outbrain.pajamasproxy.memcached.metrics;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

/**
 * Time: 9/7/13 3:04 PM
 *
 * @author Eran Harel
 */
class GraphiteReporterFactory {

  private final GraphiteReporter graphiteReporter;

  public GraphiteReporterFactory(String graphiteHost, int graphitePort, boolean graphiteEnabled, MetricRegistry metrics, String metricsPrefix) {
    if (graphiteEnabled) {
      final Graphite graphite = new Graphite(new InetSocketAddress(graphiteHost, graphitePort));
      graphiteReporter = GraphiteReporter.forRegistry(metrics).prefixedWith(metricsPrefix + "." + hostname()).convertRatesTo(TimeUnit.SECONDS)
                                         .convertDurationsTo(TimeUnit.MILLISECONDS).build(graphite);
    } else {
      graphiteReporter = null;
    }
  }

  private String hostname() {
    String hostName = null;
    try {
      hostName = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      hostName = "unknown";
    }

    return hostName.replace('.', '_');
  }

  public void start() {
    if (graphiteReporter != null) {
      graphiteReporter.start(1, TimeUnit.MINUTES);
    }
  }
}
