package com.aurora.utilities;

import android.content.Context;
import com.aurora.market.data.MarketRepository;
import com.aurora.market.data.network.MarketNetworkDataSource;
import com.aurora.market.ui.PluginMarketViewModelFactory;

public final class InjectorUtils {

    private InjectorUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static MarketRepository provideRepository(Context context) {
        MarketNetworkDataSource auroraNetworkDataSource = MarketNetworkDataSource.getInstance(context);
        return MarketRepository.getInstance(auroraNetworkDataSource);
    }

    public static MarketNetworkDataSource provideNetworkDataSource(Context context) {
        provideRepository(context);
        return MarketNetworkDataSource.getInstance(context);
    }

    public static PluginMarketViewModelFactory providePluginMarketViewModel(Context context) {
        MarketRepository repository = provideRepository(context);
        return new PluginMarketViewModelFactory(repository);
    }
}
