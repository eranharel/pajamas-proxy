package com.outbrain.pajamasproxy.memcached.server.protocol.exceptions;

/**
 */
public class MalformedCommandException extends ClientException {

  private static final long serialVersionUID = 1L;

  public MalformedCommandException(final String s) {
    super(s);
  }

  public MalformedCommandException(final String s, final Throwable throwable) {
    super(s, throwable);
  }

  public MalformedCommandException(final Throwable throwable) {
    super(throwable);
  }
}