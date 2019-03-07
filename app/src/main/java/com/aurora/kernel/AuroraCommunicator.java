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

    public AuroraCommunicator(Bus mBus) {
        super(mBus);

    }

    /**
     * Open file with a given plugin
     *
     * @param pluginName the name of the plugin to open the file with, contains version number
     * @param fileRef    a reference to the file that needs to be opened
     * @return the fragment to be shown wrapped in an observable
     */
    public Observable<Fragment> openFileWithPlugin(String pluginName, String fileRef) {
        Observable<OpenFileWithPluginResponse> mOpenFileWithPluginResponse
                = this.mBus.register(OpenFileWithPluginResponse.class);
        this.mBus.post(new OpenFileWithPluginRequest(pluginName, fileRef));

        // The map function is called on the observable. Then, the getPluginFragment function
        // is called on the response event and the result is returned
        return mOpenFileWithPluginResponse.map(OpenFileWithPluginResponse::getPluginFragment);
    }

    /**
     * Gets the settings of a plugin
     *
     * @param pluginName the name of the plugin to get the settings for
     * @return the class reference to the activity to show wrapped in an observable
     */
    public Observable<Class<? extends Activity>> getSettingsOfPlugin(String pluginName) {
        Observable<PluginSettingsResponse> mPluginSettingsResponse
                = this.mBus.register(PluginSettingsResponse.class);
        this.mBus.post(new PluginSettingsRequest(pluginName));

        return mPluginSettingsResponse.map(PluginSettingsResponse::getActivity);
    }

    /**
     * Gets a list of all the available plugins
     * @return a list of basic information on every plugin wrapped in an observable
     */
    public Observable<List<BasicPlugin>> getListOfPlugins() {
        Observable<ListPLuginsResponse> mListPluginsResponse
                = this.mBus.register(ListPLuginsResponse.class);
        this.mBus.post(new ListPluginsRequest());

        return mListPluginsResponse.map(ListPLuginsResponse::getPlugins);
    }


}
