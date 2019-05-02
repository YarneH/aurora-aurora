package com.aurora.data;

import android.arch.lifecycle.LiveData;
import com.aurora.data.network.AuroraNetworkDataSource;
import com.aurora.market.MarketPlugin;

import java.util.List;

/**
 * Handles the data operations of Aurora
 */
public final class AuroraRepository {
    private static AuroraRepository sInstance;
    private boolean mInitialized = false;
    private final AuroraNetworkDataSource mNetworkDataSource;

    private AuroraRepository(AuroraNetworkDataSource networkDataSource){
        mNetworkDataSource = networkDataSource;
    }

    public static synchronized AuroraRepository getInstance(AuroraNetworkDataSource networkDataSource) {
        if (sInstance == null) {
            sInstance = new AuroraRepository(networkDataSource);
        }

        return sInstance;
    }

    public synchronized void initializeData() {
        if (!mInitialized) {
            mNetworkDataSource.scheduleRecurringFetchPluginMarketSync();
        }
    }

    public LiveData<List<MarketPlugin>> getCurrentMarketPlugins() {
        initializeData();
        return mNetworkDataSource.getCurrentMarketPlugins();
    }
}
