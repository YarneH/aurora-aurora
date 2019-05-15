package com.aurora.market.data;

import android.arch.lifecycle.LiveData;
import com.aurora.market.data.network.MarketNetworkDataSource;
import com.aurora.market.data.database.MarketPlugin;

import java.util.List;

/**
 * Handles the data operations of Aurora
 */
public final class MarketRepository {
    /**
     * The instance of the singleton
     */
    private static MarketRepository sInstance;
    /**
     * A boolean representing whether the instance is initialized
     */
    private boolean mDataInitialized = false;
    /**
     * The MarketNetworkDataSource, responsible for the communication with online services
     */
    private final MarketNetworkDataSource mNetworkDataSource;

    /**
     * Private constructor of the singleton
     *
     * @param networkDataSource the used MarketNetworkDataSource
     */
    private MarketRepository(MarketNetworkDataSource networkDataSource) {
        mNetworkDataSource = networkDataSource;
    }

    /**
     * Default getter
     * @param networkDataSource the current MarketNetworkDataSource
     * @return The MarketRepository instance
     */
    public static synchronized MarketRepository getInstance(MarketNetworkDataSource networkDataSource) {
        if (sInstance == null) {
            sInstance = new MarketRepository(networkDataSource);
        }

        return sInstance;
    }

    /**
     * Fetch the MarketPlugins from the NetworkDataSource
     */
    private synchronized void initializeData() {
        mNetworkDataSource.fetchMarketPlugins();
        mDataInitialized = true;
    }

    /**
     * Getter for the MarketPlugins
     * @return the cached MarketPlugins
     */
    public LiveData<List<MarketPlugin>> getCurrentMarketPlugins() {
        if (!mDataInitialized) {
            initializeData();
        }
        return mNetworkDataSource.getCurrentMarketPlugins();
    }
}
