package com.aurora.market.data;

import android.arch.lifecycle.LiveData;
import com.aurora.market.data.network.MarketNetworkDataSource;
import com.aurora.market.data.database.MarketPlugin;

import java.util.List;

/**
 * Handles the data operations of Aurora
 */
public final class MarketRepository {
    private static MarketRepository sInstance;
    private boolean mInitialized = false;
    private final MarketNetworkDataSource mNetworkDataSource;

    private MarketRepository(MarketNetworkDataSource networkDataSource){
        mNetworkDataSource = networkDataSource;
    }

    public static synchronized MarketRepository getInstance(MarketNetworkDataSource networkDataSource) {
        if (sInstance == null) {
            sInstance = new MarketRepository(networkDataSource);
        }

        return sInstance;
    }

    public synchronized void initializeData() {
        if (!mInitialized) {
            mNetworkDataSource.fetchMarketPlugins();
        }
    }

    public LiveData<List<MarketPlugin>> getCurrentMarketPlugins() {
        initializeData();
        return mNetworkDataSource.getCurrentMarketPlugins();
    }
}
