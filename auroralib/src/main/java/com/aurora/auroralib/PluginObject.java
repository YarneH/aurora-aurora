package com.aurora.auroralib;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 * Class to represent text processed by the plugin
 */
public abstract class PluginObject implements Serializable {

    /**
     * Gson object for turning PluginObject to Json string
     */
    @SuppressWarnings("WeakerAccess")
    protected static Gson sGson = new Gson();
    /**
     * The name of the file that contained the text that is now displayed in the PluginObject,
     * this also contains the prepended hash
     */
    @SuppressWarnings("WeakerAccess")
    protected String mFileName;
    /**
     * The name of the plugin that the file was processed with
     */
    protected String mUniquePluginName;

    private PluginObject(@NonNull final String fileName, @NonNull final String uniquePluginName) {
        mFileName = fileName;
        mUniquePluginName = uniquePluginName;
    }

    public PluginObject(@NonNull final String fileName) {
        mFileName = fileName;
    }

    /**
     * Turn the JSON string back into a PluginObject object.
     *
     * @param json The extracted JSON string of the PluginObject object
     * @return PluginObject
     */
    public static final <T extends PluginObject> T fromJson(String json, Class<T> type) {
        return sGson.fromJson(json, type);
    }

    /**
     * Method to convert the file accessed by the Uri to an PluginObject object
     *
     * @param fileUri The Uri to the temp file
     * @param context The context
     * @return ExtractedText object
     * @throws IOException          On IO trouble
     * @throws NullPointerException When the file cannot be found.
     */
    @SuppressWarnings("unused")
    public static <T extends PluginObject> T getPluginObjectFromFile(@NonNull Uri fileUri,
                                                                     @NonNull Context context,
                                                                     @NonNull Class<T> type)
            throws IOException {

        // Open the file
        ParcelFileDescriptor inputPFD = context.getContentResolver().openFileDescriptor(fileUri,
                "r");

        if (inputPFD == null) {
            throw new IllegalArgumentException("The file could not be opened");
        }

        // Read the file
        StringBuilder total = new StringBuilder();
        InputStream fileStream = new FileInputStream(inputPFD.getFileDescriptor());
        try (BufferedReader r = new BufferedReader(new InputStreamReader(fileStream))) {
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }
        }

        // Convert the read file to an ExtractedText object
        return fromJson(total.toString(), type);
    }

    /**
     * @return NonNull filename
     */
    @NonNull
    public final String getFileName() {
        if (mFileName == null) {
            return "";
        }
        return mFileName;
    }

    /**
     * @return NonNull name of the plugin
     */
    @NonNull
    public String getUniquePluginName() {
        if (mUniquePluginName == null) {
            return "";
        }
        return mUniquePluginName;
    }

    /**
     * Sets the name of the plugin
     *
     * @param uniquePluginName name of the plugin
     */
     void setUniquePluginName(@NonNull final String uniquePluginName) {
        mUniquePluginName = uniquePluginName;
    }

    /**
     * Turns the PluginObject to a JSON string for easy caching.
     *
     * @return String (in JSON format)
     */
    @NonNull
    public String toJSON() {
        return sGson.toJson(this);
    }

}
