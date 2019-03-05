package com.aurora.externalservice;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * TODO maybe this should be an abstract class. This way we can make sure that the class takes an
 * PluginCommunicator in the constructor. We could also define an final method for the callback
 * that subscribes on the observable and calls an abstract method render(Type that we want).
 */


/**
 * Interface that needs to be implemented by every PluginEnvironment.
 * Instantiated classes will define the Android Fragments for the representation of the file.
 */
public interface PluginEnvironment {

    /**
     * This method will be called by the PluginCommunicator to get an Android Activity where the
     * settings can be changed.
     *
     * @return an activity that contains all the settings of the plugin that can be changed
     */
    Class<? extends Activity> getSettingsActivity();

    /**
     * This method will be called by the PluginCommunicator to receive a view of the file generated
     * by the plugin. It is recommended that the plugin calls
     * PluginCommunicator::processFileWithPluginProcessor(...) to do the processing.
     *
     * @param fileRef a reference to the file that needs to be opened
     * @return an Android Fragment that contains the full view of the plugin, this Fragment will be
     * loaded into the Aurora UI.
     */
    Fragment openFile(String fileRef);
}
