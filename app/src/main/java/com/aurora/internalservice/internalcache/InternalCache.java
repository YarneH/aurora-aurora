package com.aurora.internalservice.internalcache;

import android.util.Log;

import com.aurora.internalservice.InternalService;
import com.aurora.plugin.ProcessedText;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that interacts with cached file representations
 */
public class InternalCache implements InternalService {
    private static final String CLASS_TAG = "InternalCache";

    /**
     * Adds a processed text to the cache
     *
     * @param fileRef a reference to the file that was processed
     * @param text the processed text to be cached
     * @param uniquePluginName the name of the plugin that built the processed text
     * @return true if the text was successfully processed
     */
    public boolean cacheFile(String fileRef, ProcessedText text, String uniquePluginName) {
        // TODO: implement this
        Log.d(CLASS_TAG, "Not implemented yet");
        return false;
    }

    /**
     * Checks the cache if a processed version of the file is present and returns it if it is the case
     *
     * @param fileRef a reference to the file to check the cache for
     * @param uniquePluginName the name of the plugin to open the representation with
     * @return the processed file if it is present, null otherwise
     */
    public CachedProcessedFile checkCacheForProcessedFile(String fileRef, String uniquePluginName) {
        Log.d(CLASS_TAG, "Not implemented yet!");
        return null;
    }

    /**
     * Gets a list of already processed file representations
     *
     * @return a list of paths to cached files TODO: may change to CachedFile representation class!
     */
    public List<CachedProcessedFile> getFullCache() {
        Log.d(CLASS_TAG, "Not implemented yet!");
        return new ArrayList<>();
    }
}
