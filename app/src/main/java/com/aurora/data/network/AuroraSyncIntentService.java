package com.aurora.data.network;

import android.app.IntentService;
import android.content.Intent;
import com.aurora.utilities.InjectorUtils;

public class AuroraSyncIntentService extends IntentService {

    public AuroraSyncIntentService() {

        super("SunshineSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AuroraNetworkDataSource networkDataSource =
                InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        networkDataSource.fetchMarketPlugins();
    }
}
