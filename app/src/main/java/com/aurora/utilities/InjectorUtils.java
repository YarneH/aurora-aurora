package com.aurora.utilities;

import android.content.Context;
import com.aurora.market.data.MarketRepository;
import com.aurora.market.data.network.MarketNetworkDataSource;
import com.aurora.market.ui.PluginMarketViewModelFactory;

/**
 * Provides static methods to inject the various classes needed for the Plugin Market
 */
public final class InjectorUtils {

    private InjectorUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Provides the MarketRepository of the context
     * @param context the application context
     * @return the MarketRepository instance
     */
    private static MarketRepository provideRepository(Context context) {
        MarketNetworkDataSource auroraNetworkDataSource = MarketNetworkDataSource.getInstance(context);
        return MarketRepository.getInstance(auroraNetworkDataSource);
    }

    /**
     * Provides the Factory of the PluginMarketViewModel
     * @param context the application context
     * @return a PluginMarketViewModelFactory
     */
    public static PluginMarketViewModelFactory providePluginMarketViewModel(Context context) {
        MarketRepository repository = provideRepository(context);
        return new PluginMarketViewModelFactory(repository);
    }
}
