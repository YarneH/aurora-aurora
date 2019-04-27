package com.aurora.market;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;
import io.reactivex.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the data for the PluginMarket
 */
public class PluginMarketViewModel extends AndroidViewModel {
    /**
     * The LiveData of all the plugins
     */
    private MutableLiveData<List<MarketPlugin>> mMarketPlugins = new MutableLiveData<>();

    public PluginMarketViewModel(@NonNull Application application){
        super(application);

        /*
        Dummy values
         */
        MarketPlugin dummy1 = new MarketPlugin(null, "Plugin Name 1", "This is a new plugin!", null);
        MarketPlugin dummy2 = new MarketPlugin(null, "Plugin Name 2", "This is also a new plugin!", null);
        MarketPlugin dummy3 = new MarketPlugin(null, "Plugin Name 3", "This is also a new plugin! WAUW!", null);

        mMarketPlugins.setValue(new ArrayList<>());
        mMarketPlugins.getValue().add(dummy1);
        mMarketPlugins.getValue().add(dummy2);
        mMarketPlugins.getValue().add(dummy3);
    }

    public LiveData<List<MarketPlugin>> getMarketPlugins() {
        return mMarketPlugins;
    }

    public MarketPlugin getMarketPlugin(int id){
        return mMarketPlugins.getValue().get(id);
    }

    // TODO: Delete this!
    // Adds more dummy values to the list (for debugging UI)
    public void getMore() {
        MarketPlugin dummy1 = new MarketPlugin(null, "Plugin Name 1", "This is a new plugin!", null);
        MarketPlugin dummy2 = new MarketPlugin(null, "Plugin Name 2", "This is also a new plugin!", null);
        MarketPlugin dummy3 = new MarketPlugin(null, "Plugin Name 3", "This is also a new plugin! WAUW!", null);

        List<MarketPlugin> temp = mMarketPlugins.getValue();
        temp.add(dummy1);
        temp.add(dummy2);
        temp.add(dummy3);

        Log.d("Observings ", "" + temp.size());

        mMarketPlugins.setValue(temp);
    }
}
