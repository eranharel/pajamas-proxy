package com.outbrain.pajamasproxy.memcached.server.protocol.exceptions;

/**
 */
public class ClientException extends Exception {

  private static final long serialVersionUID = 1L;

  public ClientException(final String s) {
    super(s);
  }

  public ClientException(final String s, final Throwable throwable) {
    super(s, throwable);
  }

  public ClientException(final Throwable throwable) {
    super(throwable);
  }
}
