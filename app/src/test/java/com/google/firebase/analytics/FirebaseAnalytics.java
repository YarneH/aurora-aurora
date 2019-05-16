package com.google.firebase.analytics;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

public class FirebaseAnalytics {
    public static FirebaseAnalytics getInstance(@NonNull Context var0) {
        return new FirebaseAnalytics();
    }

    public void logEvent(String name, Bundle bundle) {

    }
}
