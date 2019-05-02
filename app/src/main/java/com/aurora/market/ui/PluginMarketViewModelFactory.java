package com.aurora.market.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import com.aurora.market.data.MarketRepository;

public class PluginMarketViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MarketRepository mRepository;

    public PluginMarketViewModelFactory(MarketRepository repository) {
        mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        // noinspection unchecked
        return (T) new PluginMarketViewModel(mRepository);
    }
}
