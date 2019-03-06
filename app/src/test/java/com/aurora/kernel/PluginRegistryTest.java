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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PluginRegistryTest {
    private static Bus mBus;
    private static ProcessingCommunicator mProcessingCommunicator;
    private static PluginRegistry mRegistry;

    private static PluginCommunicator mPluginCommunicator;

    private static final String DUMMY_PLUGIN_1 = "DummyPlugin1";
    private static final String DUMMY_PLUGIN_2 = "DummyPlugin2";
    private static final String PLUGIN_NAME_NOT_IN_MAP = "DummyPlugin3";

    private static Plugin plugin1;
    private static Plugin plugin2;

    @BeforeClass
    public static void initialize() {
        mBus = new Bus();
        mProcessingCommunicator = new ProcessingCommunicator(mBus);
        mRegistry = new PluginRegistry(mProcessingCommunicator);

        mPluginCommunicator = new PluginCommunicator(mBus, mRegistry);

        // Prepare the registry by setting the map
        try {
            // get the private field
            Field pluginMap = mRegistry.getClass().getDeclaredField("mPluginsMap");
            pluginMap.setAccessible(true);

            // Create fake PluginEnvironments and PluginProcessors
            PluginEnvironment environment1 = new DummyPluginEnvironment(mPluginCommunicator);
            PluginEnvironment environment2 = new DummyPluginEnvironment(mPluginCommunicator);

            PluginProcessor processor1 = new DummyPluginProcessor(mProcessingCommunicator);
            PluginProcessor processor2 = new DummyPluginProcessor(mProcessingCommunicator);

            // Create dummy plugins
            plugin1 = new DummyPlugin1(environment1, processor1);
            plugin2 = new DummyPlugin2(environment2, processor2);

            Map<String, Plugin> fakeMap = new HashMap<>();
            fakeMap.put(DUMMY_PLUGIN_1, plugin1);
            fakeMap.put(DUMMY_PLUGIN_2, plugin2);

            pluginMap.set(mRegistry, fakeMap);

            // Set the accessibility of the field back to false
            pluginMap.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void PluginRegistry_loadPlugin_shouldReturnNull() {
        // Call load plugin method with a name that is not in the map
        PluginEnvironment environment = mRegistry.loadPlugin(PLUGIN_NAME_NOT_IN_MAP);

        // Assert that environment is indeed null
        assertNull(environment);
    }

    @Test
    public void PluginRegistry_loadPlugin_shouldReturnDummyPlugins() {
        // Call load plugin method with names that are in the map
        PluginEnvironment environment1 = mRegistry.loadPlugin(DUMMY_PLUGIN_1);
        PluginEnvironment environment2 = mRegistry.loadPlugin(DUMMY_PLUGIN_2);

        // Assert that the environments are what expected
        assertEquals(plugin1.getPluginEnvironment(), environment1);
        assertEquals(plugin2.getPluginEnvironment(), environment2);
    }


    @Test
    public void PluginRegistry_loadPlugin_shouldSetProcessorOfCommunicator() {
        // Call load plugin method with name that is in the map
        mRegistry.loadPlugin(DUMMY_PLUGIN_1);

        // Assert that field of processor communicator is what expected
        assertEquals(mProcessingCommunicator.getActivePluginProcessor(), plugin1.getPluginProcessor());
    }

    /**
     * Dummy plugin for testing purposes
     */
    private static class DummyPlugin1 extends Plugin {
        public DummyPlugin1(PluginEnvironment pluginEnvironment, PluginProcessor pluginProcessor) {
            super(pluginEnvironment, pluginProcessor);
        }
    }

    /**
     * Another dummy plugin for testing purposes
     */
    private static class DummyPlugin2 extends Plugin {
        public DummyPlugin2(PluginEnvironment pluginEnvironment, PluginProcessor pluginProcessor) {
            super(pluginEnvironment, pluginProcessor);
        }
    }

    /**
     * Dummy plugin enviroment class for testing purposes
     */
    private static class DummyPluginEnvironment extends PluginEnvironment {
        public DummyPluginEnvironment(PluginCommunicator pluginCommunicator) {
            super(pluginCommunicator);
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
}
