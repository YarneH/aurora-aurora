package com.aurora.externalservice;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.aurora.kernel.PluginCommunicator;
import com.aurora.plugin.ProcessedText;
import com.aurora.processingservice.PluginProcessor;

import io.reactivex.Observable;


/**
 * Abstract class that needs to be implemented by every PluginEnvironment.
 * Instantiated classes will define the Android Fragments for the representation of the file.
 */
public abstract class PluginEnvironment {

    private PluginCommunicator mPluginCommunicator;
    protected Class<? extends Activity> mPluginSettingsActivity;

    public PluginEnvironment(PluginCommunicator pluginCommunicator, Class<? extends Activity> pluginSettingsActivity) {
        this.mPluginCommunicator = pluginCommunicator;
        this.mPluginSettingsActivity = pluginSettingsActivity;
    }

    /**
     * This method will be called by the PluginCommunicator to get an Android Activity where the
     * settings can be changed.
     *
     * @return an activity that contains all the settings of the plugin that can be changed
     */
    public Class<? extends Activity> getSettingsActivity() {
        return mPluginSettingsActivity;
    }

    /**
     * This method will be called by the PluginCommunicator to receive a view of the file generated
     * by the plugin. It is recommended that the plugin calls
     * PluginCommunicator::processFileWithPluginProcessor(...) to do the processing.
     *
     * @param fileRef a reference to the file that needs to be opened
     * @return an Android Fragment that contains the full view of the plugin, this Fragment will be
     * loaded into the Aurora UI.
     */
    public abstract Fragment openFile(String fileRef);

    /**
     * Delegates work to a certain pluginProcessor, upon returning calls the
     * resultProcessFileWithPluginProcessor method
     *
     * @param pluginProcessor the pluginProcessor to process the file with
     * @param fileRef         a reference to the file to process
     */
    protected final void processFileWithPluginProcessor(PluginProcessor pluginProcessor, String fileRef) {
        Observable<ProcessedText> processedTextObservable =
                mPluginCommunicator.processFileWithPluginProcessor(pluginProcessor, fileRef);


        processedTextObservable.subscribe(this::resultProcessFileWithPluginProcessor);
    }

    /**
     * This method is automatically called upon receiving a response from
     * processFileWithPluginProcessor
     *
     * @param processedText the object returned by the pluginProcessor
     */
    protected abstract void resultProcessFileWithPluginProcessor(ProcessedText processedText);
}