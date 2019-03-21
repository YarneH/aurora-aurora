package com.aurora.internalservice.internalcache;

import android.util.Log;

import com.aurora.auroralib.PluginObject;
import com.aurora.internalservice.InternalService;

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
    public boolean cacheFile(String fileRef, PluginObject text, String uniquePluginName) {
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

    /**
     * Removes a file from the cache given its path and plugin name
     *
     * @param fileRef a reference to the file that should be removed from the cache
     * @param uniquePluginName the name of the plugin to remove the file from
     *                         It could be that a file was processed by different plugins (or different versions)
     *                         so it should be possible to only remove those for no longer supported versions.
     * @return true if the file was successfully removed
     */
    public boolean removeFile(String fileRef, String uniquePluginName) {
        Log.d(CLASS_TAG, "This method will be implemented later! " + fileRef + " " + uniquePluginName);
        return false;
    }

    /**
     * Removes all files from the cache that were processed by a given plugin
     *
     * @param uniquePluginName the name of the plugin to remove the files from
     * @return true if the operation was successful
     */
    public boolean removeFilesByPlugin(String uniquePluginName) {
        Log.d(CLASS_TAG, "This is not implemented yet! " + uniquePluginName);
        return false;
    }

    /**
     * Clears the entire cache
     *
     * @return true if the operation was successful
     */
    public boolean clear() {
        Log.d(CLASS_TAG, "Operation not implemented yet!");
        return false;
    }
}
