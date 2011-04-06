package com.thimbleware.jmemcached.protocol;

import java.util.Map;
import java.util.Set;

import com.thimbleware.jmemcached.Cache;
import com.thimbleware.jmemcached.CacheElement;

/**
 * Represents the response to a command.
 */
public final class ResponseMessage {

  public ResponseMessage(final CommandMessage cmd) {
    this.cmd = cmd;
  }

  public CommandMessage cmd;
  public CacheElement[] elements;
  public Cache.StoreResponse response;
  public Map<String, Set<String>> stats;
  public String version;
  public Cache.DeleteResponse deleteResponse;
  public Integer incrDecrResponse;
  public boolean flushSuccess;

  public ResponseMessage withElements(final CacheElement[] elements) {
    this.elements = elements;
    return this;
  }

  public ResponseMessage withResponse(final Cache.StoreResponse response) {
    this.response = response;
    return this;
  }

  public ResponseMessage withDeleteResponse(final Cache.DeleteResponse deleteResponse) {
    this.deleteResponse = deleteResponse;
    return this;
  }

  public ResponseMessage withIncrDecrResponse(final Integer incrDecrResp) {
    this.incrDecrResponse = incrDecrResp;

    return this;
  }

  public ResponseMessage withStatResponse(final Map<String, Set<String>> stats) {
    this.stats = stats;

    return this;
  }

  public ResponseMessage withFlushResponse(final boolean success) {
    this.flushSuccess = success;

    return this;
  }
}
