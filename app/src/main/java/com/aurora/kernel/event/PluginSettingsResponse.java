package com.aurora.kernel.event;

import android.app.Activity;

/**
 * Event to respond with the settings of a plugin
 */
public class PluginSettingsResponse extends Event {
    private Class<? extends Activity> mActivity;

    public PluginSettingsResponse(Class<? extends Activity> activity) {
        mActivity = activity;
    }

    public Class<? extends Activity> getActivity() {
        return mActivity;
    }
}
