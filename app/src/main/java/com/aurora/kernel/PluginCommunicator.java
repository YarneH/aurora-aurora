package com.aurora.kernel;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.aurora.kernel.event.PluginEvent;
import com.aurora.kernel.event.PluginProcessorEvent;
import com.aurora.kernel.event.PluginSettingsEvent;
import com.aurora.plugin.PluginFragment;
import com.aurora.plugin.PluginProcessor;

import io.reactivex.Observable;

/**
 * Communicator that communicates with Plugin environments
 */
public class PluginCommunicator extends Communicator {
    private PluginFragment mPluginFragment;
    private PluginRegistry mPluginRegistry;

    private Observable<PluginEvent> pluginEventObservable;
    private Observable<PluginSettingsEvent> pluginSettingsEventsObservable;

    public PluginCommunicator(Bus bus, PluginRegistry pluginRegistry) {
        super(bus);

        this.mPluginRegistry = pluginRegistry;

        pluginEventObservable = mBus.register(PluginEvent.class);
        pluginEventObservable.subscribe((PluginEvent pluginEvent) -> openFileWithPlugin(pluginEvent.getPluginName(), pluginEvent.getFileRef()));

        pluginSettingsEventsObservable = mBus.register(PluginSettingsEvent.class);
        pluginSettingsEventsObservable.subscribe((PluginSettingsEvent pluginSettingsEvent) -> {
            getSettingsActivity(pluginSettingsEvent.getPluginName());
        });

    }

    /**
     * Requests settingActivity from a pluginFragment
     *
     * @return Class reference of the activity to open
     */
    private Class<Activity> getSettingsActivity(String pluginName) {
        Log.d("PluginCommunicator", "Not implemented yet!");
        return null;
    }

    private Fragment openFileWithPlugin(String pluginName, String fileRef) {
        Log.d("PluginCommunicator", "Not implemented yet!");
        return null;
    }

    public void processFileWithPluginProcessor(PluginProcessor pluginProcessor, String fileRef) {

        // TODO This event should be captured by processingcomm and internalcache, but only be replied once
        this.mBus.post(new PluginProcessorEvent(pluginProcessor, fileRef));

        Log.d("PluginCommunicator", "Not implemented yet!");
    }




}
