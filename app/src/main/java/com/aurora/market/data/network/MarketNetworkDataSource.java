package com.aurora.market.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.aurora.market.data.database.MarketPlugin;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MarketNetworkDataSource {
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
    private static final String URL_PREFIX = "http://";
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
     * Returns a JSONArray of all the MarketPlugins available on the Plugin Market Server
     */
    private static class GetMarketPlugins extends AsyncTask<Void, Void, JSONArray> {

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
                        byte[] logo = new GetMarketPluginLogo().execute(imageLocation).get();

                        MarketPlugin currentMarketPlugin =
                                new MarketPlugin(logo, name, description, creator, version, downloadLocation);

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

        @Override
        protected byte[] doInBackground(String... strings) {
            String url = strings[0];

            if (!url.contains(URL_PREFIX)) {
                url = URL_PREFIX + url;
            }
            try {
                URL downloadLink = new URL(url);
                Bitmap bitmap = NetworkUtils.getBitmapFromHttpUrl(downloadLink);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, stream);
                return stream.toByteArray();
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getSimpleName()).log(Level.FINE, null, e);
            }
            return new byte[0];
        }

    }
}
