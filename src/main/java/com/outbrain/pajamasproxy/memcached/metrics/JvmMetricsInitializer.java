package com.outbrain.pajamasproxy.memcached.metrics;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;

import java.lang.management.ManagementFactory;
import java.util.Map;

/**
 * Time: 9/8/13 4:51 PM
 *
 * @author Eran Harel
 */
class JvmMetricsInitializer {

  public JvmMetricsInitializer(MetricRegistry metrics) {
    registerAll("gc", new GarbageCollectorMetricSet(), metrics);
    registerAll("buffer-pools", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()), metrics);
    registerAll("memory", new MemoryUsageGaugeSet(), metrics);
    registerAll("thread-states", new ThreadStatesGaugeSet(), metrics);
  }

  private void registerAll(String prefix, MetricSet metricSet, MetricRegistry registry) throws IllegalArgumentException {
    for (Map.Entry<String, Metric> entry : metricSet.getMetrics().entrySet()) {
      if (entry.getValue() instanceof MetricSet) {
        registerAll(name(prefix, entry.getKey()), (MetricSet) entry.getValue(), registry);
      } else {
        registry.register(name(prefix, entry.getKey()), entry.getValue());
      }
    }
  }

  private String name(String prefix, String key) {
    return prefix + "." + key;
  }

}
