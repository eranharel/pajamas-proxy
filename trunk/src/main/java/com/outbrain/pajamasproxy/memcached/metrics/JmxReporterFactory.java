package com.outbrain.pajamasproxy.memcached.metrics;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;

/**
 * Time: 9/7/13 3:48 PM
 *
 * @author Eran Harel
 */
class JmxReporterFactory {

  private final JmxReporter reporter;

  public JmxReporterFactory(MetricRegistry registry) {
    reporter = JmxReporter.forRegistry(registry).build();
  }

  public void start() {
    reporter.start();
  }
}
