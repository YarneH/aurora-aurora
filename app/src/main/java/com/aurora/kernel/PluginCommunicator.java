package com.aurora.kernel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.aurora.aurora.R;
import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.kernel.event.OpenCachedFileWithPluginRequest;
import com.aurora.kernel.event.OpenFileWithPluginRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import io.reactivex.Observable;

/**
 * Communicator that communicates with Plugin environments
 */
public class PluginCommunicator extends Communicator {
    /**
     * Tag for logging purposes
     */
    private static final String CLASS_TAG = "PluginCommunicator";

    /**
     * An observable keeping track of incoming OpenFileWithPluginRequests
     */
    private final Observable<OpenFileWithPluginRequest> mOpenFileWithPluginRequestObservable;

    /**
     * An observable keeping track of incoming OpenCachedFileWithPluginRequests
     */
    private final Observable<OpenCachedFileWithPluginRequest> mOpenCachedFileWithPluginRequestObservable;

    /**
     * String that is logged on a writing error
     */
    private static final String ERROR_LOG = "Writing to temporary files went wrong";

    /**
     * Prefix used for Extracted-text temporary files
     */
    private static final String PROCESSED_PREFIX = "processed-";

    /**
     * Prefix used for Cached PluginObject temporary files
     */
    private static final String CACHED_PREFIX = "cached-";

    /**
     * File extension used as suffix
     */
    private static final String EXTENSION = ".aur";

    /**
     * a Toast to keep track of the current toast
     */
    private Toast mToast = null;


    /**
     * Creates a PluginCommunicator. There should be only one instance at a time
     *
     * @param bus            a reference to the unique bus instances that all communicators should use to
     *                       communicate events
     */
    PluginCommunicator(@NonNull Bus bus) {
        super(bus);

        // Register for requests to open file with plugin
        mOpenFileWithPluginRequestObservable = mBus.register(OpenFileWithPluginRequest.class);

        // When a request comes in, call appropriate function
        mOpenFileWithPluginRequestObservable.subscribe((OpenFileWithPluginRequest request) ->
                openFileWithPlugin(request.getExtractedText(), request.getUniquePluginName(), request.getContext())
        );

        // Register for requests to open a cached file with plugin
        mOpenCachedFileWithPluginRequestObservable = mBus.register(OpenCachedFileWithPluginRequest.class);

        // When a request comes in, call appropriate function
        mOpenCachedFileWithPluginRequestObservable.subscribe((OpenCachedFileWithPluginRequest request) ->
                openCachedFileWithPlugin(request.getJsonRepresentation(),
                        request.getUniquePluginName(), request.getContext())
        );
    }


    /**
     * Opens a file with a given plugin
     *
     * @param extractedText    the extracted text of the file to open
     *                         TODO: add tests for this method
     * @param uniquePluginName the unique name of the plugin to open the file with
     * @param context          the android context
     */
    private void openFileWithPlugin(ExtractedText extractedText, String uniquePluginName, Context context) {
        // Create intent to open plugin
        Intent launchIntent = new Intent();
        launchIntent.setAction(Constants.PLUGIN_ACTION);
        launchIntent.setPackage(uniquePluginName);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Convert the extracted text to JSON
        String extractedTextInJSON = extractedText.toJSON();
        Log.v("JSON", extractedTextInJSON);

        // Start by clearing the old transfer files
        removeFilesThatStartWithFromDir(context.getCacheDir(), PROCESSED_PREFIX);

        Uri uri;

        try {
            uri = writeToTempFile(context, extractedTextInJSON, PROCESSED_PREFIX);
        } catch (IOException e) {
            showToastAndLogError(context, ERROR_LOG, e);
            return;
        }

        // Update the intent with some final flags, extras and data
        setDataAndFlags(launchIntent, uri, Constants.PLUGIN_INPUT_TYPE_EXTRACTED_TEXT);

        // Check if at least one app exists that can execute the task
        boolean pluginOpens = launchIntent.resolveActivity(context.getPackageManager()) != null;
        if (pluginOpens) {
            context.startActivity(launchIntent);
        } else {
            showToastAndLogError(context, context.getString(R.string.could_not_open_plugin), null);
        }
    }


    /**
     * Opens a cached file with a given plugin
     *
     * @param jsonRepresentation the json representation of the plugin object to represent
     * @param uniquePluginName   the unique name of the plugin
     * @param context            the android context
     */
    private void openCachedFileWithPlugin(@NonNull final String jsonRepresentation,
                                          @NonNull final String uniquePluginName, @NonNull final Context context) {
        // Create intent to open plugin
        Intent launchIntent = new Intent();
        launchIntent.setAction(Constants.PLUGIN_ACTION);
        launchIntent.setPackage(uniquePluginName);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        // Start by clearing the old transfer files
        removeFilesThatStartWithFromDir(context.getCacheDir(), CACHED_PREFIX);

        Uri uri;

        try {
            uri = writeToTempFile(context, jsonRepresentation, CACHED_PREFIX);
        } catch (IOException e) {
            showToastAndLogError(context, ERROR_LOG, e);
            return;
        }

        setDataAndFlags(launchIntent, uri, Constants.PLUGIN_INPUT_TYPE_OBJECT);

        boolean cachedFileOpens = launchIntent.resolveActivity(context.getPackageManager()) != null;
        if (cachedFileOpens) {
            context.startActivity(launchIntent);
        } else {
            showToastAndLogError(context, context.getString(R.string.could_not_open_plugin), null);
        }

    }


    /**
     * Private non recursive helper function to clear a directory from files that start with prefix
     *
     * @param dir    the directory that needs files to be removed from
     * @param prefix the files that will be deleted start with this prefix
     */
    @SuppressWarnings("squid:S4042")
    // This warning is suppressed because it's not android compliant.
    private static void removeFilesThatStartWithFromDir(File dir, String prefix) {
        if (dir != null && dir.isDirectory()) {
            File[] files = dir.listFiles();

            for (File file : files) {
                if (file.getName().startsWith(prefix)) {
                    boolean success = file.delete();
                    if (!success) {
                        Log.e(CLASS_TAG, "There was a problem removing old files from "
                                + dir.getName());
                        return;
                    }
                }
            }
        }
    }

    /**
     * Private helper method that shows a toast on the main tread
     *
     * @param context the application context
     * @param message the message that needs to be shown
     */
    private void showToast(Context context, String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        if (mToast != null) {
            mToast.cancel();
        }
        handler.post(() -> {
            mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            mToast.show();
        });
    }

    /**
     * Private helper method that shows a toast on the main tread and also logs the message
     *
     * @param context the application context
     * @param message the message that needs to be shown
     * @param e       the throwable error
     */
    private void showToastAndLogError(Context context, String message, @Nullable Throwable e) {
        showToast(context, message);
        if (e != null) {
            Log.e(CLASS_TAG, message, e);
        } else {
            Log.e(CLASS_TAG, message);
        }

    }

    /**
     * Private helper function that writes content to a file with a name that starts with a
     * prefix, ends with suffix, and a random number in between.
     *
     * @param context the application context
     * @param content the content that will be written
     * @param prefix  name of the file
     * @return Uri to the file on success
     * @throws IOException on failure
     */
    private Uri writeToTempFile(Context context, String content, String prefix) throws IOException {
        // Write the processed file to the cache directory
        File file = File.createTempFile(prefix, PluginCommunicator.EXTENSION, context.getCacheDir());

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(content);
        }

        return FileProvider.getUriForFile(context, "com.aurora.aurora.provider", file);
    }

    /**
     * Set all the data field and flags of the intent correctly for letting the receiver be able
     * to open the file.
     *
     * @param intent   The intent where the fields need to be set
     * @param uri      The Uri to the file
     * @param dataType The datatype of the file, should be a constant of {@link Constants}
     */
    private void setDataAndFlags(Intent intent, Uri uri, String dataType) {
        // Make the file readable by the plugin, this permission last until the activity of
        // the receiving app ends
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // Add the uri of the file as data
        intent.setData(uri);
        // Specify the datatype
        intent.putExtra(Constants.PLUGIN_INPUT_TYPE, dataType);
    }
}
