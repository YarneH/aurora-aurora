package com.aurora.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.aurora.market.MarketPlugin;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import org.json.JSONArray;
import org.json.JSONObject;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AuroraNetworkDataSource {
    private static final String DEBUG_TAG = "MARKET";

    private static final String AURORA_SYNC_TAG = "aurora-sync";
    private static final String JSON_LOCATION_KEY = "apk_location";
    private static final String JSON_DESCRIPTION_KEY = "description";
    private static final String JSON_LOGO_LOCATION_KEY = "logo_location";
    private static final String JSON_NAME_KEY = "name";

    // For Singleton
    private static final Object LOCK = new Object();
    private static AuroraNetworkDataSource sInstance;
    private final Context mContext;
    private FirebaseJobDispatcher mDispatcher;

    private MutableLiveData<List<MarketPlugin>> mMarketPlugins;

    private AuroraNetworkDataSource(Context context) {
        mContext = context;
        mMarketPlugins = new MutableLiveData<>();
        Driver driver = new GooglePlayDriver(mContext);
        mDispatcher = new FirebaseJobDispatcher(driver);
    }

    /**
     * Get the singleton of this class
     */
    public static AuroraNetworkDataSource getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AuroraNetworkDataSource(context);
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
     * Starts an intent service to fetch the MarketPlugins
     */
    public void startFetchMarketPluginService() {
        Intent intentToFetch = new Intent(mContext, AuroraSyncIntentService.class);
        mContext.startService(intentToFetch);
    }

    /**
     * Schedules a repeating job service which fetches the weather.
     */
    public void scheduleRecurringFetchPluginMarketSync() {

        Log.d(DEBUG_TAG, "Creating job");
        // Create the Job to periodically sync the PluginMarket
        Job syncPluginMarketJob = mDispatcher.newJobBuilder()
                /* The Service that will be used to sync Sunshine's data */
                .setService(AuroraFirebaseJobService.class)
                /* Set the UNIQUE tag used to identify this Job */
                .setTag(AURORA_SYNC_TAG)
                /*
                 * Network constraints on which this Job should run. We choose to run on any
                 * network, but you can also choose to run only on un-metered networks or when the
                 * device is charging. It might be a good idea to include a preference for this,
                 * as some users may not want to download any data on their mobile plan. ($$$)
                 */
                .setConstraints(Constraint.ON_ANY_NETWORK)
                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                .setLifetime(Lifetime.FOREVER)
                /*
                 * We want the Plugins's to be updated once
                 */
                .setRecurring(false)
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

        // Schedule the Job with the dispatcher
        mDispatcher.schedule(syncPluginMarketJob);
        Log.d(DEBUG_TAG, "Job created");
    }

    /**
     *
     */
    public void fetchMarketPlugins() {
        new GetMarketPlugins().execute();
    }

    private class GetMarketPlugins extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... strings) {
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
                return new JSONObject(jsonPluginListResponse);
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getSimpleName()).log(Level.SEVERE, null, e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject pluginList) {
            if (pluginList != null) {
                try {
                    ArrayList<MarketPlugin> tempList = new ArrayList<>();

                    JSONArray jsonPlugins = pluginList.getJSONArray("plugins");

                    Log.d(DEBUG_TAG, "Result: " + jsonPlugins.toString());

                    for (int i = 0; i < jsonPlugins.length(); i++) {
                        JSONObject currentPlugin = jsonPlugins.getJSONObject(i);
                        String imageLocation = currentPlugin.getString(JSON_LOGO_LOCATION_KEY);
                        String name = currentPlugin.getString(JSON_NAME_KEY);
                        String downloadLocation = currentPlugin.getString(JSON_LOCATION_KEY);
                        String description = currentPlugin.getString(JSON_DESCRIPTION_KEY);

                        MarketPlugin currentMarketPlugin =
                                new MarketPlugin(imageLocation, name, description, downloadLocation);

                        tempList.add(currentMarketPlugin);
                    }

                    mMarketPlugins.postValue(tempList);

                } catch (Exception e) {
                    Logger.getLogger(this.getClass().getSimpleName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }
}
