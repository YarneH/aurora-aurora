package com.aurora.utilities;

import android.content.Context;
import com.aurora.data.AuroraRepository;
import com.aurora.data.network.AuroraNetworkDataSource;
import com.aurora.market.PluginMarketViewModelFactory;

public class InjectorUtils {

    public static AuroraRepository provideRepository(Context context) {
        AuroraNetworkDataSource auroraNetworkDataSource = AuroraNetworkDataSource.getInstance(context);
        return AuroraRepository.getInstance(auroraNetworkDataSource);
    }

    public static AuroraNetworkDataSource provideNetworkDataSource(Context context) {
        provideRepository(context);
        return AuroraNetworkDataSource.getInstance(context);
    }

    public static PluginMarketViewModelFactory providePluginMarketViewModel(Context context) {
        AuroraRepository repository = provideRepository(context);
        return new PluginMarketViewModelFactory(repository);
    }
}
