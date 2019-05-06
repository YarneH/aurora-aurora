package com.aurora.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.NoCache;

public class FakeRequestQueue extends RequestQueue {
    public FakeRequestQueue() {
        super(new NoCache(), new BasicNetwork(new MockHttpStack()), 4 , new ImmediateResponseDelivery());
    }
}

