package com.aurora.kernel;

import android.util.Log;

import com.aurora.kernel.event.PluginEvent;
import com.aurora.kernel.event.PluginSettingsEvent;
import com.aurora.kernel.event.RequestPluginsEvent;
import com.aurora.plugin.Plugin;
import com.aurora.plugin.PluginFragment;

import io.reactivex.Observable;

/**
 * Communicator class that communicates to Aurora app environment
 */
public class AuroraCommunicator extends Communicator {

    public AuroraCommunicator(Bus mBus) {
        super(mBus);
    }

    /**
     * Open file with Plugin
     */
    public void openFileWithPlugin(String pluginName, String fileRef){
        this.mBus.post(new PluginEvent(pluginName, fileRef));

        Log.d("AuroraCommunicator", "Callback not implemented yet!");
    }

    public void getSettingsOfPlugin(String pluginName) {
        this.mBus.post(new PluginSettingsEvent(pluginName));

        Log.d("AuroraCommunicator", "Callback not implemented yet!");
    }

    public void getListofPlugins() {
        this.mBus.post(new RequestPluginsEvent());

        Log.d("AuroraCommunicator", "Callback not implemented yet!");
    }


}
