package com.aurora.market.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
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
    private LiveData<List<MarketPlugin>> mMarketPlugins = new MutableLiveData<>();

    /**
     * The current MarketRepository
     */
    private MarketRepository mRepository;

    /**
     * Constructor for the PluginMarketViewModel
     * @param repository The current MarketRepository
     */
    public PluginMarketViewModel(MarketRepository repository){
        mRepository = repository;
        mMarketPlugins = mRepository.getCurrentMarketPlugins();
    }

    /**
     * Default getter for the LiveData MarketPlugins
     * @return A List of the MarketPlugins, as LiveData
     */
    public LiveData<List<MarketPlugin>> getMarketPlugins() {
        return mMarketPlugins;
    }

    /**
     * Getter for a single MarketPlugin
     * @param id the index of the MarketPlugin
     * @return The 'id'th MarketPlugin
     */
    public MarketPlugin getMarketPlugin(int id){
        return mMarketPlugins.getValue().get(id);
    }
}
