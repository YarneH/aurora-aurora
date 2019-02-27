package com.aurora.kernel;

import android.util.Log;

import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.kernel.event.PluginSettingsRequest;
import com.aurora.kernel.event.ListPluginsRequest;

/**
 * Communicator class that communicates to Aurora app environment
 */
public class AuroraCommunicator extends Communicator {

    public AuroraCommunicator(Bus mBus) {
        super(mBus);
    }

    /**
     * Open file with a given plugin
     *
     * @param pluginName the name of the plugin to open the file with
     * @param fileRef a reference to the file that needs to be opened
     */
    public void openFileWithPlugin(String pluginName, String fileRef){
        this.mBus.post(new OpenFileWithPluginRequest(pluginName, fileRef));

        Log.d("AuroraCommunicator", "Callback not implemented yet!");
    }

    /**
     * Gets the settings of a plugin
     *
     * @param pluginName the name of the plugin to get the settings for
     */
    public void getSettingsOfPlugin(String pluginName) {
        this.mBus.post(new PluginSettingsRequest(pluginName));

        Log.d("AuroraCommunicator", "Callback not implemented yet!");
    }

    /**
     * Gets a list of all the available plugins
     */
    public void getListofPlugins() {
        this.mBus.post(new ListPluginsRequest());

        Log.d("AuroraCommunicator", "Callback not implemented yet!");
    }


}
