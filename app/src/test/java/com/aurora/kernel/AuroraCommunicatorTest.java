package com.aurora.kernel;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.aurora.kernel.event.ListPLuginsResponse;
import com.aurora.kernel.event.OpenFileWithPluginResponse;
import com.aurora.kernel.event.PluginSettingsResponse;
import com.aurora.plugin.BasicPlugin;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

import static org.junit.Assert.assertEquals;

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
        String dummyPluginName = "Dummyplugin";
        String fileRef = "/path/to/file";

        Observable<Fragment> fragmentObservable =  mAuroraCommunicator.openFileWithPlugin(dummyPluginName, fileRef);

        // Make dummy fragment and response
        Fragment dummyFragment = new Fragment();
        OpenFileWithPluginResponse response = new OpenFileWithPluginResponse(dummyFragment);

        // Subscribe to observable and assert fragment is what expected
        fragmentObservable.subscribe(fragment -> assertEquals(dummyFragment, fragment));

        // Post response
        mBus.post(response);
    }

    @Test
    public void AuroraCommunicator_getSettingsOfPlugin_shouldReturnSettingsActivityClass() {
        String dummyPluginName = "Dummyplugin";

        Observable<Class<? extends Activity>> activityObservable = mAuroraCommunicator.getSettingsOfPlugin(dummyPluginName);

        // Make dummy fragment and response
        Class<? extends Activity> className = Activity.class;
        PluginSettingsResponse response = new PluginSettingsResponse(className);

        // Subscribe to observable and assert that activity is what expected
        activityObservable.subscribe(aClass -> assertEquals(className, aClass));

        // Post response
        mBus.post(response);
    }

    @Test
    public void AuroraCommunicator_getListOfPLugins_shouldReturnListOfPLugins() {
        String dummyPLuginName = "Dummyplugin";

        Observable<List<BasicPlugin>> listObservable = mAuroraCommunicator.getListofPlugins();

        // Make dummy list and response
        List<BasicPlugin> basicPluginList = new ArrayList<>();
        ListPLuginsResponse response = new ListPLuginsResponse(basicPluginList);

        // Subscribe to observable and assert that list is what expected
        listObservable.subscribe(basicPlugins -> assertEquals(basicPluginList, basicPlugins));

        // Post response
        mBus.post(response);
    }
}
