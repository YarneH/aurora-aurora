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
     * @return the processed file name if it is present, null otherwise
     */
    public String checkCacheForProcessedFile(String fileRef, String uniquePluginName) {
        Log.d(CLASS_TAG, "Not implemented yet!");
        return null;
    }

    /**
     * Gets a list of already processed file representations
     *
     * @param amount the amount of files that should be retrieved, if 0 or negative, all files will be retrieved.
     * @return a list of filenames of cached files TODO: may change to CachedFile representation class!
     */
    public List<String> getFullCache(int amount) {
        Log.d(CLASS_TAG, "Not implemented yet! " + amount);
        return new ArrayList<>();
    }

    /**
     * Gets a list of already processed file representations
     *
     * @return a list of filenames of cached files TODO: may change to CachedFile representation class!
     */
    public List<String> getFullCache() {
        Log.d(CLASS_TAG, "Not implemented yet!");
        return getFullCache(0);
    }


    /**
     * Retrieves a processed file from the cache
     *
     * @param fileRef a reference to the file to retrieve
     * @param uniquePluginName the name of the plugin that the file was processed with
     * @return the processed file if it was in the cache, null otherwise
     */
    public CachedProcessedFile retrieveFile(String fileRef, String uniquePluginName) {
        Log.d(CLASS_TAG, "This method hasn't been implemented" + fileRef + " " + uniquePluginName);
        return null;
    }
}
