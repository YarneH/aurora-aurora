package com.aurora.kernel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.aurora.aurora.R;
import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.kernel.event.ListPluginsRequest;
import com.aurora.kernel.event.ListPluginsResponse;
import com.aurora.kernel.event.OpenCachedFileWithPluginRequest;
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
    private Observable<OpenFileWithPluginRequest> mOpenFileWithPluginRequestObservable;

    /**
     * An observable keeping track of incoming OpenCachedFileWithPluginRequests
     */
    private Observable<OpenCachedFileWithPluginRequest> mOpenCachedFileWithPluginRequestObservable;

    /**
     * An observable keeping track of incoming ListPluginsRequests
     */
    private Observable<ListPluginsRequest> mListPluginsRequestObservable;


    /**
     * Creates a PluginCommunicator. There should be only one instance at a time
     * @param bus a reference to the unique bus instances that all communicators should use to communicate events
     * @param pluginRegistry a reference to the plugin registry
     */
    public PluginCommunicator(Bus bus, PluginRegistry pluginRegistry) {
        super(bus);

        this.mPluginRegistry = pluginRegistry;

        // Register for requests to open file with plugin
        mOpenFileWithPluginRequestObservable = mBus.register(OpenFileWithPluginRequest.class);

        // When a request comes in, call appropriate function
        mOpenFileWithPluginRequestObservable.subscribe((OpenFileWithPluginRequest request) ->
                openFileWithPlugin(request.getExtractedText(), request.getPluginAction(),
                        request.getChooser(), request.getContext())
        );

        // Register for requests to open a cached file with plugin
        mOpenCachedFileWithPluginRequestObservable = mBus.register(OpenCachedFileWithPluginRequest.class);

        // When a request comes in, call appropriate function
        mOpenCachedFileWithPluginRequestObservable.subscribe((OpenCachedFileWithPluginRequest request) ->
                openCachedFileWithPlugin(request.getJsonRepresentation(), request.getContext())
        );

        // Register for requests to list available plugins
        mListPluginsRequestObservable = mBus.register(ListPluginsRequest.class);

        // When a request comes in, call the appropriate function
        mListPluginsRequestObservable.subscribe((ListPluginsRequest listPluginsRequest) -> listPlugins());
    }


    /**
     * Opens a file with a given plugin
     *
     * @param extractedText the extracted text of the file to open
     *                      TODO: add tests for this method
     * @param pluginAction  the target intent of the chooser
     * @param chooser       the plugin that was selected by the user in the chooser menu
     * @param context       the android context
     */
    private void openFileWithPlugin(ExtractedText extractedText, Intent pluginAction, Intent chooser, Context context) {
        String extractedTextInJSON = extractedText.toJSON();
        Log.d("JSON", extractedTextInJSON);

        // Start by clearing the full android cache directory of our app
        clearCacheDir(context.getCacheDir());

        try {
            // Write the processed file to the cache directory
            File file = File.createTempFile("processed-", ".aur", context.getCacheDir());
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(extractedTextInJSON);
            fileWriter.close();

            // Get the URI of the file
            Uri fileUri = FileProvider.getUriForFile(context, "com.aurora.aurora.provider",
                    file);

            // Make the file readable by the plugin, this permission last until the activity of
            // the receiving app ends
            pluginAction.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // Add the uri of the file as data
            pluginAction.setData(fileUri);

        } catch (IOException e) {
            Log.e(CLASS_TAG, "Writing to temporary files went wrong", e);
        }

        // This field now gets used as a boolean to let the plugin know the kind of data will be
        // read from the file.
        pluginAction.putExtra(Constants.PLUGIN_INPUT_EXTRACTED_TEXT, "");


        // Check if at least one app exists that can execute the task
        boolean pluginOpens = pluginAction.resolveActivity(context.getPackageManager()) != null;

        // This is a bit of a hack, but it needs to be done because of trying to launch an
        // activity outside of and activity context
        // https://stackoverflow.com/questions/3918517/calling-startactivity-from-outside-of-an-activity-context
        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        if (pluginOpens) {
            context.startActivity(chooser);
        } else {
            // This toast crashes the app
            Toast.makeText(context, context.getString(R.string.no_plugins_available),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Opens a cached file with a given plugin
     *
     * @param jsonRepresentation the json representation of the plugin object to represent
     * @param context            the android context
     */
    // TODO: adapt to use file in cache to transfer data
    private void openCachedFileWithPlugin(String jsonRepresentation, Context context) {
        Intent pluginAction = new Intent(Constants.PLUGIN_ACTION);

        // Create chooser TODO: this is not necessary anymore, plugin should be known
        Intent chooser = Intent.createChooser(pluginAction, context.getString(R.string.select_plugin));
        pluginAction.putExtra(Constants.PLUGIN_INPUT_OBJECT, jsonRepresentation);

        boolean cachedFileOpens = pluginAction.resolveActivity(context.getPackageManager()) != null;

        if (cachedFileOpens) {
            context.startActivity(chooser);
        } else {
            Toast.makeText(context, context.getString(R.string.no_plugins_available), Toast.LENGTH_LONG).show();
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
     * @param dir the directory that needs to be cleared
     * @return true if success, false otherwise
     */
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
}
