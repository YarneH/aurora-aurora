package com.aurora.kernel;

import android.content.Context;
import android.content.Intent;
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
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(uniquePluginName);

        if (launchIntent == null) {
            Toast.makeText(context, context.getString(R.string.could_not_open_plugin), Toast.LENGTH_LONG).show();
            return;
        }

        launchIntent.setAction(Constants.PLUGIN_ACTION);


        String extractedTextInJSON = extractedText.toJSON();
        launchIntent.putExtra(Constants.PLUGIN_INPUT_EXTRACTED_TEXT, extractedTextInJSON);

        Log.d("JSON", extractedTextInJSON);

        boolean pluginOpens = launchIntent.resolveActivity(context.getPackageManager()) != null;

        // This is a bit of a hack, but it needs to be done because of trying to launch an
        // activity outside of and activity context
        // https://stackoverflow.com/questions/3918517/calling-startactivity-from-outside-of-an-activity-context
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        if (pluginOpens) {
            context.startActivity(launchIntent);
        } else {
            Toast.makeText(context, context.getString(R.string.could_not_open_plugin),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Opens a cached file with a given plugin
     *
     * @param jsonRepresentation the json representation of the plugin object to represent
     * @param context            the android context
     */
    private void openCachedFileWithPlugin(String jsonRepresentation, String uniquePluginName, Context context) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(uniquePluginName);

        // Check if plugin is found
        if (launchIntent == null) {
            Toast.makeText(context, context.getString(R.string.could_not_open_plugin), Toast.LENGTH_LONG).show();
            return;
        }

        launchIntent.setAction(Constants.PLUGIN_ACTION);
        launchIntent.putExtra(Constants.PLUGIN_INPUT_OBJECT, jsonRepresentation);

        boolean cachedFileOpens = launchIntent.resolveActivity(context.getPackageManager()) != null;

        if (cachedFileOpens) {
            context.startActivity(launchIntent);
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
}
