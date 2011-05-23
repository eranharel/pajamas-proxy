package com.outbrain.pajamasproxy.memcached.server.protocol.exceptions;

/**
 */
public class UnknownCommandException extends ClientException {

  private static final long serialVersionUID = 1L;

  public UnknownCommandException(final String s) {
    super(s);
  }

  public UnknownCommandException(final String s, final Throwable throwable) {
    super(s, throwable);
  }

  public UnknownCommandException(final Throwable throwable) {
    super(throwable);
  }
}
