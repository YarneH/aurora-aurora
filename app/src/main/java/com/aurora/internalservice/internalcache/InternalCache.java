package com.aurora.internalservice.internalcache;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

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
import java.util.Collections;
import java.util.Date;
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
     * The key is a unique plugin name. The value is a list of file references associated with
     * that plugin.
     */
    private Map<String, List<CachedFileInfo>> mCachedFiles;

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
     * This method will check if there exists a registry of the files that are cached.
     * If not, it will create a new one and write it to disk.
     */
    private void initCacheRegistry() {
        try (FileInputStream cacheRegistryFile = mContext.openFileInput(CACHE_LOCATION);
             BufferedReader reader = new BufferedReader(new InputStreamReader(cacheRegistryFile))) {
            Gson gson = new Gson();

            CacheRegistryElement[] cacheRegistryElements = gson.fromJson(reader,
                    CacheRegistryElement[].class);
            mCachedFiles = convertToMap(cacheRegistryElements);

            // Check if files in the map are still in the cache and remove the entries if necessary
            updateCacheStatus();

        } catch (FileNotFoundException e) {
            // If file not found, create a new file containing an empty map
            mCachedFiles = new HashMap<>();
            writeCacheRegistry();
        } catch (IOException e) {
            Log.e(CLASS_TAG, "Something went wrong while reading the cache registry file", e);
        }
    }

    /**
     * Helper method that will check for every entry in the cache registry if the file still exists in the cache.
     * If a file is not cached anymore, remove it from the registry as well.
     */
    private void updateCacheStatus() {
        // Make copy so we don't modify a data structure we are iterating over
        Map<String, List<CachedFileInfo>> cachedFilesCopy = new HashMap<>(mCachedFiles);

        for (Map.Entry<String, List<CachedFileInfo>> entry : cachedFilesCopy.entrySet()) {
            // Create copy so we don't modify a data structure we are iterating over
            List<CachedFileInfo> infoListCopy = new ArrayList<>(entry.getValue());

            for (CachedFileInfo info : infoListCopy) {
                // Get cached path for the file
                String cachedPath = getCachedPath(info.getFileRef(), info.getUniquePluginName());

                // Check if the file exists
                File cachedFile = new File(mContext.getFilesDir(), cachedPath);

                if (!cachedFile.exists() && mCachedFiles.get(entry.getKey()) != null) {
                    // Remove entry from list
                    Objects.requireNonNull(mCachedFiles.get(entry.getKey())).remove(info);
                }

                // If the entry for this plugin is empty, remove it from the list
                if (mCachedFiles.get(entry.getKey()) != null &&
                        Objects.requireNonNull(mCachedFiles.get(entry.getKey())).isEmpty()) {
                    mCachedFiles.remove(entry.getKey());
                }
            }
        }

        // Persist
        writeCacheRegistry();
    }

    /**
     * Converts an array of CacheRegistryElements to a map
     *
     * @param elements the elements to be converted to a map
     * @return a map with Strings (unique plugin names) as key and a list of associated file
     * references as a value
     */
    private static Map<String, List<CachedFileInfo>> convertToMap(CacheRegistryElement[] elements) {
        Map<String, List<CachedFileInfo>> cacheMap = new HashMap<>();
        for (CacheRegistryElement element : elements) {
            // Convert cached fileRefs to list
            // Needs to be wrapped in new list because else it is not mutable
            List<CachedFileInfo> fileRefs = new ArrayList<>(Arrays.asList(element.cachedFileRefs));

            // Add element to map
            cacheMap.put(element.uniquePluginName, fileRefs);
        }

        return cacheMap;
    }

    /**
     * Writes the cache registry state back to a file. This should be called when the cache
     * registry state is changed
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
     * Converts a map with unique plugin names as key and a list of associated file references as
     * value to an array of CacheRegistryElements for easier serialization.
     *
     * @param cacheMap the map to convert
     * @return an array of easier to serialize elements
     */
    private static CacheRegistryElement[] convertFromMap(Map<String, List<CachedFileInfo>> cacheMap) {
        // Create list of cache registry elements
        List<CacheRegistryElement> cacheRegistryElements = new ArrayList<>();

        // Loop over all entries in the map to add them to the array
        for (Map.Entry<String, List<CachedFileInfo>> el : cacheMap.entrySet()) {
            // Convert the list to an array
            CachedFileInfo[] fileInfos = el.getValue().toArray(new CachedFileInfo[]{});

            cacheRegistryElements.add(new CacheRegistryElement(el.getKey(), fileInfos));
        }

        // Convert list to array
        return cacheRegistryElements.toArray(new CacheRegistryElement[0]);
    }

    /**
     * Adds a processed pluginObject to the cache
     *
     * @param fileRef          a reference to the file that was processed
     * @param pluginObject     the processed pluginObject to be cached
     * @param uniquePluginName the name of the plugin that built the processed pluginObject
     * @return true if the pluginObject was successfully processed
     */
    public boolean cacheFile(String fileRef, String pluginObject, String uniquePluginName) {
        /*
        This method consists of two parts: We have to actually write the representation to a file.
        We should do this by mapping a file ref to another file-ref that represents the cached
        representation.
        In this file, we should have a json representation of the plugin object representation.

        Secondly, we have to update the plugin registry as to keep track of which files are
        cached and which are not.
         */

        // Get the path where to store the file
        String cachedPath = getCachedPath(fileRef, uniquePluginName);

        // Write the plugin object to the path
        if (writeCacheFile(cachedPath, pluginObject)) {
            // If writing was successful, add file to registry
            if (!mCachedFiles.containsKey(uniquePluginName)) {
                mCachedFiles.put(uniquePluginName, new ArrayList<>());
            }

            // Create cachedFileInfo object with current date as date object
            CachedFileInfo cachedFileInfo = new CachedFileInfo(fileRef, uniquePluginName,
                    new Date());

            // Require non null not really necessary because of precondition above
            List<CachedFileInfo> pluginEntry = mCachedFiles.get(uniquePluginName);

            if (pluginEntry != null) {
                // First check if the file has been cached before
                int cachedFileInfoIndex = pluginEntry.indexOf(cachedFileInfo);

                if (cachedFileInfoIndex >= 0) {
                    pluginEntry.set(cachedFileInfoIndex, cachedFileInfo);
                } else {
                    pluginEntry.add(cachedFileInfo);
                }

                // Persist
                writeCacheRegistry();

                return true;
            }
        }

        return false;
    }

    /**
     * Updates the data of an already cached file in the registry to now, if it is present. Does nothing otherwise
     *
     * @param fileRef          a reference to the original file that was processed
     * @param uniquePluginName the unique name of the plugin that the file was processed with
     */
    public void updateCachedFileDate(@NonNull final String fileRef, @NonNull final String uniquePluginName,
                                     @NonNull final Date newDate) {
        // Check if the file is in the cache
        CachedFileInfo fileInfo = checkCacheForProcessedFile(fileRef, uniquePluginName);

        // Update date if the file is in the cache and if the new date is more recent than the old date
        if (fileInfo != null && newDate.after(fileInfo.getLastOpened())) {
            fileInfo.setLastOpened(newDate);


            // Write back to cache registry
            // requireNonNull just to stop warning, should never be null in reality
            int fileIndex = Objects.requireNonNull(mCachedFiles.get(uniquePluginName)).indexOf(fileInfo);
            if (fileIndex >= 0) {
                // This should always be the case since the checkCacheForProcessedFile returned the file
                // Update the entry
                Objects.requireNonNull(mCachedFiles.get(uniquePluginName)).set(fileIndex, fileInfo);
            }
        }

    }

    /**
     * Gets the path of the cached file given the fileRef
     *
     * @param fileRef          a reference to a file
     * @param uniquePluginName the name of the plugin that the file is (about to be) cached with
     * @return the path to where the cached representation corresponding to this file can be found
     */
    private static String getCachedPath(String fileRef, String uniquePluginName) {
        // Get the file ref to it without extension
        String cachedPath;

        if (fileRef.contains(".")) {
            cachedPath = fileRef.substring(0, fileRef.indexOf('.'));
        } else {
            cachedPath = fileRef;
        }

        // Append the last part of the pluginName
        String partPluginName = uniquePluginName;
        if (uniquePluginName.lastIndexOf('.') >= 0 &&
                uniquePluginName.lastIndexOf('.') < uniquePluginName.length()) {
            partPluginName = uniquePluginName.substring(uniquePluginName.lastIndexOf('.') + 1);
        }

        cachedPath += "_" + partPluginName;

        // Concatenate .aur extension
        return cachedPath + CACHE_EXTENSION;
    }

    /**
     * Writes a cache file for a given plugin object
     *
     * @param path             the location where to write the file
     * @param pluginObjectJson the object to write to the cache
     */
    private boolean writeCacheFile(String path, String pluginObjectJson) {
        File cacheFile = new File(mContext.getFilesDir(), path);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cacheFile))) {
            // Write json representation of plugin object to the file

            writer.write(pluginObjectJson);
        } catch (IOException e) {
            Log.e(CLASS_TAG, "Something went wrong while writing a cache file!", e);
            return false;
        }
        return true;
    }

    /**
     * Checks the cache if a processed version of the file is present and returns it if it is the
     * case
     *
     * @param fileRef          a reference to the file to check the cache for (should be
     *                         hash_displayName)
     *                         Check the getFileName method from {@link com.aurora.aurora.MainActivity}.
     * @param uniquePluginName the name of the plugin to open the representation with
     * @return the processed file info if it is present, null otherwise
     */
    public CachedFileInfo checkCacheForProcessedFile(@NonNull String fileRef,
                                                     @NonNull String uniquePluginName) {

        // Create cached file info object
        CachedFileInfo lookupFile = new CachedFileInfo(fileRef, uniquePluginName);

        // Check in the registry if the file is present under unique plugin name
        List<CachedFileInfo> cachedFilesByPlugin;
        if ((cachedFilesByPlugin = mCachedFiles.get(uniquePluginName)) != null
                && cachedFilesByPlugin.contains(lookupFile)) {
            // Return the file info if it is present in the cache (with up to date 'date' field)
            return new CachedFileInfo(cachedFilesByPlugin.get(cachedFilesByPlugin.indexOf(lookupFile)));
        }

        // Return null if the parameters are invalid or if the file is not present
        return null;
    }

    /**
     * Gets a list of already processed file representations
     *
     * @return a list of of metadata about cached files
     */
    public List<CachedFileInfo> getFullCache() {
        return getFullCache(0);
    }

    /**
     * Gets a list of already processed file representations ordered on most recent date
     *
     * @param amount the amount of files that should be retrieved, if 0 or negative, all files
     *               will be retrieved.
     * @return a list of metadata of cached files, ordered on the date most recently opened.
     */
    public List<CachedFileInfo> getFullCache(int amount) {
        // Create an empty list where the results will be stored
        List<CachedFileInfo> cachedFiles = new ArrayList<>();

        // Add all values to the list
        for (Map.Entry<String, List<CachedFileInfo>> entry : mCachedFiles.entrySet()) {
            List<CachedFileInfo> infoList = entry.getValue();

            // Add clone of info
            for (CachedFileInfo info : infoList) {
                cachedFiles.add(new CachedFileInfo(info));
            }
        }

        // Sort list on date with most recent (which is the largest) value first
        Collections.sort(cachedFiles, (a, b) -> b.getLastOpened().compareTo(a.getLastOpened()));

        if (amount <= 0) {
            amount = cachedFiles.size();
        }

        // Return no more than given amount or full list
        int index = Math.min(amount, cachedFiles.size());
        return cachedFiles.subList(0, index);
    }

    /**
     * Retrieves a processed file from the cache
     *
     * @param fileRef          a reference to the file to retrieve (should be hash_displayName)
     *                         Check the getFileName method from MainActivity.
     * @param uniquePluginName the name of the plugin that the file was processed with
     * @return the processed file if it was in the cache, null otherwise
     */
    public CachedProcessedFile retrieveFile(String fileRef, String uniquePluginName) {
        // First, look up in the registry if the file is present
        if (isInCache(fileRef, uniquePluginName)) {

            // file is in cache, retrieve it
            String cachedPath = getCachedPath(fileRef, uniquePluginName);

            // Create a reader to read the file
            try (FileInputStream fileInputStream = mContext.openFileInput(cachedPath);
                 BufferedReader reader =
                         new BufferedReader(new InputStreamReader(fileInputStream))) {
                // Read file in string, use string builder for speed
                StringBuilder stringBuilder = new StringBuilder();
                String currentLine;

                // Have to read line by line because stream api is not supported
                while ((currentLine = reader.readLine()) != null) {
                    stringBuilder.append(currentLine);
                }

                String cachedRepresentation = stringBuilder.toString();

                return new CachedProcessedFile(cachedRepresentation, fileRef, uniquePluginName);
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
     * Helper method that checks if a file is in the cache registry
     *
     * @param fileRef          the reference to the file to check if it is in the cache registry
     *                         (should be hash_displayName). Check the getFileName method from
     *                         MainActivity.
     * @param uniquePluginName the name of the plugin that the file would be processed with
     * @return true if the file is in the cache, false otherwise
     */
    private boolean isInCache(String fileRef, String uniquePluginName) {
        // Create cachedFileInfo object
        CachedFileInfo cachedFileInfo = new CachedFileInfo(fileRef, uniquePluginName);

        List<CachedFileInfo> cachedFilesByPlugin = mCachedFiles.get(uniquePluginName);

        return cachedFilesByPlugin != null && cachedFilesByPlugin.contains(cachedFileInfo);
    }

    /**
     * Clears the entire cache
     *
     * @return true if the entire operation was successful or if the cache was empty, false
     * otherwise
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
     * Removes all files from the cache that were processed by a given plugin
     *
     * @param uniquePluginName the name of the plugin to remove the files from
     * @return true only if the entire operation was successful, false if plugin was not found
     * or if at least one file could not be removed
     */
    public boolean removeFilesByPlugin(String uniquePluginName) {
        // Retrieve copy of list of files cached for this plugin
        List<CachedFileInfo> cachedFilesByPlugin = mCachedFiles.get(uniquePluginName);
        boolean successful = cachedFilesByPlugin != null;

        if (!successful) {
            // Early return if the plugin does not exist
            return false;
        }

        for (CachedFileInfo cachedFileInfo : new ArrayList<>(cachedFilesByPlugin)) {
            // First call remove file to ensure that it is called even when 'successful' is
            // already false
            successful = removeFile(cachedFileInfo.getFileRef(), uniquePluginName) && successful;
        }

        // This will be true if all files were successfully removed, false in all other cases
        return successful;
    }


    /**
     * Removes a file from the cache given its path and plugin name
     *
     * @param fileRef          a reference to the file that should be removed from the cache
     *                         (should be hash_displayName). Check the getFileName method from
     *                         MainActivity.
     * @param uniquePluginName the name of the plugin to remove the file from
     *                         It could be that a file was processed by different plugins (or
     *                         different versions)
     *                         so it should be possible to only remove those for no longer
     *                         supported versions.
     * @return true if the file was successfully removed
     */
    public boolean removeFile(String fileRef, String uniquePluginName) {
        // Create CachedFileInfo object
        CachedFileInfo cachedFileInfo = new CachedFileInfo(fileRef, uniquePluginName);

        // First check if the file is in the cache registry, if it is, remove it
        if (isInCache(fileRef, uniquePluginName) && mContext.deleteFile(getCachedPath(fileRef, uniquePluginName))) {
            // If file was successfully removed, remove the fileRef from the list
            Objects.requireNonNull(mCachedFiles.get(uniquePluginName)).remove(cachedFileInfo);

            // If list is now empty, remove entry from map
            if (Objects.requireNonNull(mCachedFiles.get(uniquePluginName)).isEmpty()) {
                mCachedFiles.remove(uniquePluginName);

                writeCacheRegistry();
            }

            return true;
        }

        // If file is not in cache, return false
        return false;
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
         * The file-refs of the cached files belonging to this plugin
         */
        private CachedFileInfo[] cachedFileRefs;

        /**
         * Creates a new CacheRegistryElement
         *
         * @param uniquePluginName the name of the plugin that processed the cached files
         * @param cachedFileRefs   an array of CachedFileInfo objects
         */
        CacheRegistryElement(String uniquePluginName, CachedFileInfo[] cachedFileRefs) {
            this.uniquePluginName = uniquePluginName;
            this.cachedFileRefs = cachedFileRefs;
        }
    }
}
