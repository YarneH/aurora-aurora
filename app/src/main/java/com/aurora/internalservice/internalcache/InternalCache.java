package com.aurora.internalservice.internalcache;

import android.util.Log;

import com.aurora.internalservice.InternalService;

import java.util.List;

/**
 * Class that interacts with cached file representations
 */
public class InternalCache extends InternalService {

    /**
     * Checks if a file is in cache and if so return processed version
     *
     * @param fileRef a reference to where the file can be found
     */
    @Override
    public void process(String fileRef) {
    }

    public void checkCacheForProcessedFile() {
        Log.d("InternalCache", "Not implemented yet!");
    }

    /**
     * Gets a list of already processed file representations
     *
     * @return a list of paths to cached files TODO: may change to CachedFile representation class!
     */
    public List<String> getFullCache() {
        Log.d("InternalCache", "Not implemented yet!");
        return null;
    }
}