package com.aurora.kernel;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.aurora.aurora.R;
import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.kernel.event.ListPluginsRequest;
import com.aurora.kernel.event.ListPluginsResponse;
import com.aurora.kernel.event.OpenCachedFileWithPluginRequest;
import com.aurora.kernel.event.OpenFileWithPluginChooserRequest;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
     * A reference to the plugin registry
     */
    private PluginRegistry mPluginRegistry;

    /**
     * An observable keeping track of incoming OpenFileWithPluginRequests
     */
    private final Observable<OpenFileWithPluginRequest> mOpenFileWithPluginRequestObservable;

    //TODO delete when custom picker is finished
    /**
     * An observable keeping track of incoming OpenFileWithPluginChooserRequests
     */
    private final Observable<OpenFileWithPluginChooserRequest> mOpenFileWithPluginChooserRequestObservable;

    /**
     * An observable keeping track of incoming OpenCachedFileWithPluginRequests
     */
    private final Observable<OpenCachedFileWithPluginRequest> mOpenCachedFileWithPluginRequestObservable;

    /**
     * An observable keeping track of incoming ListPluginsRequests
     */
    private final Observable<ListPluginsRequest> mListPluginsRequestObservable;

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
     * Creates a PluginCommunicator. There should be only one instance at a time
     *
     * @param bus            a reference to the unique bus instances that all communicators should use to
     *                       communicate events
     * @param pluginRegistry a reference to the plugin registry
     */
    public PluginCommunicator(@NonNull Bus bus, @NonNull PluginRegistry pluginRegistry) {
        super(bus);

        this.mPluginRegistry = pluginRegistry;

        // Register for requests to open file with plugin
        mOpenFileWithPluginRequestObservable = mBus.register(OpenFileWithPluginRequest.class);

        // When a request comes in, call appropriate function
        mOpenFileWithPluginRequestObservable.subscribe((OpenFileWithPluginRequest request) ->
                openFileWithPlugin(request.getExtractedText(), request.getUniquePluginName(), request.getContext())
        );

        // TODO: delete following two statements if custom plugin chooser is finished
        // Register for requests to open file with plugin chooser
        mOpenFileWithPluginChooserRequestObservable = mBus.register(OpenFileWithPluginChooserRequest.class);

        // When a request comes in, call appropriate function
        mOpenFileWithPluginChooserRequestObservable.subscribe((OpenFileWithPluginChooserRequest request) ->
                openFileWithPluginChooser(request.getExtractedText(), request.getPluginAction(),
                        request.getChooser(), request.getContext())
        );

        // Register for requests to open a cached file with plugin
        mOpenCachedFileWithPluginRequestObservable = mBus.register(OpenCachedFileWithPluginRequest.class);

        // When a request comes in, call appropriate function
        mOpenCachedFileWithPluginRequestObservable.subscribe((OpenCachedFileWithPluginRequest request) ->
                openCachedFileWithPlugin(request.getJsonRepresentation(),
                        request.getUniquePluginName(), request.getContext())
        );

        // Register for requests to list available plugins
        mListPluginsRequestObservable = mBus.register(ListPluginsRequest.class);

        // When a request comes in, call the appropriate function
        mListPluginsRequestObservable.subscribe((ListPluginsRequest listPluginsRequest) -> listPlugins());
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
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(uniquePluginName);
        
        if (launchIntent == null) {
            showToastAndLog(context, context.getString(R.string.could_not_open_plugin), null);
            return;
        }

        launchIntent.setAction(Constants.PLUGIN_ACTION);

        // Convert the extracted text to JSON
        String extractedTextInJSON = extractedText.toJSON();
        Log.d("JSON", extractedTextInJSON);

        // Start by clearing the old transfer files
        removeFilesThatStartWithFromDir(context.getCacheDir(), PROCESSED_PREFIX);

        Uri uri;

        try {
            uri = writeToTempFile(context, extractedTextInJSON, PROCESSED_PREFIX, EXTENSION);
        } catch (IOException e) {
            showToastAndLog(context, ERROR_LOG, e);
            return;
        }

        // Update the intent with some final flags, extras and data
        setDataAndFlags(launchIntent, uri, Constants.PLUGIN_INPUT_TYPE_EXTRACTED_TEXT);

        // Check if at least one app exists that can execute the task
        boolean pluginOpens = launchIntent.resolveActivity(context.getPackageManager()) != null;
        if (pluginOpens) {
            context.startActivity(launchIntent);
        } else {
            showToastAndLog(context, context.getString(R.string.could_not_open_plugin), null);
        }

    }


    //TODO delete if custom picker works

    /**
     * Opens a file with a given plugin
     *
     * @param extractedText the extracted text of the file to open
     * @param pluginAction  the target intent of the chooser
     * @param chooser       the plugin that was selected by the user in the chooser menu
     * @param context       the android context
     */

    private void openFileWithPluginChooser(ExtractedText extractedText, Intent pluginAction,
                                           Intent chooser, Context context) {

        // Convert the extracted text to JSON
        String extractedTextInJSON = extractedText.toJSON();
        Log.d("JSON", extractedTextInJSON);

        // Start by clearing the old transfer files
        removeFilesThatStartWithFromDir(context.getCacheDir(), PROCESSED_PREFIX);

        Uri uri;

        try {
            uri = writeToTempFile(context, extractedTextInJSON, PROCESSED_PREFIX, EXTENSION);
        } catch (IOException e) {
            showToastAndLog(context, ERROR_LOG, e);
            return;
        }



        setDataAndFlags(pluginAction, uri, Constants.PLUGIN_INPUT_TYPE_EXTRACTED_TEXT);




        // This is a bit of a hack, but it needs to be done because of trying to launch an
        // activity outside of and activity context
        // https://stackoverflow.com/questions/3918517/calling-startactivity-from-outside-of-an-activity-context
        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Check if at least one app exists that can execute the task
        boolean pluginOpens = pluginAction.resolveActivity(context.getPackageManager()) != null;
        if (pluginOpens) {
            context.startActivity(chooser);
        } else {
            showToastAndLog(context, context.getString(R.string.no_plugins_available), null);
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

        Intent pluginAction = new Intent(Constants.PLUGIN_ACTION);
        pluginAction.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pluginAction.setType("*/*");
        List<ResolveInfo> possiblePlugins = context.getPackageManager().queryIntentActivities(pluginAction, 0);

        // Loop over the plugins to see if the wanted plugin is in there
        ActivityInfo activityInfo = null;
        for (ResolveInfo pluginInfo : possiblePlugins) {
            if (uniquePluginName.equals(pluginInfo.activityInfo.applicationInfo.packageName)) {
                // If the wanted plugin is found, change activityInfo and break
                activityInfo = pluginInfo.activityInfo;
                break;
            }
        }

        if (activityInfo != null) {
            Intent launchIntent = new Intent(pluginAction);

            ComponentName cn = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
            launchIntent.setComponent(cn);

            // Start by clearing the old transfer files
            removeFilesThatStartWithFromDir(context.getCacheDir(), CACHED_PREFIX);

            Uri uri;

            try {
                uri = writeToTempFile(context, jsonRepresentation, CACHED_PREFIX, EXTENSION);
            } catch (IOException e) {
                showToastAndLog(context, ERROR_LOG, e);
                return;
            }

            setDataAndFlags(launchIntent, uri, Constants.PLUGIN_INPUT_TYPE_OBJECT);

            boolean cachedFileOpens = launchIntent.resolveActivity(context.getPackageManager()) != null;
            if (cachedFileOpens) {
                context.startActivity(launchIntent);
                return;
            }
        }
        showToastAndLog(context, context.getString(R.string.could_not_open_plugin), null);
    }

    /**
     * Lists all available plugins. It actually fires a ListPluginsResponseEvent that should be
     * subscribed on by the AuroraCommunicator
     */
    private void listPlugins() {
        // Get available plugins from plugin registry
        List<Plugin> pluginList = mPluginRegistry.getPlugins();

        // Make a response event and post it
        ListPluginsResponse response = new ListPluginsResponse(pluginList);
        mBus.post(response);
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
                        Log.d(CLASS_TAG, "There was a problem removing old files from "
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
        handler.post(() -> Toast.makeText(context, message,
                Toast.LENGTH_LONG).show());
    }

    /**
     * Private helper method that shows a toast on the main tread and also logs the message
     *
     * @param context the application context
     * @param message the message that needs to be shown
     * @param e       the throwable error
     */
    private void showToastAndLog(Context context, String message, @Nullable Throwable e) {
        showToast(context, message);
        if (e != null) {
            Log.e(CLASS_TAG, message, e);
        } else {
            Log.d(CLASS_TAG, message);
        }

    }

    /**
     * Private helper function that writes content to a file with a name that starts with a
     * prefix, ends with suffix, and a random number in between.
     *
     * @param context the application context
     * @param content the content that will be written
     * @param prefix  name of the file
     * @param suffix  extension of the file
     * @return Uri to the file on success
     * @throws IOException on failure
     */
    private Uri writeToTempFile(Context context, String content, String prefix, String suffix) throws IOException {
        // Write the processed file to the cache directory
        File file = File.createTempFile(prefix, suffix, context.getCacheDir());

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
