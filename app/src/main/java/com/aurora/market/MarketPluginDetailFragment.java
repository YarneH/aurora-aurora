package com.aurora.market;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aurora.aurora.R;

/**
 * A fragment representing a single MarketPlugin detail screen.
 * This fragment is either contained in a {@link MarketPluginListActivity}
 * in two-pane mode (on tablets) or a {@link MarketPluginDetailActivity}
 * on handsets.
 */
public class MarketPluginDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The fragment argument representing the MarketPlugin
     */
    public static final String ARG_MARKET_PLUGIN = "market_plugin";

    /**
     * The MarketPlugin this fragment is presenting.
     */
    private MarketPlugin mMarketPlugin;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MarketPluginDetailFragment() {
        // Mandatory empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MARKET_PLUGIN)) {
            mMarketPlugin = (MarketPlugin) getArguments().getSerializable(ARG_MARKET_PLUGIN);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mMarketPlugin.getPluginName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.marketplugin_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mMarketPlugin != null) {
            ((TextView) rootView.findViewById(R.id.marketplugin_detail)).setText(mMarketPlugin.getDescription());
        }

        return rootView;
    }
}
