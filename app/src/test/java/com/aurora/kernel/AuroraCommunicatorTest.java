package com.aurora.kernel;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.aurora.kernel.event.ListPLuginsResponse;
import com.aurora.kernel.event.OpenFileWithPluginResponse;
import com.aurora.kernel.event.PluginSettingsResponse;
import com.aurora.plugin.BasicPlugin;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;


public class AuroraCommunicatorTest {

    private static Bus mBus;
    private static AuroraCommunicator mAuroraCommunicator;

    @BeforeClass
    public static void initialize() {
        mBus = new Bus();
        mAuroraCommunicator = new AuroraCommunicator(mBus);
    }

    @Test
    public void AuroraCommunicator_openFileWithPlugin_shouldReturnPluginFragment() {
        // Create dummy arguments
        String dummyPluginName = "Dummyplugin";
        String fileRef = "/path/to/file";

        // Create test observer to subscribe to the observable
        TestObserver<Fragment> subscriber = new TestObserver<>();

        // Call the method
        Observable<Fragment> fragmentObservable = mAuroraCommunicator.openFileWithPlugin(dummyPluginName, fileRef);

        // Make dummy fragment and response
        Fragment dummyFragment = new DummyFragment();
        OpenFileWithPluginResponse response = new OpenFileWithPluginResponse(dummyFragment);

        // Subscribe to observable and assert fragment is what expected
        fragmentObservable.subscribe(subscriber);

        // Post response
        mBus.post(response);

        // Assert values
        subscriber.assertSubscribed();
        subscriber.assertValue(dummyFragment);
    }

    @Test
    public void AuroraCommunicator_getSettingsOfPlugin_shouldReturnSettingsActivityClass() {
        // Create dummy argument
        String dummyPluginName = "Dummyplugin";

        // Create test observer to subscribe to the observable
        TestObserver<Class<? extends Activity>> activityObserver = new TestObserver<>();

        // Call the method under test
        Observable<Class<? extends Activity>> activityObservable = mAuroraCommunicator.getSettingsOfPlugin(dummyPluginName);

        // Make dummy Class and response
        Class<? extends Activity> className = DummyActivity.class;
        PluginSettingsResponse response = new PluginSettingsResponse(className);

        // Subscribe to observable and assert that activity is what expected
        activityObservable.subscribe(activityObserver);

        // Post response
        mBus.post(response);

        // Assert values
        activityObserver.assertSubscribed();
        activityObserver.assertValue(className);
    }

    @Test
    public void AuroraCommunicator_getListOfPLugins_shouldReturnListOfPLugins() {
        // Create dummy arguments
        String pluginName = "DummyPlugin";

        // Create observer to subscribe to observable
        TestObserver<List<BasicPlugin>> observer = new TestObserver<>();

        // Call the method under test
        Observable<List<BasicPlugin>> listObservable = mAuroraCommunicator.getListOfPlugins();

        // Make dummy list
        List<BasicPlugin> basicPluginList = new ArrayList<>();

        // Add fake basic plugin
        basicPluginList.add(new BasicPlugin(pluginName, null));

        // Make response containing the list
        ListPLuginsResponse response = new ListPLuginsResponse(basicPluginList);

        // Subscribe to observable and assert that list is what expected
        listObservable.subscribe(observer);

        // Post response
        mBus.post(response);

        // Assert values
        observer.assertSubscribed();
        observer.assertValue(basicPluginList);
    }

    /**
     * Private dummy activity class for testing purposes
     */
    private class DummyActivity extends Activity {
    }

    /**
     * Private dummy fragment class for testing purposes
     */
    private class DummyFragment extends Fragment {
    }
}
