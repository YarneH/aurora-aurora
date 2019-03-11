package com.aurora.kernel;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.aurora.aurora.NotFoundActivity;
import com.aurora.aurora.NotFoundFragment;
import com.aurora.externalservice.PluginEnvironment;
import com.aurora.internalservice.internalprocessor.ExtractedText;
import com.aurora.kernel.event.ListPluginsRequest;
import com.aurora.kernel.event.ListPluginsResponse;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.kernel.event.OpenFileWithPluginResponse;
import com.aurora.kernel.event.PluginProcessorResponse;
import com.aurora.kernel.event.PluginSettingsRequest;
import com.aurora.kernel.event.PluginSettingsResponse;
import com.aurora.plugin.BasicProcessedText;
import com.aurora.plugin.Plugin;
import com.aurora.plugin.ProcessedText;
import com.aurora.processingservice.PluginProcessor;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

public class PluginCommunicatorTest {

    private static Bus mBus;
    private static ProcessingCommunicator mProcessingCommunicator;
    private static PluginRegistry mPluginRegistry;
    private static PluginCommunicator mPluginCommunicator;
    private static final String PLUGINS_CFG = "plugins.cfg";
    private static final String PLUGIN_NAME = "DummyPlugin";
    private static final String PLUGIN_NOT_IN_REGISTRY = "You have found the candy, congratulations!";
    private static final String FILE_PATH = "/path/to/file";

    private static Plugin mPlugin;

    private static Fragment mDummyFragment;

    @BeforeClass
    public static void initialize() {
        mBus = new Bus();

        mProcessingCommunicator = new ProcessingCommunicator(mBus);
        mPluginRegistry = new PluginRegistry(mProcessingCommunicator, PLUGINS_CFG);
        mPluginCommunicator = new PluginCommunicator(mBus, mPluginRegistry);

        mDummyFragment = new DummyFragment();
    }

    /**
     * Helper method to add a dummy plugin to the registry
     */
    private void addPluginToRegistry() {
        // Clear registry
        mPluginRegistry.removeAllPlugins();

        // Create name and description
        String uniqueName = "DummyPlugin";
        String pluginName = "DummyPlugin";
        String description = "This is a dummy description.";
        String version = "0.1";
        // Create environment and processor
        PluginEnvironment environment = new DummyPluginEnvironment(mPluginCommunicator, DummyActivity.class);
        PluginProcessor processor = new DummyPluginProcessor(mProcessingCommunicator);

        // Create plugin using environment and processor
        mPlugin = new DummyPlugin(uniqueName, pluginName, null, description, version,
                environment, processor);

        // Register plugin in registry
        mPluginRegistry.registerPlugin(PLUGIN_NAME, mPlugin);
    }

    @Test
    public void PluginCommunicator_processFileWithPlugin_shouldReturnProcessedFile() {
        // Create dummy arguments
        PluginProcessor processor = new DummyPluginProcessor(mProcessingCommunicator);

        // Create test observer to subscribe to the observable
        TestObserver<ProcessedText> observer = new TestObserver<>();

        // Call method under test
        Observable<ProcessedText> processedTextObservable = mPluginCommunicator.processFileWithPluginProcessor(processor, FILE_PATH);

        // Create dummy processed text
        String title = "Title";
        List<String> paragraphs = Arrays.asList("Paragraph 1", "Paragraph 2", "Paragraph 3");
        ProcessedText text = new BasicProcessedText(title, paragraphs);

        // Create response
        PluginProcessorResponse response = new PluginProcessorResponse(text);

        // Subscribe to observable
        processedTextObservable.subscribe(observer);

        // Post response
        mBus.post(response);

        // Assert values
        observer.assertSubscribed();
        observer.assertValue(text);
    }


    @Test
    public void PluginCommunicator_PluginSettingsObservable_shouldPostSettingsResponse() {
        addPluginToRegistry();

        // Create test observer to subscribe to the observable
        TestObserver<Class<? extends Activity>> observer = new TestObserver<>();

        // Register for PluginSettingsResponse events
        Observable<PluginSettingsResponse> observable = mBus.register(PluginSettingsResponse.class);

        // Subscribe to observable
        observable.map(PluginSettingsResponse::getActivity).subscribe(observer);

        // Post request event
        mBus.post(new PluginSettingsRequest(PLUGIN_NAME));

        // Assert values
        observer.assertSubscribed();
        observer.assertValue(DummyActivity.class);
    }

    @Test
    public void PluginCommunicator_PluginSettingsObservable_shouldPostResponseContainingNotFoundActivity() {
        addPluginToRegistry();

        // Create test observer to subscribe to the observable
        TestObserver<Class<? extends Activity>> observer = new TestObserver<>();

        // Register for PluginSettingsResponse events
        Observable<PluginSettingsResponse> observable = mBus.register(PluginSettingsResponse.class);

        // Subscribe to observable
        observable.map(PluginSettingsResponse::getActivity).subscribe(observer);

        // Post request event
        mBus.post(new PluginSettingsRequest(PLUGIN_NOT_IN_REGISTRY));

        // Assert values
        observer.assertSubscribed();
        observer.assertValue(NotFoundActivity.class);

    }

    @Test
    public void PluginCommunicator_OpenFileWithPluginRequest_shouldPostResponseEvent() {
        addPluginToRegistry();

        // Create test observer to subscribe to observable
        TestObserver<Fragment> observer = new TestObserver<>();

        // Register for OpenFileWithPluginResponse events
        Observable<OpenFileWithPluginResponse> observable = mBus.register(OpenFileWithPluginResponse.class);

        // Subscribe to observable
        observable.map(OpenFileWithPluginResponse::getPluginFragment).subscribe(observer);

        // Post request event
        mBus.post(new OpenFileWithPluginRequest(PLUGIN_NAME, FILE_PATH));

        // Assert values
        observer.assertSubscribed();
        observer.assertValue(mDummyFragment);
    }

    @Test
    public void PluginCommunicator_OpenFileWithPluginRequest_shouldPostResponseEventContainingNotFoundFragment() {
        addPluginToRegistry();

        // Create test observer to subscribe to observable
        TestObserver<Class<? extends Fragment>> observer = new TestObserver<>();

        // Register for OpenFileWithPluginResponse events
        Observable<OpenFileWithPluginResponse> observable = mBus.register(OpenFileWithPluginResponse.class);

        // Subscribe to observable
        observable.map(openFileWithPluginResponse -> openFileWithPluginResponse.getPluginFragment().getClass())
                .subscribe(observer);

        // Post request event
        mBus.post(new OpenFileWithPluginRequest(PLUGIN_NOT_IN_REGISTRY, FILE_PATH));

        // Assert values
        observer.assertSubscribed();
        observer.assertValue(NotFoundFragment.class);
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
        observer.assertValue(mPlugin.getBasicPluginInfo().getName());
    }


    /**
     * Dummy plugin processor for testing purposes
     */
    private class DummyPluginProcessor extends PluginProcessor {

        public DummyPluginProcessor(ProcessingCommunicator processingCommunicator) {
            super(processingCommunicator);
        }

        @Override
        public ProcessedText processFileWithPluginProcessor(String fileRef) {
            return null;
            //TODO
        }

        @Override
        protected void resultProcessFileWithAuroraProcessor(ExtractedText extractedText) {
            //TODO
        }
    }

    /**
     * Dummy plugin class for testing purposes
     */
    private class DummyPlugin extends Plugin {

        public DummyPlugin(String uniqueName, String name, File pluginLogo, String description, String version,
                           PluginEnvironment pluginEnvironment, PluginProcessor pluginProcessor) {
            super(uniqueName, name, pluginLogo, description, version, pluginEnvironment, pluginProcessor);
        }
    }

    /**
     * Dummy plugin environment for testing purposes
     */
    private class DummyPluginEnvironment extends PluginEnvironment {

        public DummyPluginEnvironment(PluginCommunicator pluginCommunicator, Class<? extends Activity> pluginSettingsActivity) {
            super(pluginCommunicator, pluginSettingsActivity);
        }

        @Override
        public Fragment openFile(String fileRef) {
            return mDummyFragment;
        }

        @Override
        protected void resultProcessFileWithPluginProcessor(ProcessedText processedText) {

        }

    }

    /**
     * Dummy activity for testing purposes
     */
    private class DummyActivity extends Activity {
    }

    /**
     * Dummy fragment for testing purposes
     */
    private static class DummyFragment extends Fragment {
    }

}
