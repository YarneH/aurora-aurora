package com.aurora.market.data.network;

import android.app.DownloadManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.aurora.aurora.R;
import com.aurora.market.data.database.MarketPlugin;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A singleton structure that is responsible for all the data of the Plugin Marget
 * that is gather from online services
 */
public final class MarketNetworkDataSource {
    /**
     * The log tag of this class
     */
    private static final String LOG_TAG = MarketNetworkDataSource.class.getSimpleName();
    /**
     * One of the JSON keys
     */
    private static final String JSON_LOCATION_KEY = "apk_location";
    /**
     * One of the JSON keys
     */
    private static final String JSON_DESCRIPTION_KEY = "description";
    /**
     * One of the JSON keys
     */
    private static final String JSON_LOGO_KEY = "plugin_logo";
    /**
     * One of the JSON keys
     */
    private static final String JSON_NAME_KEY = "name";
    /**
     * One of the JSON keys
     */
    private static final String JSON_CREATOR_KEY = "creator";
    /**
     * One of the JSON keys
     */
    private static final String JSON_VERSION_KEY = "version_code";
    /**
     * One of the JSON keys
     */
    private static final String JSON_UNIQUE_NAME = "unique_name";
    /**
     * One of the JSON keys
     */
    private static final String JSON_INTERNAL_SERVICES_KEY = "internal_services";
    /**
     * The HTTP prefix of a url
     */
    private static final String URL_HTTP_PREFIX = "http://";
    /**
     * The HTTPS prefix of a url
     */
    private static final String URL_HTTPS_PREFIX = "https://";
    /**
     * The quality percentage for the compressing of the logo
     */
    private static final int COMPRESS_QUALITY = 100;
    /**
     * A lock for the data
     */
    private static final Object LOCK = new Object();
    /**
     * Singleton instance
     */
    private static MarketNetworkDataSource sInstance;
    /**
     * The context of the app
     */
    private final Context mContext;
    /**
     * Dispatcher for Firebase
     */
    private FirebaseJobDispatcher mDispatcher;
    /**
     * A list of all the available MarketPlugins
     */
    private static MutableLiveData<List<MarketPlugin>> mMarketPlugins = new MutableLiveData<>();

    /**
     * A list of the unique names of the downloading plugins
     */
    private List<String> mDownloadingPlugins = new ArrayList<>();

    private MarketNetworkDataSource(Context context) {
        mContext = context;
        Driver driver = new GooglePlayDriver(mContext);
        mDispatcher = new FirebaseJobDispatcher(driver);
    }

    /**
     * Get the singleton of this class
     */
    public static MarketNetworkDataSource getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MarketNetworkDataSource(context);
            }
        }
        return sInstance;
    }

    /**
     * Get the LiveData of the current saved MarketPlugins
     */
    public LiveData<List<MarketPlugin>> getCurrentMarketPlugins() {
        return mMarketPlugins;
    }

    /**
     * Get all the available MarketPlugins
     */
    public void fetchMarketPlugins() {
        new GetMarketPlugins().execute();
    }

    /**
     * Download the Plugin
     *
     * @param marketPlugin The MarketPlugin of which the Plugin should be downloaded
     */
    public void downloadMarketPlugin(MarketPlugin marketPlugin) {
        mDownloadingPlugins.add(marketPlugin.getPluginName());
        new DownloadAndInstallPluginTask().execute(marketPlugin);
    }

    /**
     * Returns whether the plugin is downloading or not
     *
     * @param marketPlugin The MarketPlugin which will be checked on downloading
     * @return a boolean, indicating whether the provided MarketPlugin is downloading
     */
    public boolean isDownloading(MarketPlugin marketPlugin) {
        return mDownloadingPlugins.contains(marketPlugin.getPluginName());
    }

    /**
     * Returns a JSONArray of all the MarketPlugins available on the Plugin Market Server
     */
    private static class GetMarketPlugins extends AsyncTask<Void, Void, JSONArray> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected JSONArray doInBackground(Void... strings) {
            try {
                URL marketPluginListURL = NetworkUtils.getMarketPluginURL();
                if (marketPluginListURL == null) {
                    return null;
                }

                // Get response
                String jsonPluginListResponse = NetworkUtils.getResponseFromHttpUrl(marketPluginListURL);

                // Parse response
                return new JSONArray(jsonPluginListResponse);
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getSimpleName()).log(Level.SEVERE, null, e);
            }

            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute(JSONArray pluginList) {
            if (pluginList != null) {
                try {
                    ArrayList<MarketPlugin> tempList = new ArrayList<>();

                    for (int i = 0; i < pluginList.length(); i++) {
                        JSONObject currentPlugin = pluginList.getJSONObject(i);
                        String imageLocation = currentPlugin.getString(JSON_LOGO_KEY);
                        String name = currentPlugin.getString(JSON_NAME_KEY);
                        String downloadLocation = currentPlugin.getString(JSON_LOCATION_KEY);
                        String description = currentPlugin.getString(JSON_DESCRIPTION_KEY);
                        String creator = currentPlugin.getString(JSON_CREATOR_KEY);
                        String version = currentPlugin.getString(JSON_VERSION_KEY);
                        String unique = currentPlugin.getString(JSON_UNIQUE_NAME);
                        byte[] logo = new GetMarketPluginLogo().execute(imageLocation).get();

                        MarketPlugin currentMarketPlugin =
                                new MarketPlugin(logo, name, description, creator, version, unique, downloadLocation);

                        tempList.add(currentMarketPlugin);
                    }

                    mMarketPlugins.postValue(tempList);

                } catch (Exception e) {
                    Logger.getLogger(this.getClass().getSimpleName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }

    /**
     * Get the Bitmap of an image via the provided link
     */
    private static class GetMarketPluginLogo extends AsyncTask<String, Void, byte[]> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected byte[] doInBackground(String... strings) {
            String url = strings[0];

            if (!url.contains(URL_HTTP_PREFIX) && !url.contains(URL_HTTPS_PREFIX)) {
                url = URL_HTTP_PREFIX + url;
            }
            try {
                URL downloadLink = new URL(url);
                Bitmap bitmap = NetworkUtils.getBitmapFromHttpUrl(downloadLink);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, stream);
                return stream.toByteArray();
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getSimpleName()).log(Level.FINE, null, e);
            }
            return new byte[0];
        }

    }

    /**
     * A AsyncTask that will download and install a plugin from the market
     */
    private class DownloadAndInstallPluginTask extends AsyncTask<MarketPlugin, Void, Void> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected Void doInBackground(MarketPlugin... marketPlugins) {
            MarketPlugin marketPlugin = marketPlugins[0];
            // Create request for android download manager
            Uri uri = Uri.parse(marketPlugin.getDownloadLink().toString());
            DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                    DownloadManager.Request.NETWORK_MOBILE);

            // Set title and description
            request.setTitle(marketPlugin.getPluginName() + ".apk");
            request.setDescription(mContext.getResources().getString(R.string.download_plugin));

            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

            // Set the destination for download file to a path within the application's external files directory
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                    marketPlugin.getPluginName() + ".apk");
            request.setMimeType("application/vnd.android.package-archive");

            // Save the downloadID for later
            long downloadID = downloadManager.enqueue(request);

            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (id == downloadID) {
                        context.unregisterReceiver(this);
                        Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);

                        // Build up the path to the downloaded plugin
                        String path = Environment.getExternalStorageDirectory() +
                                File.pathSeparator +
                                Environment.DIRECTORY_DOWNLOADS +
                                File.separator +
                                marketPlugin.getPluginName() +
                                ".apk";

                        // Get the URI of the downloaded apk and prepare intent
                        Uri apkURI = FileProvider.getUriForFile(context,
                                context.getApplicationContext().getPackageName() + ".provider",
                                new File(path));

                        // Check if the Uri really points to a file
                        installIntent.setDataAndType(apkURI, "application/vnd.android.package-archive");
                        installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION + Intent.FLAG_ACTIVITY_NEW_TASK);
                        // Start install intent
                        mDownloadingPlugins.remove(marketPlugin.getPluginName());
                        mContext.startActivity(installIntent);
                    }
                }
            };
            mContext.registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            return null;
        }
    }
}
