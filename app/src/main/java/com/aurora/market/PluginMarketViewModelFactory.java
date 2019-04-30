package com.aurora.market;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import com.aurora.data.AuroraRepository;

public class PluginMarketViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AuroraRepository mRepository;

    public PluginMarketViewModelFactory(AuroraRepository repository) {
        mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        // noinspection unchecked
        return (T) new PluginMarketViewModel(mRepository);
    }
}
