package com.aurora.market.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.aurora.market.data.MarketRepository;
import com.aurora.market.data.database.MarketPlugin;

import java.util.List;

/**
 * Holds the data for the PluginMarket
 */
public class PluginMarketViewModel extends ViewModel {
    /**
     * The LiveData of all the plugins
     */
    private LiveData<List<MarketPlugin>> mMarketPlugins;

    /**
     * Constructor for the PluginMarketViewModel
     * @param repository The current MarketRepository
     */
    PluginMarketViewModel(MarketRepository repository){
        mMarketPlugins = repository.getCurrentMarketPlugins();
    }

    /**
     * Default getter for the LiveData MarketPlugins
     * @return A List of the MarketPlugins, as LiveData
     */
    LiveData<List<MarketPlugin>> getMarketPlugins() {
        return mMarketPlugins;
    }

}
