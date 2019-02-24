package com.aurora.kernel;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.aurora.plugin.PluginFragment;

/**
 * Communicator that communicates with Plugin environments
 */
public class PluginCommunicator extends Communicator {
    private PluginFragment mPluginFragment;
    private PluginRegistry mPluginRegistry;

    /**
     * Requests settingActivity from a pluginFragment
     * @return Class reference of the activity to open
     */
    public Class<Activity> requestSettingsActivity() {
        Log.d("PluginCommunicator", "Not implemented yet!");
        return null;
    }

    public Fragment openFileWithPlugin(String pluginName, String fileRef) {
        Log.d("PluginCommunicator", "Not implemented yet!");
        return null;
    }
}
