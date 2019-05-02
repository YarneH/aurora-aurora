package com.aurora.auroralib.translation;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aurora.auroralib.ServiceCaller;
import com.aurora.internalservice.internaltranslation.ITranslate;

import java.util.List;

public class TranslationServiceCaller extends ServiceCaller {
    /**
     * Tag used for log messages
     */
    private static final String LOG_TAG = TranslationServiceCaller.class.getSimpleName();

    /**
     * Binding to the remote interface
     */
    private ITranslate mTranslateBinding = null;

    public TranslationServiceCaller(Context context){
        super(context);
    }

    /**
     * Tries to translate a list of strings in aurora through the service
     *
     * @param sentences             the list of strings to be translated
     * @param sourceLanguage        the language of the input sentences in ISO code
     * @param destinationLanguage   the desired language of the translations in ISO format
     * @return the list of translated sentences
     */
    public List<String> translateOperation(@NonNull List<String> sentences, String sourceLanguage,
                                  @NonNull String destinationLanguage ) {
        //TODO
        synchronized (mMonitor) {
            List<String> translatedSentences = null;
            bindService(ITranslate.class, LOG_TAG);
            try {
                while (!mServiceConnected) {
                    mMonitor.wait();
                }

                translatedSentences = translate(sentences, sourceLanguage, destinationLanguage);

                unbindService();
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, "translateOperation was interrupted!", e);

                // Restore the interrupted state:
                // https://www.ibm.com/developerworks/java/library/j-jtp05236/index.html?ca=drs-#2.1
                Thread.currentThread().interrupt();
            }

            return translatedSentences;
        }
    }


    /**
     * Will start a new thread to cache the file
     *
     * @param sentences             the list of strings to be translated
     * @param sourceLanguage        the language of the input sentences in ISO code
     * @param destinationLanguage   the desired language of the translations in ISO format
     * @return status code of the cache operation from Cache Service in Aurora Internal Services
     */
    private List<String> translate(@NonNull List<String> sentences, String sourceLanguage,
                                   @NonNull String destinationLanguage ) {
        TranslationServiceCaller.TranslateThread translateThread =
                new TranslationServiceCaller.TranslateThread(sentences, sourceLanguage, destinationLanguage);
        translateThread.start();
        try {
            translateThread.join();
        } catch (InterruptedException e) {
            Log.e(getClass().getSimpleName(), "Exception requesting translation", e);

            // Restore the interrupted state:
            // https://www.ibm.com/developerworks/java/library/j-jtp05236/index.html?ca=drs-#2.1
            Thread.currentThread().interrupt();
        }
        return translateThread.getTranslatedSentences();
    }

    /**
     * This function will be called by the android system
     *
     * @param className
     * @param binder    Finishes the binding process
     */
    @Override
    public void onServiceConnected(ComponentName className, IBinder binder) {
        synchronized (mMonitor) {
            mTranslateBinding = ITranslate.Stub.asInterface(binder);
            Log.d(LOG_TAG, "Plugin Bound");

            mServiceConnected = true;
            mMonitor.notifyAll();
        }
    }


    /**
     * Release the binding
     */
    protected void disconnect() {
        mServiceConnected = false;
        mTranslateBinding = null;
        Log.d(LOG_TAG, "Plugin Unbound");
    }

    /**
     * A private thread class that will cache the file in another thread to avoid blocking of the main thread
     */
    private class TranslateThread extends Thread {
        private List<String> mTranslatedSentences = null;
        private List<String> mSentences;
        private String mSourceLanguage;
        private String mDestinationLanguage;

        protected TranslateThread(List<String> sentences, String sourceLanguage,
                                    String destinationLanguage) {
            mSentences = sentences;
            mSourceLanguage = sourceLanguage;
            mDestinationLanguage = destinationLanguage;
        }

        protected List<String> getTranslatedSentences() {
            return mTranslatedSentences;
        }

        @Override
        public void run() {
            Log.d(LOG_TAG, "translation called");
            try {

                if (mTranslateBinding == null) {
                    synchronized (mMonitor) {
                        Log.d(LOG_TAG, "Entering sync block" + mTranslatedSentences);

                        mTranslatedSentences = translate();
                    }
                } else {
                    mTranslatedSentences = mTranslateBinding.translate(mSentences, mSourceLanguage,
                            mDestinationLanguage);
                    Log.d(LOG_TAG, "" + mTranslatedSentences);
                }


            } catch (RemoteException e) {
                Log.e(getClass().getSimpleName(), "Exception requesting translation", e);
                mTranslatedSentences = null;
            }
        }

        private List<String> translate() throws RemoteException {
            synchronized (mMonitor) {
                try {
                    while (!mServiceConnected) {
                        mMonitor.wait();
                    }
                } catch (InterruptedException e) {
                    Log.e(getClass().getSimpleName(), "Exception requesting translation", e);

                    // Restore the interrupted state:
                    // https://www.ibm.com/developerworks/java/library/j-jtp05236/index.html?ca=drs-#2.1
                    Thread.currentThread().interrupt();
                }
                List<String> translatedSentences = mTranslateBinding.translate(mSentences,
                        mSourceLanguage, mDestinationLanguage);
                Log.d(LOG_TAG, "" + translatedSentences);
                return translatedSentences;
            }
        }
    }
}
