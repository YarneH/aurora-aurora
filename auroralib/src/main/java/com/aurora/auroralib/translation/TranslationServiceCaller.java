package com.aurora.auroralib.translation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aurora.auroralib.cache.CacheResults;
import com.aurora.auroralib.cache.CacheServiceCaller;
import com.aurora.internalservice.internaltranslation.ITranslate;

import java.util.ArrayList;
import java.util.List;

public class TranslationServiceCaller implements ServiceConnection {
    /**
     * Tag used for log messages
     */
    private static final String LOG_TAG = TranslationServiceCaller.class.getSimpleName();

    /**
     * Binding to the remote interface
     */
    private ITranslate mBinding = null;

    // !!! Not sure yet if this is handled right by just passing an activity's context (See BasicPlugin_Old)
    private Context mAppContext;

    /**
     * Object used for synchronisation
     */
    private final Object monitor = new Object();

    /**
     * Boolean indicating if service is connected
     */
    private boolean mServiceConnected = false;

    public TranslationServiceCaller(Context context) {
        mAppContext = context;
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
        synchronized (monitor) {
            //int result = CacheResults.NOT_REACHED;
            List<String> translatedSentences = null;
            bindService();
            try {
                while (!mServiceConnected) {
                    monitor.wait();
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
     * Binds the service so that a call to the AIDL defined function cache(String) can be executed
     */
    private void bindService() {
        Intent implicit = new Intent(ITranslate.class.getName());
        List<ResolveInfo> matches = mAppContext.getPackageManager().queryIntentServices(implicit, 0);

        if (matches.isEmpty()) {
            Log.d(LOG_TAG, "No translation service found");
        } else if (matches.size() > 1) {
            Log.d(LOG_TAG, "Multiple translation services found");
        } else {
            Log.d(LOG_TAG, "1 translation service found");
            Intent explicit = new Intent(implicit);
            ServiceInfo svcInfo = matches.get(0).serviceInfo;
            ComponentName cn = new ComponentName(svcInfo.applicationInfo.packageName,
                    svcInfo.name);

            explicit.setComponent(cn);
            mAppContext.bindService(explicit, this, Context.BIND_AUTO_CREATE);
            Log.d(LOG_TAG, "Binding service");
        }
    }

    /**
     * Release the binding
     */
    private void unbindService() {
        mAppContext.unbindService(this);
        disconnect();
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
        synchronized (monitor) {
            mBinding = ITranslate.Stub.asInterface(binder);
            Log.d(LOG_TAG, "Plugin Bound");

            mServiceConnected = true;
            monitor.notifyAll();
        }
    }

    /**
     * This function is called by the android system if the service gets disconnected
     *
     * @param className
     */
    @Override
    public void onServiceDisconnected(ComponentName className) {
        disconnect();
    }

    /**
     * Release the binding
     */
    private void disconnect() {
        mServiceConnected = false;
        mBinding = null;
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

                if (mBinding == null) {
                    synchronized (monitor) {
                        Log.d(LOG_TAG, "Entering sync block" + mTranslatedSentences);

                        mTranslatedSentences = translate();
                    }
                } else {
                    mTranslatedSentences = mBinding.translate(mSentences, mSourceLanguage,
                            mDestinationLanguage);
                    Log.d(LOG_TAG, "" + mTranslatedSentences);
                }


            } catch (RemoteException e) {
                Log.e(getClass().getSimpleName(), "Exception requesting translation", e);
                mTranslatedSentences = null;
            }
        }

        private List<String> translate() throws RemoteException {
            synchronized (monitor) {
                try {
                    while (!mServiceConnected) {
                        monitor.wait();
                    }
                } catch (InterruptedException e) {
                    Log.e(getClass().getSimpleName(), "Exception requesting translation", e);

                    // Restore the interrupted state:
                    // https://www.ibm.com/developerworks/java/library/j-jtp05236/index.html?ca=drs-#2.1
                    Thread.currentThread().interrupt();
                }
                List<String> translatedSentences = mBinding.translate(mSentences,
                        mSourceLanguage, mDestinationLanguage);
                Log.d(LOG_TAG, "" + translatedSentences);
                return translatedSentences;
            }
        }
    }
}
