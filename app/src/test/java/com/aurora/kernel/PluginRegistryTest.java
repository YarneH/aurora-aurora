package com.aurora.kernel;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.aurora.externalservice.PluginEnvironment;
import com.aurora.internalservice.internalprocessor.ExtractedText;
import com.aurora.plugin.Plugin;
import com.aurora.plugin.ProcessedText;
import com.aurora.processingservice.PluginProcessor;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PluginRegistryTest {
    private static Bus mBus;
    private static ProcessingCommunicator mProcessingCommunicator;
    private static PluginRegistry mRegistry;
    private static PluginCommunicator mPluginCommunicator;

    private static String mConfigRef = "testConfigFile.cfg";

    private static final String DUMMY_NAME_1 = "DummyPlugin1";
    private static final String DUMMY_NAME_2 = "DummyPlugin2";
    private static final String NOT_IN_MAP_PLUGIN = "DummyPlugin3";
    private static final String DESCRIPTION_1 = "Dummydescription 1";
    private static final String DESCRIPTION_2 = "Dummydescription 2";
    private static final String VERSION_1 = "0.1";
    private static final String VERSION_2 = "0.1";

    private static Plugin plugin1;
    private static Plugin plugin2;

    @BeforeClass
    public static void initialize() {
        mBus = new Bus();
        mProcessingCommunicator = new ProcessingCommunicator(mBus);
        mRegistry = new PluginRegistry(mProcessingCommunicator, mConfigRef);
        mPluginCommunicator = new PluginCommunicator(mBus, mRegistry);

        // Clear the map of plugins
        mRegistry.removeAllPlugins();

        // Create environments and processors
        PluginEnvironment environment1 = new DummyPluginEnvironment(mPluginCommunicator, DummyActivity.class);
        PluginEnvironment environment2 = new DummyPluginEnvironment(mPluginCommunicator, DummyActivity.class);

        PluginProcessor processor1 = new DummyPluginProcessor(mProcessingCommunicator);
        PluginProcessor processor2 = new DummyPluginProcessor(mProcessingCommunicator);

        // Create dummy plugins
        plugin1 = new DummyPlugin1(DUMMY_NAME_1, null, DESCRIPTION_1, VERSION_1, environment1, processor1);
        plugin2 = new DummyPlugin2(DUMMY_NAME_2, null, DESCRIPTION_2, VERSION_2, environment2, processor2);

        // Add dummy plugins
        mRegistry.registerPlugin(DUMMY_NAME_1, plugin1);
        mRegistry.registerPlugin(DUMMY_NAME_2, plugin2);
    }


    @Test
    public void PluginRegistry_loadPlugin_shouldReturnNull() {
        // Call load plugin method with a name that is not in the map
        PluginEnvironment environment = mRegistry.loadPlugin(NOT_IN_MAP_PLUGIN);

        // Assert that environment is indeed null
        assertNull(environment);
    }

    @Test
    public void PluginRegistry_loadPlugin_shouldReturnDummyPlugins() {
        // Call load plugin method with names that are in the map
        PluginEnvironment environment1 = mRegistry.loadPlugin(DUMMY_NAME_1);
        PluginEnvironment environment2 = mRegistry.loadPlugin(DUMMY_NAME_2);

        // Assert that the environments are what expected
        assertEquals(plugin1.getPluginEnvironment(), environment1);
        assertEquals(plugin2.getPluginEnvironment(), environment2);
    }


    @Test
    public void PluginRegistry_loadPlugin_shouldSetProcessorOfCommunicator() {
        // Call load plugin method with name that is in the map
        mRegistry.loadPlugin(DUMMY_NAME_1);

        // Assert that field of processor communicator is what expected
        assertEquals(mProcessingCommunicator.getActivePluginProcessor(), plugin1.getPluginProcessor());
    }

    @Test
    public void PluginRegistry_loadPlugin_shouldNotSetProcessorOfCommunicator() {
        // First get the current active processor
        PluginProcessor currentProcessor = mProcessingCommunicator.getActivePluginProcessor();

        // Call the method with a name that is not in the map
        mRegistry.loadPlugin(NOT_IN_MAP_PLUGIN);

        // Assert that field of processor communicator is not changed
        assertEquals(currentProcessor, mProcessingCommunicator.getActivePluginProcessor());
    }

    /**
     * Dummy plugin for testing purposes
     */
    private static class DummyPlugin1 extends Plugin {
        public DummyPlugin1(String name, File pluginLogo, String description, String version,
                            PluginEnvironment pluginEnvironment, PluginProcessor pluginProcessor) {
            super(name, pluginLogo, description, version, pluginEnvironment, pluginProcessor);
        }
    }

    /**
     * Another dummy plugin for testing purposes
     */
    private static class DummyPlugin2 extends Plugin {

        public DummyPlugin2(String name, File pluginLogo, String description, String version,
                            PluginEnvironment pluginEnvironment, PluginProcessor pluginProcessor) {
            super(name, pluginLogo, description, version, pluginEnvironment, pluginProcessor);
        }
    }

    /**
     * Dummy plugin enviroment class for testing purposes
     */
    private static class DummyPluginEnvironment extends PluginEnvironment {


        public DummyPluginEnvironment(PluginCommunicator pluginCommunicator, Class<? extends Activity> pluginSettingsActivity) {
            super(pluginCommunicator, pluginSettingsActivity);
        }

        @Override
        public Class<? extends Activity> getSettingsActivity() {
            return null;
        }

        @Override
        public Fragment openFile(String fileRef) {
            return null;
        }

        @Override
        protected void resultProcessFileWithPluginProcessor(ProcessedText processedText) {
            Log.d("DummyPluginEnvironment", "This is a dummy environment.");
        }
    }

    /**
     * Dummy plugin processor class for testing purposes
     */
    private static class DummyPluginProcessor extends PluginProcessor {

        public DummyPluginProcessor(ProcessingCommunicator processingCommunicator) {
            super(processingCommunicator);
        }

        @Override
        public ProcessedText processFileWithPluginProcessor(String fileRef) {
            return null;
        }

        @Override
        protected void resultProcessFileWithAuroraProcessor(ExtractedText extractedText) {

        }
    }

    /**
     * Dummy activity class for testing purposes
     */
    private class DummyActivity extends Activity {

    }
}
