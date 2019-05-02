package com.aurora.kernel;

import com.aurora.kernel.event.ListPluginsRequest;
import com.aurora.kernel.event.ListPluginsResponse;
import com.aurora.plugin.Plugin;
import com.aurora.util.MockContext;

import org.junit.BeforeClass;
import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

public class PluginCommunicatorUnitTest {

    private static Bus mBus;
    private static ProcessingCommunicator mProcessingCommunicator;
    private static PluginRegistry mPluginRegistry;
    private static PluginCommunicator mPluginCommunicator;
    private static final String PLUGINS_CFG = "testConfigFile.json";
    private static final String UNIQUE_PLUGIN_NAME = "com.aurora.dummyplugin";
    private static final String PLUGIN_NAME = "DummyPlugin";
    private static final String PLUGIN_NOT_IN_REGISTRY = "You have found the candy, congratulations!";
    private static final String FILE_PATH = "/path/to/file";

    private static Plugin mPlugin;


    @BeforeClass
    public static void initialize() {
        mBus = new Bus(Schedulers.trampoline());

        mProcessingCommunicator = new ProcessingCommunicator(mBus);
        mPluginRegistry = new PluginRegistry(mProcessingCommunicator, PLUGINS_CFG, new MockContext());
        mPluginCommunicator = new PluginCommunicator(mBus, mPluginRegistry);
    }

    /**
     * Helper method to add a dummy plugin to the registry
     */
    private void addPluginToRegistry() {
        // Clear registry
        mPluginRegistry.removeAllPlugins();

        // Create name and description
        String description = "This is a dummy description.";
        int versionNumber = 1;
        String versionCode = "0.1";

        // Create plugin using environment and processor
        mPlugin = new Plugin(UNIQUE_PLUGIN_NAME, PLUGIN_NAME, null, description, versionNumber, versionCode);

        // Register plugin in registry
        mPluginRegistry.registerPlugin(PLUGIN_NAME, mPlugin);
    }


    @Test
    public void PluginCommunicator_ListPluginRequest_shouldPostResponseEvent() {
        addPluginToRegistry();

        // Create a test observer to subscribe to observable
        TestObserver<String> observer = new TestObserver<>();

        // Register for ListPluginsResponse events
        Observable<ListPluginsResponse> observable = mBus.register(ListPluginsResponse.class);

        // Subscribe to observable
        observable.map(listPluginsResponse -> listPluginsResponse.getPlugins().get(0).getName()).subscribe(observer);

        // Post request event
        mBus.post(new ListPluginsRequest());

        // Assert values
        observer.assertSubscribed();
        observer.assertValue(mPlugin.getName());
        observer.dispose();
    }

}
