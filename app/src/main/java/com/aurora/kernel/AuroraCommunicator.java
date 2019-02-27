package com.aurora.kernel;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.kernel.event.PluginSettingsRequest;
import com.aurora.kernel.event.ListPluginsRequest;
import com.aurora.plugin.BasicPlugin;

import java.util.List;

import io.reactivex.Observable;

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
    public Observable<Fragment> openFileWithPlugin(String pluginName, String fileRef){
        this.mBus.post(new OpenFileWithPluginRequest(pluginName, fileRef));

        // TODO: Before implementing this, first do testing
        Log.d("AuroraCommunicator", "Callback not implemented yet!");
        return null;
    }

    /**
     * Gets the settings of a plugin
     *
     * @param pluginName the name of the plugin to get the settings for
     */
    public Observable<Class<? extends Activity>> getSettingsOfPlugin(String pluginName) {
        this.mBus.post(new PluginSettingsRequest(pluginName));

        // TODO: Before implementing this, first do testing
        Log.d("AuroraCommunicator", "Callback not implemented yet!");
        return null;
    }

    /**
     * Gets a list of all the available plugins
     */
    public Observable<List<BasicPlugin>> getListofPlugins() {
        this.mBus.post(new ListPluginsRequest());

        // TODO: Before implementing this, first do testing
        Log.d("AuroraCommunicator", "Callback not implemented yet!");
        return null;
    }


}
