package com.aurora.market;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.aurora.data.AuroraRepository;

import java.util.List;

/**
 * Holds the data for the PluginMarket
 */
public class PluginMarketViewModel extends ViewModel {
    /**
     * The LiveData of all the plugins
     */
    private LiveData<List<MarketPlugin>> mMarketPlugins = new MutableLiveData<>();

    private AuroraRepository mRepository;

    public PluginMarketViewModel(AuroraRepository repository){
        mRepository = repository;
        mMarketPlugins = mRepository.getCurrentMarketPlugins();
    }

    public LiveData<List<MarketPlugin>> getMarketPlugins() {
        return mMarketPlugins;
    }

    public MarketPlugin getMarketPlugin(int id){
        return mMarketPlugins.getValue().get(id);
    }
}
