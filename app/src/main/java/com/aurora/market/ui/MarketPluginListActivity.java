package com.aurora.market.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurora.aurora.R;
import com.aurora.market.data.database.MarketPlugin;
import com.aurora.utilities.InjectorUtils;

import java.util.List;
import java.util.Objects;

/**
 * An activity representing a list of MarketPlugins. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MarketPluginDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MarketPluginListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane = false;
    /**
     * The ViewModel of the PluginMarket, containing all the data needed for the UI
     */
    private PluginMarketViewModel mViewModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplugin_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.marketplugin_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
        }

        PluginMarketViewModelFactory factory = InjectorUtils.providePluginMarketViewModel(this.getApplicationContext());
        mViewModel = ViewModelProviders.of(this, factory).get(PluginMarketViewModel.class);

        View recyclerView = findViewById(R.id.marketplugin_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    // Set up the RecyclerView responsible for the CardViews of the MarketPlugins
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        // Get the List of MarketPlugins and initiate the MarketPluginsRecyclerViewAdapter with it
        List<MarketPlugin> marketPlugins = mViewModel.getMarketPlugins().getValue();
        recyclerView.setAdapter(new MarketPluginsRecyclerViewAdapter(this, marketPlugins, mTwoPane));

        // Observe the LiveData and update the MarketPluginsRecyclerViewAdapter when the data changes
        mViewModel.getMarketPlugins().observe(this, (List<MarketPlugin> marketPlugins1) -> {
            Log.d("MARKET", "Observing " + marketPlugins1);
            Objects.requireNonNull((MarketPluginsRecyclerViewAdapter) recyclerView.getAdapter()).setMarketPlugins(
                    marketPlugins1);
            Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        });
    }

    public static class MarketPluginsRecyclerViewAdapter
            extends RecyclerView.Adapter<MarketPluginsRecyclerViewAdapter.ViewHolder> {
        /**
         * The parent activity of the Adapter, used to access the SupportFragmentManager
         */
        private final MarketPluginListActivity mParentActivity;

        /**
         * Boolean representing whether to show WideScreen mode or not
         */
        private final boolean mTwoPane;

        /**
         * The MarketPlugins shown by the MarketPluginsRecyclerView
         */
        private List<MarketPlugin> mMarketPlugins;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MarketPlugin item = (MarketPlugin) view.getTag();
                if (mTwoPane) {
                    // Create the arguments and initiate the MarketPluginDetailFragment
                    Bundle arguments = new Bundle();
                    arguments.putInt(MarketPluginDetailFragment.ARG_ITEM_ID, mMarketPlugins.indexOf(item));
                    arguments.putSerializable(MarketPluginDetailFragment.ARG_MARKET_PLUGIN, item);
                    MarketPluginDetailFragment fragment = new MarketPluginDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.marketplugin_detail_container, fragment)
                            .commit();
                } else {
                    // Create the intent for the MarketPluginDetailActivity with the correct Extra's
                    Context context = view.getContext();
                    Intent intent = new Intent(context, MarketPluginDetailActivity.class);
                    intent.putExtra(MarketPluginDetailFragment.ARG_ITEM_ID, mMarketPlugins.indexOf(item));
                    intent.putExtra(MarketPluginDetailFragment.ARG_MARKET_PLUGIN, item);
                    context.startActivity(intent);
                }
            }
        };

        MarketPluginsRecyclerViewAdapter(MarketPluginListActivity parent, List<MarketPlugin> items, boolean twoPane) {
            mMarketPlugins = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.marketplugin_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mContentView.setText(mMarketPlugins.get(position).getPluginName());
            holder.itemView.setTag(mMarketPlugins.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
            Bitmap imgBitmap = mMarketPlugins.get(position).getLogo();
            Log.d("image", "" + imgBitmap);
            if (imgBitmap != null) {
                int logoDimen = (int) holder.mContentView.getResources().getDimension(R.dimen.market_plugin_logo);
                Bitmap tempBitmap = Bitmap.createScaledBitmap(imgBitmap, logoDimen,
                        logoDimen, true);

                holder.mImageView.setImageBitmap(tempBitmap);
            }
        }

        @Override
        public int getItemCount() {
            if (mMarketPlugins == null) {
                return 0;
            }
            return mMarketPlugins.size();
        }

        /**
         * Set the List of MarketPlugins. This is used to update the data of the adapter
         *
         * @param items
         */
        public void setMarketPlugins(List<MarketPlugin> items) {
            Log.d("Market", "Plugins in adapter set: " + items);
            mMarketPlugins = items;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mContentView;
            private final ImageView mImageView;

            ViewHolder(View view) {
                super(view);
                mContentView = (TextView) view.findViewById(R.id.content);
                mImageView = (ImageView) view.findViewById(R.id.img_logo);
            }
        }
    }
}
