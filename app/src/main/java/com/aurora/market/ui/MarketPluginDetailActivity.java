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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.aurora.aurora.R;
import com.aurora.market.data.database.MarketPlugin;
import edu.stanford.nlp.ling.tokensregex.Env;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

/**
 * An activity representing a single MarketPlugin detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MarketPluginListActivity}.
 */
public class MarketPluginDetailActivity extends AppCompatActivity {
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
                int test = 9999;

                Boolean readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                Boolean writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                if (!readPermission || !writePermission) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, test);
                }
                new DownloadPluginTask().execute(mMarketPlugin.getDownloadLink());
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

        mMarketPlugin = (MarketPlugin) getIntent().getSerializableExtra(MarketPluginDetailFragment.ARG_MARKET_PLUGIN);
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


    private class DownloadPluginTask extends AsyncTask<URL, Void, Void> {

        // TODO: show loading screen
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

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
                request.setTitle("souschef.apk");
                request.setDescription("Downloading the new plugin!");

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

                // Set the local destination for download file to a path within the application's external files directory
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "souschef.apk");
                request.setMimeType("application/vnd.android.package-archive");

                long downloadID = downloadManager.enqueue(request);

                BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // TODO: Fix this, look into PackageInstaller
                        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                        if (id == downloadID) {
                            Intent installIntent = new Intent(Intent.ACTION_VIEW);
                            String path = Environment.getDataDirectory() + File.pathSeparator + Environment.DIRECTORY_DOWNLOADS + File.separator + "souschef.apk";
                            File test = new File(Environment.getExternalStorageDirectory() + "/download/" + "souschef.apk");
                            //installIntent.setDataAndType(Uri.fromFile(new File(Environment.DIRECTORY_DOWNLOADS + "/souschef.apk")), "application/vnd.android.package-archive");
                            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "paperviewer.apk")), downloadManager.getMimeTypeForDownloadedFile(id));
                            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(installIntent);
                        }
                    }
                };
                registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                //Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "app.apk")), "application/vnd.android.package-archive");
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivity(intent);
            } catch (Exception e) {
                Log.e("Download", "exception", e);
            }

            return null;
        }

        // TODO: delete file and stop loading screen
        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("Download", "finished");
            super.onPostExecute(aVoid);
        }
    }

}
