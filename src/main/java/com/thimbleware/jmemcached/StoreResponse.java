package com.thimbleware.jmemcached;

/**
 * Enum defining response statuses from set/add type commands
 */
public enum StoreResponse {
  STORED, NOT_STORED, EXISTS, NOT_FOUND
}