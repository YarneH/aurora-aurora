package com.aurora.kernel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
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

@SuppressWarnings("common-java:DuplicatedBlocks")
//Duplicated code because OpenFileWithPluginChooser will be removed soon
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
    private Observable<OpenFileWithPluginRequest> mOpenFileWithPluginRequestObservable;

    //TODO delete when custom picker is finished
    /**
     * An observable keeping track of incoming OpenFileWithPluginChooserRequests
     */
    private Observable<OpenFileWithPluginChooserRequest> mOpenFileWithPluginChooserRequestObservable;

    /**
     * An observable keeping track of incoming OpenCachedFileWithPluginRequests
     */
    private Observable<OpenCachedFileWithPluginRequest> mOpenCachedFileWithPluginRequestObservable;

    /**
     * An observable keeping track of incoming ListPluginsRequests
     */
    private Observable<ListPluginsRequest> mListPluginsRequestObservable;

    /**
     *
     */
    private static final String ERROR_LOG = "Writing to temporary files went wrong";


    /**
     * Creates a PluginCommunicator. There should be only one instance at a time
     *
     * @param bus            a reference to the unique bus instances that all communicators should use to
     *                       communicate events
     * @param pluginRegistry a reference to the plugin registry
     */
    public PluginCommunicator(Bus bus, PluginRegistry pluginRegistry) {
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
            showToast(context, context.getString(R.string.could_not_open_plugin));
            return;
        }

        launchIntent.setAction(Constants.PLUGIN_ACTION);

        // Convert the extracted text to JSON
        String extractedTextInJSON = extractedText.toJSON();
        Log.d("JSON", extractedTextInJSON);

        // Start by clearing the full android cache directory of our app
        clearCacheDir(context.getCacheDir());

        Uri uri;

        try {
            uri = writeToTempFile(context, extractedTextInJSON, "processed-", ".aur");
        } catch (IOException e) {
            Log.e(CLASS_TAG, ERROR_LOG, e);
            showToast(context, ERROR_LOG);
            return;
        }

        // Make the file readable by the plugin, this permission last until the activity of
        // the receiving app ends
        launchIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // Add the uri of the file as data
        launchIntent.setData(uri);
        // Specify the datatype
        launchIntent.putExtra(Constants.PLUGIN_INPUT_TYPE, Constants.PLUGIN_INPUT_TYPE_EXTRACTED_TEXT);

        // This is a bit of a hack, but it needs to be done because of trying to launch an
        // activity outside of and activity context
        // https://stackoverflow.com/questions/3918517/calling-startactivity-from-outside-of-an-activity-context
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Check if at least one app exists that can execute the task
        boolean pluginOpens = launchIntent.resolveActivity(context.getPackageManager()) != null;

        if (pluginOpens) {
            context.startActivity(launchIntent);
        } else {
            showToast(context, context.getString(R.string.could_not_open_plugin));
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
                                           Intent chooser, Context context) { //NOSONAR

        // Convert the extracted text to JSON
        String extractedTextInJSON = extractedText.toJSON();
        Log.d("JSON", extractedTextInJSON);

        // Start by clearing the full android cache directory of our app
        clearCacheDir(context.getCacheDir()); //NOSONAR

        Uri uri; //NOSONAR

        try {
            uri = writeToTempFile(context, extractedTextInJSON, "processed-", ".aur"); //NOSONAR
        } catch (IOException e) { //NOSONAR
            Log.e(CLASS_TAG, ERROR_LOG, e); //NOSONAR
            showToast(context, ERROR_LOG); //NOSONAR
            return;//NOSONAR 
        }

        // Make the file readable by the plugin, this permission last until the activity of
        // the receiving app ends
        pluginAction.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // Add the uri of the file as data
        pluginAction.setData(uri);
        // Specify the datatype
        pluginAction.putExtra(Constants.PLUGIN_INPUT_TYPE, Constants.PLUGIN_INPUT_TYPE_EXTRACTED_TEXT);

        // This is a bit of a hack, but it needs to be done because of trying to launch an
        // activity outside of and activity context
        // https://stackoverflow.com/questions/3918517/calling-startactivity-from-outside-of-an-activity-context
        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Check if at least one app exists that can execute the task
        boolean pluginOpens = pluginAction.resolveActivity(context.getPackageManager()) != null;

        if (pluginOpens) {
            context.startActivity(chooser);
        } else {
            showToast(context, context.getString(R.string.no_plugins_available));
        }
    }

    /**
     * Opens a cached file with a given plugin
     *
     * @param jsonRepresentation    the json representation of the plugin object to represent
     * @param uniquePluginName      the unique name of the plugin
     * @param context            the android context
     */
    private void openCachedFileWithPlugin(String jsonRepresentation, String uniquePluginName, Context context) {
        // Create intent to open plugin
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(uniquePluginName);

        // Check if plugin is found

        if (launchIntent == null) {
            showToast(context, context.getString(R.string.could_not_open_plugin));
            return;
        }

        launchIntent.setAction(Constants.PLUGIN_ACTION);

        // Start by clearing the full android cache directory of our app
        clearCacheDir(context.getCacheDir());

        Uri uri;

        try {
            uri = writeToTempFile(context, jsonRepresentation, "cached-", ".aur");
        } catch (IOException e) {
            Log.e(CLASS_TAG, ERROR_LOG, e);
            showToast(context, ERROR_LOG);
            return;
        }

        // Make the file readable by the plugin, this permission last until the activity of
        // the receiving app ends
        launchIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // Add the uri of the file as data
        launchIntent.setData(uri);
        // Specify the datatype
        launchIntent.putExtra(Constants.PLUGIN_INPUT_TYPE, Constants.PLUGIN_INPUT_TYPE_OBJECT);


        boolean cachedFileOpens = launchIntent.resolveActivity(context.getPackageManager()) != null;

        if (cachedFileOpens) {
            context.startActivity(launchIntent);
        } else {
            showToast(context, context.getString(R.string.could_not_open_plugin));
        }
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
     * Private helper function to clear the cache directory
     *
     * @param dir the directory that needs to be cleared
     * @return true if success, false otherwise
     */
    @SuppressWarnings("squid:S4042")
    private static boolean clearCacheDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child: children) {
                boolean success = clearCacheDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        if(dir != null) {
            return dir.delete();
        }
        return false;
    }

    /**
     * Private helper method that shows a toast on the main tread
     *
     * @param context   the application context
     * @param message   the message that needs to be shown
     */
    private void showToast(Context context, String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(context, message,
                Toast.LENGTH_LONG).show());
    }

    /**
     * Private helper function that writes content to a file with a name that starts with a
     * prefix, ends with suffix, and a random number in between.
     *
     * @param context   the application context
     * @param content   the content that will be written
     * @param prefix    name of the file
     * @param suffix    extension of the file
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
}
