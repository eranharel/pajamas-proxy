package com.outbrain.pajamasproxy.memcached;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.thimbleware.jmemcached.MemCacheDaemon;

public class PajamasProxyDaemon {

  //  private static final Logger log = LoggerFactory.getLogger(PajamasProxyDaemon.class);

  public static void main(final String[] args) {
    final MemCacheDaemon<?> daemon = new ClassPathXmlApplicationContext("ApplicationContext.xml").getBean(MemCacheDaemon.class);
    daemon.start();

    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        if (daemon.isRunning()) {
          daemon.stop();
        }
      }
    }));
  }
}
