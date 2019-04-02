package com.aurora.internalservice.internalcache;

import android.content.Context;
import android.util.Log;

import com.aurora.auroralib.PluginObject;
import com.aurora.internalservice.InternalService;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
     * The extension that is used for cached files
     */
    private static final String CACHE_EXTENSION = ".aur";

    /**
     * A reference to the android application context (necessary for reading and writing files
     */
    private Context mContext;

    /**
     * Data structure that keeps track of cached files per plugin
     * The key is a unique plugin name. The value is a list of file references associated with that plugin.
     */
    private Map<String, List<String>> mCachedFiles;

    /**
     * Creates an instance of the internal cache
     *
     * @param applicationContext the android application context
     */
    public InternalCache(Context applicationContext) {
        mContext = applicationContext;

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
        /*
        This method consists of two parts: We have to actually write the representation to a file.
        We should do this by mapping a file ref to another fileref that represents the cached representation.
        In this file, we should have a json representation of the plugin object representation.

        Secondly, we have to update the plugin registry as to keep track of which files are cached and which are not.
         */

        // Get the path where to store the file
        String cachedPath = getCachedPath(fileRef);

        // Write the plugin object to the path
        if (writeCacheFile(cachedPath, pluginObject)) {
            // If writing was successful, add file to registry
            if (!mCachedFiles.containsKey(uniquePluginName)) {
                mCachedFiles.put(uniquePluginName, new ArrayList<>());
            }

            // Require non null not really necessary because of precondition above
            Objects.requireNonNull(mCachedFiles.get(uniquePluginName)).add(fileRef);

            return true;
        }

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
        // Check in the registry if the file is present under unique plugin name
        List<String> cachedFilesByPlugin = null;
        if (uniquePluginName != null
                && (cachedFilesByPlugin = mCachedFiles.get(uniquePluginName)) != null
                && cachedFilesByPlugin.contains(fileRef)) {
            // Return the file ref if it is present in the cache
            return fileRef;
        }

        // Return null if the parameters are invalid or if the file is not present
        return null;
    }

    /**
     * Gets a list of already processed file representations
     *
     * @param amount the amount of files that should be retrieved, if 0 or negative, all files will be retrieved.
     * @return a list of filenames of cached files TODO: may change to CachedFile representation class!
     */
    public List<String> getFullCache(int amount) {
        if (amount <= 0) {
            return getFullCache();
        }

        // Create an empty list where the results will be stored
        List<String> cachedFiles = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : mCachedFiles.entrySet()) {
            if (entry.getValue().size() < amount - cachedFiles.size()) {
                // Add entire list
                cachedFiles.addAll(entry.getValue());
            } else {
                // Add one by one until amount is reached
                for (String fileRef : entry.getValue()) {
                    if (cachedFiles.size() < amount) {
                        cachedFiles.add(fileRef);
                    }
                }
            }
        }

        return cachedFiles;
    }

    /**
     * Gets a list of already processed file representations
     *
     * @return a list of filenames of cached files TODO: may change to CachedFile representation class!
     */
    public List<String> getFullCache() {
        List<String> cachedFiles = new ArrayList<>();

        // Loop over all plugins and add all files to result list
        for (Map.Entry<String, List<String>> entry : mCachedFiles.entrySet()) {
            cachedFiles.addAll(entry.getValue());
        }

        return cachedFiles;
    }


    /**
     * Retrieves a processed file from the cache
     *
     * @param fileRef          a reference to the file to retrieve
     * @param uniquePluginName the name of the plugin that the file was processed with
     * @return the processed file if it was in the cache, null otherwise
     */
    public CachedProcessedFile retrieveFile(String fileRef, String uniquePluginName) {
        // First, look up in the registry if the file is present
        if (isInCache(fileRef, uniquePluginName)) {

            // file is in cache, retrieve it
            String cachedPath = getCachedPath(fileRef);

            // Create a reader to read the file
            try (FileInputStream fileInputStream = mContext.openFileInput(cachedPath);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream))) {
                // Read file in string, use string builder for speed
                StringBuilder stringBuilder = new StringBuilder();
                String currentLine;

                // Have to read line by line because stream api is not supported
                while ((currentLine = reader.readLine()) != null) {
                    stringBuilder.append(currentLine);
                }

                String cachedRepresentation = stringBuilder.toString();

                return new CachedProcessedFile(cachedRepresentation);
            } catch (FileNotFoundException e) {
                Log.e(CLASS_TAG, "The cached file was not found!", e);
            } catch (IOException e) {
                Log.e(CLASS_TAG, "Something went wrong while reading the cached file", e);
            }
        }

        // If file not in cache or exception occurred, return null
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
        // First check if the file is in the cache registry, if it is, remove it
        if (isInCache(fileRef, uniquePluginName) && mContext.deleteFile(getCachedPath(fileRef))) {
            // If file was successfully removed, remove the fileRef from the list
            Objects.requireNonNull(mCachedFiles.get(uniquePluginName)).remove(fileRef);

            // If list is now empty, remove entry from map
            if (Objects.requireNonNull(mCachedFiles.get(uniquePluginName)).isEmpty()) {
                mCachedFiles.remove(uniquePluginName);
            }

            return true;
        }

        // If file is not in cache, return false
        return false;
    }

    /**
     * Removes all files from the cache that were processed by a given plugin
     *
     * @param uniquePluginName the name of the plugin to remove the files from
     * @return true only if the entire operation was successful, false if plugin was not found
     * or if at least one file could not be removed
     */
    public boolean removeFilesByPlugin(String uniquePluginName) {
        // Retrieve copy of list of files cached for this plugin
        List<String> cachedFilesByPlugin = mCachedFiles.get(uniquePluginName);
        boolean successful = cachedFilesByPlugin != null;

        if (!successful) {
            // Early return if the plugin does not exist
            return false;
        }

        for (String fileRef : new ArrayList<>(cachedFilesByPlugin)) {
            // First call remove file to ensure that it is called even when 'successful' is already false
            successful = removeFile(fileRef, uniquePluginName) && successful;
        }

        // This will be true if all files were successfully removed, false in all other cases
        return successful;
    }

    /**
     * Clears the entire cache
     *
     * @return true if the entire operation was successful or if the cache was empty, false otherwise
     */
    public boolean clear() {
        boolean successful = true;

        // Call removeFilesByPlugin for each plugin in the registry
        // Iterate over copy to avoid concurrent modification exceptions
        for (String uniquePluginName : new HashSet<>(mCachedFiles.keySet())) {
            successful = removeFilesByPlugin(uniquePluginName) && successful;
        }

        return successful;
    }

    /**
     * This method will check if there exists a registry of the files that are cached.
     * If not, it will create a new one and write it to disk.
     */
    private void initCacheRegistry() {
        try (FileInputStream cacheRegistryFile = mContext.openFileInput(CACHE_LOCATION);
             BufferedReader reader = new BufferedReader(new InputStreamReader(cacheRegistryFile))) {
            Gson gson = new Gson();

            CacheRegistryElement[] cacheRegistryElements = gson.fromJson(reader, CacheRegistryElement[].class);
            mCachedFiles = convertToMap(cacheRegistryElements);

        } catch (FileNotFoundException e) {
            // If file not found, create a new file containing an empty map
            mCachedFiles = new HashMap<>();
            writeCacheRegistry();
        } catch (IOException e) {
            // If something goes wrong when reading the file, log the exception
            Log.e(CLASS_TAG, "Something went wrong while reading the cache registry file", e);
        }
    }

    /**
     * Writes the cache registry state back to a file. This should be called when the cache registry state is changed
     */
    private void writeCacheRegistry() {
        // Convert map to array of CacheRegistryElements
        CacheRegistryElement[] elements = convertFromMap(mCachedFiles);

        File cacheFile = new File(mContext.getFilesDir(), CACHE_LOCATION);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cacheFile))) {
            Gson gson = new Gson();

            // Convert array to json string
            String cachedElementsString = gson.toJson(elements, CacheRegistryElement[].class);

            // Write string to file
            writer.write(cachedElementsString);
        } catch (IOException e) {
            Log.e(CLASS_TAG, "Something went wrong while writing the cache registry", e);
        }
    }

    /**
     * Converts an array of CacheRegistryElements to a map
     *
     * @param elements the elements to be converted to a map
     * @return a map with Strings (unique plugin names) as key and a list of associated file references as a value
     */
    private static Map<String, List<String>> convertToMap(CacheRegistryElement[] elements) {
        Map<String, List<String>> cacheMap = new HashMap<>();
        for (CacheRegistryElement el : elements) {
            // Convert cached fileRefs to list
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
    private static CacheRegistryElement[] convertFromMap(Map<String, List<String>> cacheMap) {
        // Create list of cache registry elements
        List<CacheRegistryElement> cacheRegistryElements = new ArrayList<>();

        // Loop over all entries in the map to add them to the array
        for (Map.Entry<String, List<String>> el : cacheMap.entrySet()) {
            // Convert the list to an array
            String[] fileRefs = el.getValue().toArray(new String[]{});

            cacheRegistryElements.add(new CacheRegistryElement(el.getKey(), fileRefs));
        }

        // Convert list to array
        return cacheRegistryElements.toArray(new CacheRegistryElement[0]);
    }

    /**
     * Gets the path of the cached file given the fileRef
     *
     * @param fileRef a reference to a file
     * @return the path to where the cached representation corresponding to this file can be found
     */
    private static String getCachedPath(String fileRef) {
        // Get the file ref to it without extension
        String cachedPath;
        if (fileRef.contains(".")) {
            cachedPath = fileRef.substring(0, fileRef.indexOf('.'));
        } else {
            cachedPath = fileRef;
        }

        // Concatenate .aur extension
        return cachedPath + CACHE_EXTENSION;
    }

    /**
     * Writes a cache file for a given plugin object
     *
     * @param path         the location where to write the file
     * @param pluginObject the object to write to the cache
     */
    private boolean writeCacheFile(String path, PluginObject pluginObject) {
        File cacheFile = new File(mContext.getFilesDir(), path);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cacheFile))) {
            // Write json representation of plugin object to the file
            writer.write(pluginObject.toJSON());
        } catch (IOException e) {
            Log.e(CLASS_TAG, "Something went wrong while writing a cache file!", e);

            return false;
        }
        return true;
    }

    /**
     * Helper method that checks if a file is in the cache registry
     *
     * @param fileRef          the reference to the file to check if it is in the cache registry
     * @param uniquePluginName the name of the plugin that the file would be processed with
     * @return true if the file is in the cache, false otherwise
     */
    private boolean isInCache(String fileRef, String uniquePluginName) {
        List<String> cachedFilesByPlugin = mCachedFiles.get(uniquePluginName);

        return cachedFilesByPlugin != null && cachedFilesByPlugin.contains(fileRef);
    }

    /**
     * Inner helper class that helps with serializing the cache registry
     */
    private static class CacheRegistryElement {
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
