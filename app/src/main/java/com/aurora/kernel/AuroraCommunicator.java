package com.aurora.kernel;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.aurora.kernel.event.ListPLuginsResponse;
import com.aurora.kernel.event.ListPluginsRequest;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.kernel.event.OpenFileWithPluginResponse;
import com.aurora.kernel.event.PluginSettingsRequest;
import com.aurora.kernel.event.PluginSettingsResponse;
import com.aurora.plugin.BasicPlugin;

import java.util.List;

import io.reactivex.Observable;

/**
 * Communicator class that communicates to Aurora app environment
 */
public class AuroraCommunicator extends Communicator {
    private static final String CLASS_TAG = "AuroraCommunicator";
    private Observable<OpenFileWithPluginResponse> mOpenFileWithPluginResponse;
    private Observable<PluginSettingsResponse> mPluginSettingsResponse;
    private Observable<ListPLuginsResponse> mListPLuginsResponse;

    public AuroraCommunicator(Bus mBus) {
        super(mBus);

        // Subscribe to response events
        mOpenFileWithPluginResponse = this.mBus.register(OpenFileWithPluginResponse.class);
        mPluginSettingsResponse = this.mBus.register(PluginSettingsResponse.class);
        mListPLuginsResponse = this.mBus.register(ListPLuginsResponse.class);
    }

    /**
     * Open file with a given plugin
     *
     * @param pluginName the name of the plugin to open the file with
     * @param fileRef a reference to the file that needs to be opened
     */
    public Observable<Fragment> openFileWithPlugin(String pluginName, String fileRef){
        this.mBus.post(new OpenFileWithPluginRequest(pluginName, fileRef));

        // The map function is called on the observable. Then, the getPluginFragment function
        // is called on the response event and the result is returned
        return mOpenFileWithPluginResponse.map(OpenFileWithPluginResponse::getPluginFragment);
    }

    /**
     * Gets the settings of a plugin
     *
     * @param pluginName the name of the plugin to get the settings for
     */
    public Observable<Class<? extends Activity>> getSettingsOfPlugin(String pluginName) {
        this.mBus.post(new PluginSettingsRequest(pluginName));

        return mPluginSettingsResponse.map(PluginSettingsResponse::getActivity);
    }

    /**
     * Gets a list of all the available plugins
     */
    public Observable<List<BasicPlugin>> getListOfPlugins() {
        this.mBus.post(new ListPluginsRequest());

        return mListPLuginsResponse.map(ListPLuginsResponse::getPlugins);
    }


}
