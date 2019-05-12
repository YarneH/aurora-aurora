package com.aurora.market.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Network;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import com.aurora.aurora.R;
import com.aurora.market.data.database.MarketPlugin;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MarketNetworkDataSource {
    private static final String DEBUG_TAG = "MARKET";

    private static final String JSON_LOCATION_KEY = "apk_location";
    private static final String JSON_DESCRIPTION_KEY = "description";
    private static final String JSON_LOGO_KEY = "plugin_logo";
    private static final String JSON_NAME_KEY = "name";
    private static final String JSON_CREATOR_KEY = "creator";
    private static final String JSON_VERSION_KEY = "version_code";
    private static final String JSON_UNIQUE_NAME = "unique_name";
    private static final String JSON_INTERNAL_SERVICES_KEY = "internal_services";
    private static final String URL_PREFIX = "http://";


    // For Singleton
    private static final Object LOCK = new Object();
    private static MarketNetworkDataSource sInstance;
    private final Context mContext;
    private FirebaseJobDispatcher mDispatcher;

    private MutableLiveData<List<MarketPlugin>> mMarketPlugins;

    private MarketNetworkDataSource(Context context) {
        mContext = context;
        mMarketPlugins = new MutableLiveData<>();
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
     *
     */
    public void fetchMarketPlugins() {
        new GetMarketPlugins().execute();
    }

    private class GetMarketPlugins extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... strings) {
            try {
                URL marketPluginListURL = NetworkUtils.getMarketPluginURL();
                if (marketPluginListURL == null) {
                    Log.d(DEBUG_TAG, "URL is null");
                    // TODO: Handle null
                    return null;
                }

                // Get response
                String jsonPluginListResponse = NetworkUtils.getResponseFromHttpUrl(marketPluginListURL);

                Log.d(DEBUG_TAG, "Response: " + jsonPluginListResponse);

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

                    Log.d(DEBUG_TAG, "Result: " + pluginList.toString());

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

                        Log.d(DEBUG_TAG, "New plugin: " + currentMarketPlugin);

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
    private class GetMarketPluginLogo extends AsyncTask<String, Void, byte[]> {

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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                return stream.toByteArray();
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getSimpleName()).log(Level.FINE, null, e);
            }
            return null;
        }

    }
}
