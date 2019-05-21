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
 * Abstract class from which an extended class should be the main dataclass of a plugin. An
 * extended PluginObject is automatically cached by the
 * {@link ProcessorCommunicator#pipeline(ExtractedText)}. Make sure that all the fields are
 * serializable or transient and that a plugin environment (the frontend) has all the information
 * needed to properly display it to end users. A PluginObject retrieved from the Aurora cache
 * should still have everything necessary to construct the views without additional processing.
 *
 * <p>
 * Currently {@link com.google.gson.TypeAdapter} for the following not Serializable objects
 * exist: {@link BitmapListAdapter} and {@link CoreNLPDocumentAdapter}. These can be used by
 * placing an &#64;JsonAdapter({@link com.google.gson.TypeAdapter}.class) in front of the field.
 * </p>
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

    
    /**
     * PluginObject constructor
     *
     * @param fileName Name of the processed file
     */
    public PluginObject(@NonNull final String fileName) {
        mFileName = fileName;
    }

    /**
     * Turn the JSON string back into a PluginObject object.
     *
     * @param json  The extracted JSON string of the PluginObject object
     * @param type  Class to convert the Json to
     * @return PluginObject
     */
    public static final <T extends PluginObject> T fromJson(String json, Class<T> type) {
        return sGson.fromJson(json, type);
    }

    /**
     * Method to convert the file accessed by the Uri to a PluginObject object
     *
     * @param fileUri   The Uri to the temp file
     * @param context   The context
     * @param type      Class of the Object stored in the file on Uri
     * @return PluginObject object
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

        // Convert the read file to a PluginObject object
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
