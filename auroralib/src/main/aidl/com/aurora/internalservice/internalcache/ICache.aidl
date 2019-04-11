// ICache.aidl
package com.aurora.internalservice.internalcache;

// Declare any non-default types here with import statements

interface ICache {
    /**
     * Caches a pluginObject in JSON format
     * returns a statuscode
     */
    int cache(String pluginObject);
}
