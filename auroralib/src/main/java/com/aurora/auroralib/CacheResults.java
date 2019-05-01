package com.aurora.auroralib;

/**
 * Class of constants indicating results of the caching operation.
 * The {@link CacheServiceCaller} has a cache method that will try to cache a file in Aurora, and it will return
 * one of the constant return codes in this class.
 * @see CacheServiceCaller
 */
public final class CacheResults {
    /**
     * Indicates that the cache operation succeeded
     */
    public static final int CACHE_SUCCESS = 0;

    /**
     * Indicates that the cache operation failed
     */
    public static final int CACHE_FAIL = -1;

    /**
     * Indicates that the kernel could not be instantiated correctly
     */
    public static final int KERNEL_FAIL = -2;

    /**
     * Indicates that a RemoteException occurred
     */
    public static final int REMOTE_FAIL = -3;

    /**
     * Indicates that the cache service could not be reached
     */
    public static final int NOT_REACHED = -1000;

    /**
     * Empty private constructor so people cannot instantiate the class (it it just constants)
     */
    private CacheResults() {
    }
}
