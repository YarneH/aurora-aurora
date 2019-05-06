package com.aurora.plugin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.aurora.internalservice.internaltranslation.ITranslate;
import com.aurora.kernel.ContextNullException;
import com.aurora.kernel.Kernel;
import com.aurora.kernel.ProcessingCommunicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that handles translation requests
 */
public class TranslationService extends Service {

    /**
     * Required function for Service usage. Gets called by the Android platform.
     *
     * @param intent Service Intent for the cache service
     * @return a TranslationBinder to be used in the plugin
     */
    @Override
    public IBinder onBind(Intent intent) {
        return(new TranslationService.TranslationBinder());
    }

    /**
     * Binder for the service, makes use of AIDL file
     */
    private class TranslationBinder extends ITranslate.Stub {
        /**
         * Requests a translation operation to be executed by the processing communicator
         *
         * @param sentences             the list of strings to be translated
         * @param sourceLanguage        the language of the input sentences in ISO code
         * @param destinationLanguage   the desired language of the translations in ISO format
         * @return the list of translated sentences
         */
        @Override
        public List<String> translate(List<String> sentences, String sourceLanguage, String destinationLanguage) {
            Log.d("AURORA_TRANSLATE", "SERVICE IS BEING RUN FOR:" + sentences + "\n" +
                    sourceLanguage  + "\n" + destinationLanguage);

            // Get the kernel and appropriate communicator
            try {
                ProcessingCommunicator processingCommunicator = Kernel.getInstance(
                        TranslationService.this.getApplicationContext()).getProcessingCommunicator();

                return processingCommunicator.translateSentences(sentences, sourceLanguage, destinationLanguage);
            } catch (ContextNullException e) {
                Log.e("TranslationService", "The kernel was not initialized with a valid context", e);
                return new ArrayList<>();
            }
        }
    }
}
