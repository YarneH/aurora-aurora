package com.android.volley.toolbox;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.aurora.util.FakeRequestQueue;

public class Volley {

    public static RequestQueue newRequestQueue(Context context){
        return new FakeRequestQueue();
    }
}
