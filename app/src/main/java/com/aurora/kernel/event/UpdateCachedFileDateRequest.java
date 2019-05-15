package com.aurora.kernel.event;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Class used to update the date of an already cached file in the cache
 */
public class UpdateCachedFileDateRequest implements Event {

    /**
     * A reference to the originally processed file
     */
    private String mFileRef;

    /**
     * The name of the plugin that the file was processed with
     */
    private String mUniquePluginName;

    /**
     * The new date at which the cached file was opened
     */
    private Date mNewDate;

    public UpdateCachedFileDateRequest(@NonNull final String fileRef, @NonNull final String uniquePluginName,
                                       @NonNull final Date newDate) {
        mFileRef = fileRef;
        mUniquePluginName = uniquePluginName;
        mNewDate = newDate;
    }

    /**
     * @return a reference to the originally processed file
     */
    public @NonNull
    String getFileRef() {
        return mFileRef;
    }

    /**
     * @return the name of the plugin that the file was processed
     */
    public @NonNull
    String getUniquePluginName() {
        return mUniquePluginName;
    }

    /**
     * @return the new date at which the cached file was opened
     */
    public @NonNull
    Date getNewDate() {
        return mNewDate;
    }
}
