package com.aurora.kernel.event;

import android.support.annotation.NonNull;

/**
 * Class used to update the date of an already cached file in the cache
 */
public class UpdateCachedFileDateRequest implements Event {

    /**
     * A reference to the originally processed file
     */
    private String fileRef;

    /**
     * The name of the plugin that the file was processed with
     */
    private String uniquePluginName;

    public UpdateCachedFileDateRequest(@NonNull final String fileRef, @NonNull final String uniquePluginName) {
        this.fileRef = fileRef;
        this.uniquePluginName = uniquePluginName;
    }

    public @NonNull String getFileRef() {
        return fileRef;
    }

    public @NonNull String getUniquePluginName() {
        return uniquePluginName;
    }
}
