package com.aurora.kernel;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.aurora.auroralib.ExtractedText;
import com.aurora.internalservice.internalcache.CachedProcessedFile;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.kernel.event.ListPluginsRequest;
import com.aurora.kernel.event.ListPluginsResponse;
import com.aurora.kernel.event.OpenCachedFileWithPluginRequest;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.kernel.event.RetrieveFileFromCacheRequest;
import com.aurora.kernel.event.RetrieveFileFromCacheResponse;
import com.aurora.plugin.Plugin;

import java.io.InputStream;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Communicator class that communicates to Aurora app environment
 */
public class AuroraCommunicator extends Communicator {
    private static final String CLASS_TAG = "AuroraCommunicator";


    public AuroraCommunicator(Bus mBus) {
        super(mBus);
    }

    /**
     * Open file with a given plugin. This method will first extract the text from the given file reference,
     * then it will send a request to let the plugin make the representation.
     *
     * @param fileRef      a reference to the file that needs to be opened
     * @param context      the android context
     */
    public void openFileWithPlugin(String fileRef, InputStream file, Context context) {
        // Create observable to listen to
        Observable<InternalProcessorResponse> internalProcessorResponse =
                mBus.register(InternalProcessorResponse.class);

        // Subscribe to observable
        // The subscribe will only be triggered after the file was processed internally
        internalProcessorResponse
                .map(InternalProcessorResponse::getExtractedText)
                .subscribe((ExtractedText extractedText) ->
                        sendOpenFileRequest(extractedText, context));

        // First create internal processing
        InternalProcessorRequest internalProcessorRequest = new InternalProcessorRequest(file, fileRef);

        // Post request on the bus
        mBus.post(internalProcessorRequest);
    }


    /**
     * Method to open an already cached file with the plugin
     *
     * @param fileRef      a reference to the file to open
     * @param context      the android context
     */
    public void openFileWithCache(String fileRef, Context context) {
        // Create observable to listen to
        Observable<RetrieveFileFromCacheResponse> retrieveFileFromCacheResponse =
                mBus.register(RetrieveFileFromCacheResponse.class);

        // Subscribe to observable
        retrieveFileFromCacheResponse
                .map(RetrieveFileFromCacheResponse::getProcessedFile)
                .map(CachedProcessedFile::getJsonRepresentation)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((String jsonRepresentation) -> {
                    if ("{}".equals(jsonRepresentation)) {
                        // TODO extract text and show plugin anyway

                        // Temporarily show toast
                        Toast.makeText(context, "File was not cached", Toast.LENGTH_LONG).show();

                        Log.d(CLASS_TAG, "Not implemented yet!");
                    } else {
                        sendOpenCachedFileRequest(jsonRepresentation, context);
                    }
                });

        // Send request to retrieve file from cache TODO change this!
        RetrieveFileFromCacheRequest request = new RetrieveFileFromCacheRequest(fileRef, "DummyPlugin");
        mBus.post(request);
    }

    /**
     * Gets a list of all the available plugins
     *
     * @return a list of basic information on every plugin wrapped in an observable
     */
    public Observable<List<Plugin>> getListOfPlugins() {
        Observable<ListPluginsResponse> mListPluginsResponse
                = this.mBus.register(ListPluginsResponse.class);
        this.mBus.post(new ListPluginsRequest());

        return mListPluginsResponse.map(ListPluginsResponse::getPlugins);
    }


    /**
     * Private handle method to send request to plugin communicator to open file with plugin
     *
     * @param extractedText the extracted text of the file that was internally processed
     * @param context       the android context
     */
    private void sendOpenFileRequest(ExtractedText extractedText, Context context) {

        // Create request and post it on bus
        OpenFileWithPluginRequest openFileWithPluginRequest =
                new OpenFileWithPluginRequest(extractedText, context);
        mBus.post(openFileWithPluginRequest);
    }

    /**
     * Private handle method to send request to plugin communicator to open an already cached file with plugin
     *
     * @param jsonRepresentation the representation of the object to represent
     * @param context            the android context
     */
    private void sendOpenCachedFileRequest(String jsonRepresentation, Context context) {
        // Create request and post it on bus

        OpenCachedFileWithPluginRequest openCachedFileWithPluginRequest =
                new OpenCachedFileWithPluginRequest(jsonRepresentation, context);
        mBus.post(openCachedFileWithPluginRequest);
    }
}
