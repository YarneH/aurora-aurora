package com.aurora.kernel;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.aurora.aurora.R;
import com.aurora.auroralib.ExtractedText;
import com.aurora.internalservice.internalcache.CachedFileInfo;
import com.aurora.internalservice.internalcache.CachedProcessedFile;
import com.aurora.kernel.event.DocumentNotSupportedEvent;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.kernel.event.OpenCachedFileWithPluginRequest;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.kernel.event.QueryCacheRequest;
import com.aurora.kernel.event.QueryCacheResponse;
import com.aurora.kernel.event.RetrieveFileFromCacheRequest;
import com.aurora.kernel.event.RetrieveFileFromCacheResponse;
import com.aurora.kernel.event.UpdateCachedFileDateRequest;
import com.aurora.plugin.Plugin;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.apache.xpath.operations.Bool;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Communicator class that communicates to Aurora app environment
 */
public class AuroraCommunicator extends Communicator {
    /**
     * Tag for logging purposes
     */
    private static final String CLASS_TAG = "AuroraCommunicator";

    /**
     * The android application context
     */
    private Context mContext;
    /**
     * Keeps track of the starting timestamp of processing.
     */
    private long mStartTime;
    /**
     * Indicates whether or not the text is being extracted.
     */
    private MutableLiveData<Boolean> mLoading;

    /**
     * Observable keeping track of events indicating that a document is not supported
     */
    private Observable<DocumentNotSupportedEvent> mDocumentNotSupportedEventObservable;

    /**
     * Creates an AuroraCommunicator. There should be only one AuroraCommunicator at a time
     *
     * @param bus                A reference to the unique bus instance over which
     *                           the communicators will communicate events
     * @param applicationContext the android context
     */
    public AuroraCommunicator(@NonNull final Bus bus, @NonNull final Context applicationContext) {
        super(bus);

        mContext = applicationContext;
        mLoading = new MutableLiveData<>();
        mLoading.postValue(false);

        // Register for incoming events
        mDocumentNotSupportedEventObservable = mBus.register(DocumentNotSupportedEvent.class);

        // Call right method when event comes in
        mDocumentNotSupportedEventObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> showDocumentNotSupportedMessage(event.getReason()),
                        error -> Log.e(CLASS_TAG, "Something went wrong showing the message", error));
    }

    /**
     * Open file with a given plugin. This method will first extract
     * the text from the given file reference,
     * then it will send a request to let the plugin make the representation.
     *
     * @param fileRef  a reference to the file that needs to be opened
     * @param fileType the file type
     * @param file     the input stream of the file
     * @param plugin   the plugin to open the file with.
     */
    @SuppressLint("CheckResult")
    public void openFileWithPlugin(String fileRef, String fileType, InputStream file,
                                   Plugin plugin) {

        // mark starting time
        mStartTime = System.currentTimeMillis();
        // Set the state to loading.
        mLoading.postValue(true);

        // Register observable
        Observable<InternalProcessorResponse> internalProcessorResponseObservable =
                mBus.register(InternalProcessorResponse.class);
        // Subscribe to observable
        // The subscribe will only be triggered after the file was processed internally
        internalProcessorResponseObservable
                .map(InternalProcessorResponse::getExtractedText)
                .take(1)
                .subscribe((ExtractedText extractedText) -> {
                            Bundle params = new Bundle();
                            params.putInt("extracted_text_length", extractedText.toString().length());
                            params.putLong("processing_time", System.currentTimeMillis() - mStartTime);
                            FirebaseAnalytics.getInstance(mContext).logEvent("processing_performance", params);
                            mLoading.postValue(false);
                            sendOpenFileRequest(extractedText, plugin.getUniqueName());
                        }
                        , (Throwable e) ->
                                Log.e(CLASS_TAG,
                                        "Something went wrong when receiving the internally processed file.", e)
                );


        InternalProcessorRequest internalProcessorRequest =
                new InternalProcessorRequest(fileRef, fileType, file, plugin.getInternalServices());

        // Post request on the bus
        mBus.post(internalProcessorRequest);
    }


    /**
     * Method to open an already cached file with the plugin
     *
     * @param fileRef          a reference to the file to open
     * @param uniquePluginName the name of the plugin that the file was processed with
     */
    @SuppressLint("CheckResult")
    public void openFileWithCache(String fileRef, String uniquePluginName) {
        // Create observable to listen to
        Observable<RetrieveFileFromCacheResponse> retrieveFileFromCacheResponse =
                mBus.register(RetrieveFileFromCacheResponse.class);

        // Subscribe to observable
        retrieveFileFromCacheResponse
                .map(RetrieveFileFromCacheResponse::getProcessedFile)
                .observeOn(AndroidSchedulers.mainThread())
                .take(1)
                .subscribe((CachedProcessedFile processedFile) -> {
                            if ("{}".equals(processedFile.getJsonRepresentation())) {
                                Toast.makeText(mContext,
                                        mContext.getString(R.string.cached_file_not_found_processing_file),
                                        Toast.LENGTH_LONG).show();
                                // TODO: change this such that it processes the original file

                            } else {
                                sendOpenCachedFileRequest(processedFile.getJsonRepresentation(),
                                        processedFile.getFileRef(), processedFile.getUniquePluginName());
                            }
                        }, (Throwable e) ->
                                Log.e(CLASS_TAG, "Something went wrong while retrieving a file from the cache!", e)
                );

        RetrieveFileFromCacheRequest request = new RetrieveFileFromCacheRequest(fileRef, uniquePluginName);
        mBus.post(request);
    }

    /**
     * Gets a list of all cached files, ordered on date in ascending order
     *
     * @param maxLength The maximum number of entries that should be returned, or <= 0 if you want all files
     * @param observer  an observer instance containing code that will be executed when the result comes in
     */
    public void getListOfCachedFiles(final int maxLength,
                                     @NonNull final Observer<List<CachedFileInfo>> observer) {
        // Create observable to listen to
        Observable<QueryCacheResponse> queryCacheResponseObservable = mBus.register(QueryCacheResponse.class);

        queryCacheResponseObservable
                .map(QueryCacheResponse::getResults)
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

        // Create request and post on bus
        QueryCacheRequest request = new QueryCacheRequest(maxLength);
        mBus.post(request);

    }

    /**
     * Private handle method to send request to plugin communicator to open file with plugin
     *
     * @param extractedText    the extracted text of the file that was internally processed
     * @param uniquePluginName the (unique) name of the plugin to open the file with
     */
    private void sendOpenFileRequest(final ExtractedText extractedText, final String uniquePluginName) {

        // Create request and post it on bus
        OpenFileWithPluginRequest openFileWithPluginRequest =
                new OpenFileWithPluginRequest(extractedText, uniquePluginName, mContext);
        mBus.post(openFileWithPluginRequest);
    }


    /**
     * Private handle method to send request to plugin communicator to open an already cached file with plugin.
     * It will also update the dateLastOpened of the cached file in the cache
     *
     * @param jsonRepresentation the representation of the object to represent
     * @param uniquePluginName   the name of the plugin that the file was processed with
     */
    private void sendOpenCachedFileRequest(@NonNull final String jsonRepresentation, @NonNull final String fileRef,
                                           @NonNull final String uniquePluginName) {
        // Create request and post it on bus
        OpenCachedFileWithPluginRequest openCachedFileWithPluginRequest =
                new OpenCachedFileWithPluginRequest(jsonRepresentation, uniquePluginName, mContext);
        mBus.post(openCachedFileWithPluginRequest);

        // Create request to update the dateLastOpened of the file
        UpdateCachedFileDateRequest updateCachedFileDateRequest =
                new UpdateCachedFileDateRequest(fileRef, uniquePluginName, new Date());
        mBus.post(updateCachedFileDateRequest);
    }

    /**
     * Shows a message indicating that a document could not be processed
     *
     * @param reason the reason why the document could not be processed
     */
    private void showDocumentNotSupportedMessage(@NonNull final String reason) {
        Toast.makeText(mContext, reason, Toast.LENGTH_LONG).show();
        mLoading.postValue(false);
    }

    /**
     * Getter for the loading state LiveData.
     *
     * @return LiveData that changes when the loading state changes
     */
    public MutableLiveData<Boolean> getLoadingData() {
        return mLoading;
    }
}
