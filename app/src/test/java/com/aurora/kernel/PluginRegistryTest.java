package com.aurora.kernel;

import com.aurora.plugin.Plugin;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PluginRegistryTest {
    private static Bus mBus;
    private static ProcessingCommunicator mProcessingCommunicator;
    private static PluginRegistry mRegistry;
    private static PluginCommunicator mPluginCommunicator;

    private static String mConfigRef = "testConfigFile.json";

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
        mBus = new Bus(Schedulers.trampoline());
        mProcessingCommunicator = new ProcessingCommunicator(mBus);

        // Create config file
        File configFile = new File(mConfigRef);

        try (Writer writer = new BufferedWriter(new FileWriter(configFile))) {
            // Write to file
            writer.write("[]");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRegistry = new
                PluginRegistry(mProcessingCommunicator, mConfigRef);

        mPluginCommunicator = new
                PluginCommunicator(mBus, mRegistry);

        // Clear the map of plugins
        mRegistry.removeAllPlugins();

        // Create dummy plugins
        plugin1 = new DummyPlugin1(DUMMY_NAME_1, DUMMY_NAME_1, null, DESCRIPTION_1, VERSION_1);
        plugin2 = new DummyPlugin2(DUMMY_NAME_2, DUMMY_NAME_2, null, DESCRIPTION_2, VERSION_2);

        // Add dummy plugins
        mRegistry.registerPlugin(DUMMY_NAME_1, plugin1);
        mRegistry.registerPlugin(DUMMY_NAME_2, plugin2);
    }


    @Test
    public void PluginRegistry_loadPlugin_shouldReturnNull() {
        // Call load plugin method with a name that is not in the map
        Plugin plugin = mRegistry.loadPlugin(NOT_IN_MAP_PLUGIN);

        // Assert that environment is indeed null
        assertNull(plugin);
    }

    @Test
    public void PluginRegistry_loadPlugin_shouldReturnDummyPlugins() {
        // Call load plugin method with names that are in the map
        Plugin pluginLoaded1 = mRegistry.loadPlugin(DUMMY_NAME_1);
        Plugin pluginLoaded2 = mRegistry.loadPlugin(DUMMY_NAME_2);

        // Assert that the environments are what expected
        assertEquals(plugin1, pluginLoaded1);
        assertEquals(plugin2, pluginLoaded2);
    }


    /**
     * Dummy plugin for testing purposes
     */
    private static class DummyPlugin1 extends Plugin {
        public DummyPlugin1(String uniqueName, String name, File pluginLogo, String description, String version) {
            super(uniqueName, name, pluginLogo, description, version);
        }
    }

    /**
     * Another dummy plugin for testing purposes
     */
    private static class DummyPlugin2 extends Plugin {

        public DummyPlugin2(String uniqueName, String name, File pluginLogo, String description, String version) {
            super(uniqueName, name, pluginLogo, description, version);
        }
    }

}
