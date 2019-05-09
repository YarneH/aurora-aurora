package com.aurora.market.ui;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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

import com.aurora.aurora.R;
import com.aurora.market.data.database.MarketPlugin;

import java.io.File;
import java.net.URL;

/**
 * An activity representing a single MarketPlugin detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MarketPluginListActivity}.
 */
public class MarketPluginDetailActivity extends AppCompatActivity {
    /**
     * The request code used for requesting the writing and reading external storage permission
     */
    private static final int WRITE_AND_READ_REQUEST_CODE = 9999;
    /**
     * The request code used for installing a plugin
     */
    private static final int INSTALL_PLUGIN_REQUEST_CODE = 1234;
    /**
     * The MarketPlugin which is represented by the current DetailActivity
     */
    private MarketPlugin mMarketPlugin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplugin_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        Activity activity = this;

        // Setup the FAB for downloading the Plugin
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_download_plugin);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This will download the plugin", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // Check if the permissions for reading and writing are acquired
                Boolean readPermission = ActivityCompat.checkSelfPermission(
                        activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                Boolean writePermission = ActivityCompat.checkSelfPermission(
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
                    new DownloadAndInstallPluginTask().execute(mMarketPlugin.getDownloadLink());
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == WRITE_AND_READ_REQUEST_CODE
                && grantResults.length > 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            // Download the plugin
            new DownloadAndInstallPluginTask().execute(mMarketPlugin.getDownloadLink());
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == INSTALL_PLUGIN_REQUEST_CODE) {
            // TODO: Delete downloaded apk!
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * A AsyncTask that will download an install a plugin from the market
     */
    private class DownloadAndInstallPluginTask extends AsyncTask<URL, Void, Void> {

        @Override
        protected Void doInBackground(URL... urls) {
            try {
                // Create request for android download manager
                android.net.Uri uri = Uri.parse(urls[0].toString());
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                        DownloadManager.Request.NETWORK_MOBILE);

                // Set title and description
                request.setTitle(mMarketPlugin.getPluginName() + ".apk");
                request.setDescription(getResources().getString(R.string.download_plugin));

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

                // Set the destination for download file to a path within the application's external files directory
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        mMarketPlugin.getPluginName() + ".apk");
                request.setMimeType("application/vnd.android.package-archive");

                // Save the downloadID for later
                long downloadID = downloadManager.enqueue(request);

                BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                        if (id == downloadID) {
                            Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);

                            // Build up the path to the downloaded plugin
                            String path = String.valueOf(Environment.getExternalStorageDirectory()) +
                                    File.pathSeparator +
                                    Environment.DIRECTORY_DOWNLOADS +
                                    File.separator +
                                    mMarketPlugin.getPluginName() +
                                    ".apk";

                            // Get the URI of the downloaded apk and prepare intent
                            Uri apkURI = FileProvider.getUriForFile(context,
                                    context.getApplicationContext().getPackageName() + ".provider",
                                    new File(path));
                            installIntent.setDataAndType(apkURI, "application/vnd.android.package-archive");
                            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivityForResult(installIntent, INSTALL_PLUGIN_REQUEST_CODE);
                            unregisterReceiver(this);
                        }
                    }
                };
                registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            } catch (Exception e) {
                Log.e("Download", "exception", e);
            }
            return null;
        }
    }

}
