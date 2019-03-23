package com.aurora.internalservice.internalcache;

import android.util.Log;

import com.aurora.internalservice.InternalService;
import com.aurora.internalservice.InternallyProcessedFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that interacts with cached file representations
 */
public class InternalCache implements InternalService {
    private static final String CLASS_TAG = "InternalCache";

    /**
     * Checks if a file is in cache and if so return processed version
     *
     * @param fileRef a reference to where the file can be found
     * @return The cached and already processed file
     */
    @Override
    public InternallyProcessedFile processFile(InputStream file, String fileRef) {
        // TODO: implement this
        Log.d(CLASS_TAG, "Not implemented yet");
        return null;
    }

    public void checkCacheForProcessedFile() {
        Log.d(CLASS_TAG, "Not implemented yet!");
    }

    /**
     * Gets a list of already processed file representations
     *
     * @return a list of paths to cached files TODO: may change to CachedFile representation class!
     */
    public List<String> getFullCache() {
        Log.d(CLASS_TAG, "Not implemented yet!");
        return new ArrayList<>();
    }
}
