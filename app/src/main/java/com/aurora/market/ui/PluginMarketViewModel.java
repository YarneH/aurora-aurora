package com.aurora.market.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
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

    private MarketRepository mRepository;

    public PluginMarketViewModel(MarketRepository repository){
        mRepository = repository;
        mMarketPlugins = mRepository.getCurrentMarketPlugins();
    }

    public LiveData<List<MarketPlugin>> getMarketPlugins() {
        Log.d("MARKET", "Getting marketplugins: " + mMarketPlugins);
        return mMarketPlugins;
    }

    public MarketPlugin getMarketPlugin(int id){
        return mMarketPlugins.getValue().get(id);
    }
}
