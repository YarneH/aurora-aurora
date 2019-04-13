package com.aurora.kernel;

import android.content.Context;
import android.content.Intent;
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
    private static final String CLASS_TAG = "PluginCommunicator";

    private PluginRegistry mPluginRegistry;

    private Observable<OpenFileWithPluginRequest> mOpenFileWithPluginRequestObservable;
    private Observable<OpenCachedFileWithPluginRequest> mOpenCachedFileWithPluginRequestObservable;
    private Observable<ListPluginsRequest> mListPluginsRequestObservable;


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
        pluginAction.putExtra(Constants.PLUGIN_INPUT_EXTRACTED_TEXT, extractedTextInJSON);

        boolean pluginOpens = pluginAction.resolveActivity(context.getPackageManager()) != null;

        // This is a bit of a hack, but it needs to be done because of trying to launch an
        // activity outside of and activity context
        // https://stackoverflow.com/questions/3918517/calling-startactivity-from-outside-of-an-activity-context
        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        if (pluginOpens) {
            context.startActivity(chooser);
        } else {
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
}
