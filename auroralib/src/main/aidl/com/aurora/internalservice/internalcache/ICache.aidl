// ICache.aidl
package com.aurora.internalservice.internalcache;

/**
 * AIDL interface for the cache operation
 */
interface ICache {
    /**
     * Caches a pluginObject in JSON format
     * returns a statuscode
     */
    int cache(String fileName, String uniquePluginName, in Uri uri);

    Uri getWritePermissionUri(String packageName);
}
