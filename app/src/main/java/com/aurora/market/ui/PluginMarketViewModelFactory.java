package com.aurora.market.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.aurora.market.data.MarketRepository;

/**
 * A ViewModelFactory which creates ViewModels for the PluginMarket
 */
public class PluginMarketViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    /**
     * The MarketRepository, which handles all data requests for the Plugin Market
     */
    private final MarketRepository mRepository;

    /**
     * Constructor for a PluginMarketViewModelFactory linked to the provided MarketRepository
     * @param repository The MarketRepository of the application
     */
    public PluginMarketViewModelFactory(MarketRepository repository) {
        mRepository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        // noinspection unchecked
        return (T) new PluginMarketViewModel(mRepository);
    }
}
