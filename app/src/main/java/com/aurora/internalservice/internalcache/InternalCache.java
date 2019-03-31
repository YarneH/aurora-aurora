package com.aurora.internalservice.internalcache;

import android.util.Log;

import com.aurora.auroralib.PluginObject;
import com.aurora.internalservice.InternalService;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that interacts with cached file representations
 */
public class InternalCache implements InternalService {
    private static final String CLASS_TAG = "InternalCache";

    /**
     * The location where the cached file registry will be stored.
     */
    private static final String CACHE_LOCATION = "cached-files.json";

    /**
     * Data structure that keeps track of cached files per plugin
     * The key is a unique plugin name. The value is a list of file references associated with that plugin.
     */
    private Map<String, List<String>> mCachedFiles;

    public InternalCache() {
        // Initialize cache registry
        initCacheRegistry();
    }

    /**
     * Adds a processed pluginObject to the cache
     *
     * @param fileRef          a reference to the file that was processed
     * @param pluginObject     the processed pluginObject to be cached
     * @param uniquePluginName the name of the plugin that built the processed pluginObject
     * @return true if the pluginObject was successfully processed
     */
    public boolean cacheFile(String fileRef, PluginObject pluginObject, String uniquePluginName) {
        // TODO: implement this
        /*
        This method consists of two parts: We have to actually write the representation to a file.
        We should do this by mapping a file ref to another fileref that represents the cached representation.
        In this file, we should have a json representation of the plugin object representation.

        Secondly, we have to update the plugin registry as to keep track of which files are cached and which are not.
         */

        Log.d(CLASS_TAG, "Not implemented yet");
        return false;
    }

    /**
     * Checks the cache if a processed version of the file is present and returns it if it is the case
     *
     * @param fileRef          a reference to the file to check the cache for
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
     * @param fileRef          a reference to the file to retrieve
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
     * @param fileRef          a reference to the file that should be removed from the cache
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

    /**
     * This method will check if there exists a registry of the files that are cached.
     * If not, it will create a new one and write it to disk.
     */
    private void initCacheRegistry() {
        File cacheFile = new File(CACHE_LOCATION);

        if (cacheFile.exists()) {
            // Read the file
            mCachedFiles = readCacheRegistry(cacheFile);
        } else {
            // If the file does not exist, create new empty map
            mCachedFiles = new HashMap<>();
        }
    }

    /**
     * This method will read the contents of the cache registry file
     *
     * @param cacheFile the file to read
     */
    private Map<String, List<String>> readCacheRegistry(File cacheFile) {
        CacheRegistryElement[] cacheRegistryElements = null;

        try(BufferedReader reader = new BufferedReader(new FileReader(cacheFile))) {
            Gson gson = new Gson();

             cacheRegistryElements = gson.fromJson(reader, CacheRegistryElement[].class);
        } catch (FileNotFoundException e) {
            Log.e(CLASS_TAG, "The file was not found!");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(CLASS_TAG, "Something went wrong reading the file!");
            e.printStackTrace();
        }

        // Convert the elements to the appropriate data structure and return or return empty map otherwise
        return (cacheRegistryElements != null) ? convertToMap(cacheRegistryElements) : new HashMap<>();
    }

    /**
     * Writes the cache registry state back to a file. This should be called when the cache registry state is changed
     */
    private void writeCacheRegistry() {
        // Convert map to array of CacheRegistryElements
        CacheRegistryElement[] elements = convertFromMap(mCachedFiles);

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(CACHE_LOCATION))) {
            Gson gson = new Gson();

            // Convert array to json string
            String cachedElementsString = gson.toJson(elements, CacheRegistryElement[].class);

            // Write string to file
            writer.write(cachedElementsString);
        } catch (IOException e) {
            Log.e(CLASS_TAG, "Something went wrong while writing the cache registry");
            e.printStackTrace();
        }
    }

    /**
     * Converts an array of CacheRegistryElements to a map
     *
     * @param elements the elements to be converted to a map
     * @return a map with Strings (unique plugin names) as key and a list of associated file references as a value
     */
    private Map<String, List<String>> convertToMap(CacheRegistryElement[] elements) {
        Map<String, List<String>> cacheMap = new HashMap<>();
        for (CacheRegistryElement el: elements) {
            // Convert cached filerefs to list
            List<String> fileRefs = Arrays.asList(el.cachedFileRefs);

            // Add element to map
            cacheMap.put(el.uniquePluginName, fileRefs);
        }

        return cacheMap;
    }

    /**
     * Converts a map with unique plugin names as key and a list of associated file references as value
     * to an array of CacheRegistryElements for easier serialization.
     *
     * @param cacheMap the map to convert
     * @return an array of easier to serialize elements
     */
    private CacheRegistryElement[] convertFromMap(Map<String, List<String>> cacheMap) {
        // Create list of cache registry elements
        List<CacheRegistryElement> cacheRegistryElements = new ArrayList<>();

        // Loop over all entries in the map to add them to the array
        for (Map.Entry<String, List<String>> el: cacheMap.entrySet()) {
            // Convert the list to an array
            String[] fileRefs = el.getValue().toArray(new String[]{});

            cacheRegistryElements.add(new CacheRegistryElement(el.getKey(), fileRefs));
        }

        // Convert list to array
        return cacheRegistryElements.toArray(new CacheRegistryElement[0]);
    }

    /**
     * Inner helper class that helps with serializing the cache registry
     */
    private class CacheRegistryElement {
        /**
         * The name of the plugin to which the cached files belong
         */
        private String uniquePluginName;

        /**
         * The filerefs of the cached files belonging to this plugin
         */
        private String[] cachedFileRefs;

        public CacheRegistryElement(String uniquePluginName, String[] cachedFileRefs) {
            this.uniquePluginName = uniquePluginName;
            this.cachedFileRefs = cachedFileRefs;
        }
    }
}
