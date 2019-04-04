package com.aurora.internalservice.internalcache;

import java.util.Date;
import java.util.Objects;

/**
 * Class that contains basic information of a cached file.
 */
public class CachedFileInfo {
    /**
     * The file reference to the original file
     */
    private String mFileRef;

    /**
     * The plugin that the file was opened with
     */
    private String mUniquePluginName;

    /**
     * The date that the file was last opened
     */
    private Date mLastOpened;

    public CachedFileInfo(String fileRef, String uniquePluginName, Date lastOpened) {
        mFileRef = fileRef;
        mUniquePluginName = uniquePluginName;
        mLastOpened = lastOpened;
    }

    public CachedFileInfo(String fileRef, String uniquePluginName) {
        this(fileRef, uniquePluginName, null);
    }

    public String getFileRef() {
        return mFileRef;
    }

    public String getUniquePluginName() {
        return mUniquePluginName;
    }

    public Date getLastOpened() {
        return mLastOpened;
    }

    public void setLastOpened(Date lastOpened) {
        mLastOpened = lastOpened;
    }

    /**
     * Overridden equals method will only keep track of fileref and plugin, not the date
     *
     * @param o the object to compare to this
     * @return true if the objects have equal filerefs and plugin name
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CachedFileInfo that = (CachedFileInfo) o;
        return Objects.equals(mFileRef, that.mFileRef) &&
                Objects.equals(mUniquePluginName, that.mUniquePluginName);
    }

    /**
     * Overridden hashcode method that only takes into account the fileref and the plugin name, not the date
     *
     * @return a hash based on the file reference and the plugin name
     */
    @Override
    public int hashCode() {
        return Objects.hash(mFileRef, mUniquePluginName);
    }
}
