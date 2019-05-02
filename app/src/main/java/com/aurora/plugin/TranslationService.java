package com.aurora.plugin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.aurora.auroralib.cache.CacheResults;
import com.aurora.auroralib.translation.TranslationErrorCodes;
import com.aurora.auroralib.translation.TranslationResult;
import com.aurora.internalservice.internaltranslation.ITranslate;
import com.aurora.kernel.ContextNullException;
import com.aurora.kernel.Kernel;
import com.aurora.kernel.ProcessingCommunicator;
import com.aurora.kernel.event.TranslationResponse;

import java.util.List;

import io.reactivex.Observable;

public class TranslationService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return(new TranslationService.TranslationBinder());
    }



    private class TranslationBinder extends ITranslate.Stub {

        @Override
        public List<String> translate(List<String> sentences, String sourceLanguage, String destinationLanguage) {
            Log.d("AURORA_TRANSLATE", "SERVICE IS BEING RUN FOR:" + sentences + "\n" +
                    sourceLanguage  + "\n" + destinationLanguage);

            // Get the kernel and appropriate communicator
            Kernel kernel = null;
            try {
                kernel = Kernel.getInstance(TranslationService.this.getApplicationContext());


                ProcessingCommunicator processingCommunicator = kernel.getProcessingCommunicator();

                return processingCommunicator.translateSentences(sentences, sourceLanguage, destinationLanguage);
            } catch (ContextNullException e) {
                Log.e("CacheService", "The kernel was not initialized with a valid context", e);
                return null;
                //return new TranslationResult(TranslationErrorCodes.KERNEL_FAIL, null);
            }
        }
    }
}
