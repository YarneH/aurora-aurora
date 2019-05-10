package com.aurora.auroralib.translation;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aurora.auroralib.ServiceCaller;
import com.aurora.internalservice.internaltranslation.ITranslate;

import java.util.ArrayList;
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
    @Override
    protected void disconnect() {
        mServiceConnected = false;
        mTranslateBinding = null;
        Log.d(LOG_TAG, "Plugin Unbound");
    }

    /**
     * A private thread class that will cache the file in another thread to avoid blocking of the main thread
     */
    private class TranslateThread extends Thread {
        private static final int MAX_REQUEST_SIZE = 1000;

        /**
         * Translated sentences
         */
        private List<String> mTranslatedSentences = new ArrayList<>();

        /**
         * Sentences to be translated
         */
        private List<String> mSentences;

        /**
         * Language of original sentences
         */
        private String mSourceLanguage;

        /**
         * Language to translate to
         */
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

        /**
         * Waits in case the binding is not ready and executes the translation operation
         */
        @Override
        public void run() {
            Log.i(LOG_TAG, "translation called");
            try {

                if (mTranslateBinding == null) {
                    synchronized (mMonitor) {
                        Log.v(LOG_TAG, "Entering sync block" + mTranslatedSentences);
                        mTranslatedSentences = translateAfterConnected();
                    }
                } else {
                    // Translate big requests in different parts:
                    mTranslatedSentences = executeTranslateRequest();
                }


            } catch (RemoteException e) {
                Log.e(getClass().getSimpleName(), "Exception requesting translation", e);
                mTranslatedSentences = null;
            }
        }


        /**
         * Helper method that waits until the translation service is connected if necessary
         *
         * @return                  The list of translated sentences
         * @throws RemoteException
         */
        private List<String> translateAfterConnected() throws RemoteException {
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
                // Translate big requests in different parts:
                return executeTranslateRequest();
            }
        }


        /**
         * Helper method that executes the full translate request, consisting of splitting the
         * request and then executing the smaller requests and concatenating their results
         *
         * @return                  The list of all translated sentences
         * @throws RemoteException
         */
        private List<String> executeTranslateRequest() throws RemoteException {
            List<String> translatedSentences = new ArrayList<>();

            List<List<String>> smallerRequestList = createSmallerRequests(mSentences);
            for (List<String> requestSentences : smallerRequestList){
                Log.d(LOG_TAG, "Small request: " + requestSentences);
                translatedSentences.addAll(mTranslateBinding.translate(requestSentences,
                        mSourceLanguage, mDestinationLanguage));
            }

            Log.d(LOG_TAG, "" + translatedSentences);
            return translatedSentences;
        }

        /**
         * Helper method to split a big translate request
         *
         * @param allSentences  List of all sentences to be translated
         * @return              List of smaller lists of sentences, with total character length
         *                      around MAX_REQUEST_SIZE
         */
        private List<List<String>> createSmallerRequests(List<String> allSentences){
            List<List<String>> smallerRequestList = new ArrayList<>();

            int currentRequestSize = 0;
            List<String> currentRequestList = new ArrayList<>();
            for (String sentence : allSentences){
                currentRequestSize += sentence.length();
                Log.d("CURRENT_REQUEST_SIZE", "" + currentRequestSize);
                currentRequestList.add(sentence);

                if (currentRequestSize > MAX_REQUEST_SIZE){
                    Log.d("TOO_BIG_REQUEST_SIZE", "" + currentRequestSize);
                    Log.d("CURRENT_REQUEST", "" + currentRequestList);
                    smallerRequestList.add(currentRequestList);
                    currentRequestList = new ArrayList<>();
                    currentRequestSize = 0;
                }
            }
            if (!currentRequestList.isEmpty()){
                smallerRequestList.add(currentRequestList);
            }
            return smallerRequestList;
        }

    }
}
