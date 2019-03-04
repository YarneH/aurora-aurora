package com.aurora.kernel.event;

import android.support.v4.app.Fragment;

/**
 * Event to respond that a file has been opened with a plugin
 */
public class OpenFileWithPluginResponse extends Event {
    private Fragment mPluginFragment;

    public OpenFileWithPluginResponse(Fragment pluginFragment) {
        mPluginFragment = pluginFragment;
    }

    public Fragment getPluginFragment() {
        return mPluginFragment;
    }
}
