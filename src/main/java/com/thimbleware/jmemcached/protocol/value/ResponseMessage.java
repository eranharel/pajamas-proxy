package com.thimbleware.jmemcached.protocol.value;

import java.util.Map;
import java.util.Set;

import com.thimbleware.jmemcached.CacheElement;
import com.thimbleware.jmemcached.DeleteResponse;
import com.thimbleware.jmemcached.StoreResponse;

/**
 * Represents the response to a command.
 */
public final class ResponseMessage {

  public ResponseMessage(final CommandMessage cmd) {
    this.cmd = cmd;
  }

  public CommandMessage cmd;
  public CacheElement[] elements;
  public StoreResponse response;
  public Map<String, Set<String>> stats;
  public String version;
  public DeleteResponse deleteResponse;
  public Integer incrDecrResponse;
  public boolean flushSuccess;

  public ResponseMessage withElements(final CacheElement[] elements) {
    this.elements = elements;
    return this;
  }

  public ResponseMessage withResponse(final StoreResponse response) {
    this.response = response;
    return this;
  }

  public ResponseMessage withDeleteResponse(final DeleteResponse deleteResponse) {
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
