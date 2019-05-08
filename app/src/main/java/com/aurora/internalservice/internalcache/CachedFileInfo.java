package com.aurora.internalservice.internalcache;

import java.util.Date;
import java.util.Objects;

/**
 * Class that contains basic information of a cached file.
 */
public class CachedFileInfo implements Cloneable {
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

    /**
     * Creates a new CachedFileInfo instance
     *
     * @param fileRef          a unique file reference to the original file, consisting of hash_fileDisplayName
     * @param uniquePluginName the name of plugin that the file was processed with
     * @param lastOpened       the date that the file was last opened.
     */
    public CachedFileInfo(String fileRef, String uniquePluginName, Date lastOpened) {
        mFileRef = fileRef;
        mUniquePluginName = uniquePluginName;
        mLastOpened = lastOpened;
    }

    /**
     * Creates a new CachedFileInfo instance with the last opened date set to now
     *
     * @param fileRef          the file reference to the original file consisting of hash_fileDisplayName
     * @param uniquePluginName the name of plugin that the file was processed with
     * @see #CachedFileInfo(String, String, Date)
     */
    public CachedFileInfo(String fileRef, String uniquePluginName) {
        this(fileRef, uniquePluginName, null);
    }

    /**
     * @return the file reference of the original file
     */
    public String getFileRef() {
        return mFileRef;
    }

    /**
     * @return the name of the file that should be used in UI
     */
    public String getFileDisplayName() {
        int firstIndex = mFileRef.indexOf('_') + 1;

        return mFileRef.substring(firstIndex);
    }

    /**
     * @return the name of the plugin that the file was processed with
     */
    public String getUniquePluginName() {
        return mUniquePluginName;
    }

    /**
     * @return The moment at which the file was last opened
     */
    public Date getLastOpened() {
        return mLastOpened;
    }

    /**
     * @param lastOpened the moment at which the file was last opened
     */
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

    /**
     * @inheritDoc
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new CachedFileInfo(mFileRef, mUniquePluginName, mLastOpened);
    }
}
