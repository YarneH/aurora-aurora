package com.aurora.kernel.event;

/**
 * Response event that returns if opening the plugin worked or failed
 */
public class OpenFileWithPluginResponse extends Event {
    private boolean mSuccess;

    public OpenFileWithPluginResponse(boolean success) {
        mSuccess = success;
    }

    public boolean isSuccess() {
        return mSuccess;
    }
}
