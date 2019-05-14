package com.aurora.market.ui;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import android.widget.ProgressBar;
import android.widget.TextView;
import com.aurora.aurora.CardFileAdapter;
import com.aurora.aurora.R;
import com.aurora.market.data.database.MarketPlugin;
import com.aurora.market.data.network.MarketNetworkDataSource;

import java.io.File;
import java.net.URL;

import static java.lang.Thread.sleep;

/**
 * An activity representing a single MarketPlugin detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MarketPluginListActivity}.
 */
public class MarketPluginDetailActivity extends AppCompatActivity {
    private static final String LOG_TAG = MarketPluginDetailActivity.class.getSimpleName();

    /**
     * The request code used for requesting the writing and reading external storage permission
     */
    private static final int WRITE_AND_READ_REQUEST_CODE = 9999;

    /**
     * The MarketPlugin which is represented by the current DetailActivity
     */
    private MarketPlugin mMarketPlugin = null;

    /**
     *
     */
    private ProgressBar mProgressBar;

    /**
     *
     */
    private FloatingActionButton mDownloadFAB;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplugin_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        mProgressBar = findViewById(R.id.pb_download);
        mDownloadFAB = findViewById(R.id.fab_download_plugin);

        setSupportActionBar(toolbar);
        // Setup the FAB for downloading the Plugin
        Activity activity = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_download_plugin);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This will download the plugin", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // Check if the permissions for reading and writing are acquired
                boolean readPermission = ActivityCompat.checkSelfPermission(
                        activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                boolean writePermission = ActivityCompat.checkSelfPermission(
                        activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                // Ask the permissions if needed, otherwise download the MarketPlugin
                if (!readPermission || !writePermission) {
                    ActivityCompat.requestPermissions(
                            activity,
                            new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_AND_READ_REQUEST_CODE);
                } else {
                    MarketNetworkDataSource.getInstance(getBaseContext()).downloadMarketPlugin(mMarketPlugin);
                    updateDownloadUI();
                }
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state saved from previous configurations
        // of this activity (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(MarketPluginDetailFragment.ARG_ITEM_ID,
                    getIntent().getIntExtra(MarketPluginDetailFragment.ARG_ITEM_ID, 0));
            arguments.putSerializable(MarketPluginDetailFragment.ARG_MARKET_PLUGIN,
                    getIntent().getSerializableExtra(MarketPluginDetailFragment.ARG_MARKET_PLUGIN));
            MarketPluginDetailFragment fragment = new MarketPluginDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.marketplugin_detail_container, fragment)
                    .commit();
        }

        // Save the MarketPlugin
        mMarketPlugin = (MarketPlugin) getIntent().getSerializableExtra(MarketPluginDetailFragment.ARG_MARKET_PLUGIN);
        updateDownloadUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDownloadUI();
    }

    /**
     * Updates the UI corresponding to the status of a download
     */
    private void updateDownloadUI() {
        if (mDownloadFAB != null && mProgressBar != null) {
            Drawable intent =
                    null;
            try {
                intent = getBaseContext().getPackageManager().getApplicationIcon("com.aurora." + mMarketPlugin.getPluginName().toLowerCase());
                Log.d("Download", "Installed");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Log.d("Download", "Not installed");
            }
            if (MarketNetworkDataSource.getInstance(getBaseContext()).isDownloading(mMarketPlugin)) {
                mProgressBar.setVisibility(View.VISIBLE);
                mDownloadFAB.setImageDrawable(null);
            } else {
                mProgressBar.setVisibility(View.GONE);
                mDownloadFAB.setImageResource(R.drawable.ic_download_white);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == WRITE_AND_READ_REQUEST_CODE
                && grantResults.length > 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            // Download the plugin
            MarketNetworkDataSource.getInstance(getBaseContext()).downloadMarketPlugin(mMarketPlugin);
            updateDownloadUI();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
