package com.aurora.internalservice.internalprocessor;

import android.util.Log;

import com.aurora.internalservice.InternalService;

/**
 * Class that takes care of internal processing of file (such as text extraction)
 */
public class InternalTextProcessing implements InternalService {

    // TODO: return type may change
    /**
     * extracts text from a file
     *
     * @param fileRef a reference to where the file can be found
     */
    @Override
    public void processFile(String fileRef) {
        Log.d("InternalTextProcessing", "Not implemented yet!");
    }
}
