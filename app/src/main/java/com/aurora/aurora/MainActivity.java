package com.aurora.aurora;

import android.arch.lifecycle.MutableLiveData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.aurora.auroralib.Constants;
import com.aurora.internalservice.internalcache.CachedFileInfo;
import com.aurora.kernel.AuroraCommunicator;
import com.aurora.kernel.ContextNullException;
import com.aurora.kernel.Kernel;
import com.aurora.market.ui.MarketPluginListActivity;
import com.aurora.plugin.InternalServices;
import com.aurora.plugin.Plugin;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * The main activity of the application, started when the app is opened.
 * See the <a href=https://developer.android.com/guide/components/activities/activity-lifecycle>
 * android lifecycle (weblink)
 * </a>
 * for more information.
 * onCreate is called when this activity is launched.
 * <p>
 * Implements {@code NavigationView.OnNavigationItemSelectedListener} to listen to events
 * on the NavigationView.
 */
@SuppressWarnings("squid:S1200")
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * Tag for logging
     */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();


    /**
     * Constant for radix of Integer.toString()
     */
    private static final int HEX_RADIX = 16;

    /**
     * The request-code used to start the file-chooser intent.
     * Chosen to be 1.
     */
    private static final int REQUEST_FILE_GET = 1;

    /**
     * Devide a number in half
     */
    private static final int DEVIDE_IN_HALF = 2;

    /**
     * Android view which is basically a scrollview, but efficiently
     * reuses the containers.
     * <br>
     * It contains all recently opened files.
     */
    private RecyclerView mRecyclerView = null;

    /**
     * An instance of the {@link Kernel}.
     */
    private Kernel mKernel = null;

    /**
     * Delivers the communication between the environment and the Kernel.
     */
    private AuroraCommunicator mAuroraCommunicator = null;

    /**
     * Firebase analytics
     */
    private FirebaseAnalytics mFirebaseAnalytics = null;

    /**
     * The list of cached files, which will be shown in the RecyclerView
     */
    private List<CachedFileInfo> mCachedFileInfoList = new ArrayList<>();


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Set up kernel */
        setupKernel();

        /* Setup RecyclerView */
        mRecyclerView = findViewById(R.id.rv_files);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CardFileAdapter adapter = new CardFileAdapter(mKernel, this, mCachedFileInfoList);
        mRecyclerView.setAdapter(adapter);

        /* Setup swipes of RecyclerView */
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                ((CardFileAdapter) Objects.requireNonNull(mRecyclerView.getAdapter()))
                        .removeCard(viewHolder.getAdapterPosition());
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
                                    boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int itemHeight = itemView.getHeight();
                Drawable icon = getDrawable(R.drawable.delete_shape);
                int iconHeight = 0;
                if (icon != null) {
                    iconHeight = icon.getIntrinsicHeight();
                }
                int iconWidth = 0;
                if (icon != null) {
                    iconWidth = icon.getIntrinsicWidth();
                }

                // Calculate the position of the icon
                int deleteIconTop = itemView.getTop() + (itemHeight - iconHeight) / DEVIDE_IN_HALF;
                int deleteIconMargin = (itemHeight - iconHeight) / DEVIDE_IN_HALF;
                int deleteIconLeft = itemView.getRight() - deleteIconMargin - iconWidth;
                int deleteIconRight = itemView.getRight() - deleteIconMargin;
                int deleteIconBottom = deleteIconTop + iconHeight;

                // Draw icon
                if (icon != null) {
                    icon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
                    icon.draw(c);
                }
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(mRecyclerView);

        /* Get list of cached files */
        refreshCachedFileInfoList();

        /* Initialize FirebaseAnalytics */
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        /* Add toolbar when activity is created */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Implementation of adding files in onClick */
        findViewById(R.id.fab_download_plugin).setOnClickListener(view -> selectFile());

        /* Listener and sync are for navigationView functionality */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /* Setup NavigationView and preselect 'Home' */
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);

        /* Show TextView when RecyclerView is empty */
        if (adapter.getItemCount() == 0) {
            findViewById(R.id.cl_empty_text).setVisibility(View.VISIBLE);
        }

        // If opening the file is done from a file explorer
        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            // This method is also called when a file is opened from the file chooser
            onActivityResult(REQUEST_FILE_GET, RESULT_OK, getIntent());
        }
    }

    /**
     * Set up the kernel
     */
    private void setupKernel() {
        /* Listen to the loading state of the communicator */
        try {
            mKernel = Kernel.getInstance(this.getApplicationContext());
            mAuroraCommunicator = mKernel.getAuroraCommunicator();
            MutableLiveData<Boolean> mLoading = mAuroraCommunicator.getLoadingData();
            mLoading.observe(this, (Boolean isLoading) -> {
                if (isLoading == null || !isLoading) {
                    findViewById(R.id.pb_extracting).setVisibility(View.GONE);
                    findViewById(R.id.nav_view).bringToFront();
                    ((DrawerLayout) findViewById(R.id.drawer_layout))
                            .setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                } else {
                    // Show loading screen if it was visible before.
                    findViewById(R.id.pb_extracting).setVisibility(View.VISIBLE);
                    ((DrawerLayout) findViewById(R.id.drawer_layout))
                            .setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
            });
        } catch (ContextNullException e) {
            Log.e(LOG_TAG,
                    "The kernel was not initialized with a valid android application context", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();
        refreshCachedFileInfoList();
    }

    /**
     * Refresh the list of info of the cached files
     */
    private void refreshCachedFileInfoList() {
        /* Get list of cached files */
        if (mAuroraCommunicator != null) {
            mAuroraCommunicator.getListOfCachedFiles(0, new Observer<List<CachedFileInfo>>() {
                private Disposable mDisposable;

                @Override
                public void onSubscribe(Disposable d) {
                    mDisposable = d;
                }

                @Override
                public void onNext(List<CachedFileInfo> cachedFileInfos) {
                    mCachedFileInfoList = cachedFileInfos;
                    ((CardFileAdapter) Objects.requireNonNull(mRecyclerView.getAdapter()))
                            .updateData(mCachedFileInfoList);
                    if (cachedFileInfos.isEmpty()) {
                        findViewById(R.id.cl_empty_text).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.cl_empty_text).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("MainActivity", "Error while trying to get the list of cached files", e);
                }

                @Override
                public void onComplete() {
                    mDisposable.dispose();
                }
            });
        }
    }

    /**
     * Creates an intent to open the file manager.
     */
    protected void selectFile() {
        final String[] mimeTypes = {
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "text/plain",
                "application/pdf"};
        Intent intent = new Intent();
        intent.setType("* / *");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_FILE_GET);
        }
    }

    /**
     * In this case when selectFile()'s intent returns
     *
     * @param requestCode code used to send the intent. {@value REQUEST_FILE_GET} in this case.
     * @param resultCode  status code
     * @param data        resulting data, a URI in case of file-selector
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE_GET && resultCode == RESULT_OK) {
            Uri textFile = data.getData();

            try {
                if (textFile != null) {
                    Log.i("URI", textFile.toString());
                    String type = MimeTypeMap.getSingleton().getExtensionFromMimeType(
                            getApplicationContext().getContentResolver().getType(textFile));
                    Log.i("MIME", type);
                    String fileName = getFileName(textFile);
                    Log.i("FILENAME", fileName);

                    /*
                     * Firebase Analytics
                     */
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, textFile.toString());
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
                    mFirebaseAnalytics.logEvent("NEW_FILE_OPENED", bundle);

                    // Make inputstream reader for aurora communicator
                    InputStream read = getContentResolver().openInputStream(textFile);

                    // TODO: create and call custom chooser here, and let it return the unique plugin name of the
                    // plugin to open the file with (package name, e.g. "com.aurora.basicplugin")


                    // Create intent to open file with a certain plugin
                    Intent pluginAction = new Intent(Constants.PLUGIN_ACTION);
                    pluginAction.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    pluginAction.setType("*/*");


                    // Look for plugins that can answer the pluginAction
                    PackageManager manager = getPackageManager();
                    List<ResolveInfo> infos = manager.queryIntentActivities(pluginAction,
                            PackageManager.MATCH_DEFAULT_ONLY);

                    if (!infos.isEmpty()) {
                        List<String> packageNames = new ArrayList<>();
                        for (ResolveInfo info : infos) {
                            Log.i(LOG_TAG,
                                    "Found Plugin: " + info.activityInfo.packageName +
                                            " - " + info.getIconResource());
                            packageNames.add(info.activityInfo.packageName);
                        }
                        // Get a list of filled in Plugin objects
                        List<Plugin> plugins = getPlugins(packageNames);
                        // Show the chooser
                        showPluginAdapterAlertDialog(plugins, textFile, fileName, type, read);

                    } else {
                        Log.i(LOG_TAG, "NO PLUGINS FOUND");
                        showPopUpView("No plugins were found");
                    }


                } else {
                    showPopUpView("The selected file was null, please select another file!");
                }
            } catch (FileNotFoundException e) {
                showPopUpView("The file could not be found, please select another file!");
                Log.e(LOG_TAG, "The file could not be found", e);
            }
        }
    }

    /**
     * Get all plugins on device and their info form their packageNames
     *
     * @param packageNames The packageNames of the plugins that where found using Intent querying
     * @return The list of plugins that were resolved
     */
    private List<Plugin> getPlugins(List<String> packageNames) {
        List<Plugin> plugins = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        for (String packageName : packageNames) {
            // Get the ApplicationInfo and PackageInfo to get the attributes of the Plugin
            ApplicationInfo applicationInfo = null;
            PackageInfo packageInfo = null;
            try {
                applicationInfo = packageManager.getApplicationInfo(packageName,
                        PackageManager.GET_META_DATA);
                packageInfo = packageManager.getPackageInfo(packageName, 0);
            } catch (final PackageManager.NameNotFoundException e) {
                Log.e(LOG_TAG, "Package not found", e);
            }

            plugins.add(createPlugin(packageName, applicationInfo, packageInfo, packageManager));
        }

        return plugins;
    }

    /**
     * Create a Plugin object with info obtained from a packageName
     *
     * @param packageName     name of the plugin's package
     * @param applicationInfo ApplicationInfo obtained for the package
     * @param packageInfo     PackageInfo obtained for the package
     * @param packageManager  A packageManager to resolve some final info
     * @return the plugin object created
     */
    private Plugin createPlugin(String packageName, ApplicationInfo applicationInfo,
                                PackageInfo packageInfo, PackageManager packageManager) {
        // Create the Plugin object
        Plugin plugin;
        if (applicationInfo != null && packageInfo != null) {
            // Get the requested Internal Services for preprocessing
            List<InternalServices> internalServices = new ArrayList<>();
            for (InternalServices internalService : InternalServices.values()) {
                if (applicationInfo.metaData != null &&
                        applicationInfo.metaData.getBoolean(internalService.name())) {
                    internalServices.add(internalService);
                }
            }

            // Create the plugin
            plugin = new Plugin(packageName, (String) packageManager.getApplicationLabel(
                    applicationInfo), null, (String) applicationInfo.loadDescription(
                    packageManager), internalServices);
        } else {
            plugin = new Plugin(packageName, packageName.substring(
                    packageName.lastIndexOf('.') + 1), null, null);
        }
        return plugin;
    }


    /**
     * Shows the plugin picker dialog.
     *
     * @param plugins  The plugins to be offered in the chooser dialog
     * @param textFile The exact uri of the file to be opened
     * @param fileName The name of the file to be opened
     * @param type     The MIME type of the file to be opened
     * @param readFile An InputStream to the read file
     */
    private void showPluginAdapterAlertDialog(List<Plugin> plugins, Uri textFile, String fileName, String type,
                                              InputStream readFile) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set title value.
        builder.setTitle(getString(R.string.select_plugin));
        // Set adapter and define the onClickListener
        builder.setAdapter(new PluginAdapter(plugins, this), (
                DialogInterface dialogInterface, int itemIndex) -> {
            if (plugins.get(itemIndex).getUniqueName() != null) {
                Plugin selectedPlugin = plugins.get(itemIndex);
                Log.i(LOG_TAG, "Selected Plugin: " + selectedPlugin.getUniqueName());
                findViewById(R.id.pb_extracting).setVisibility(View.VISIBLE);
                ((DrawerLayout) findViewById(R.id.drawer_layout))
                        .setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mAuroraCommunicator.openFileWithPlugin(textFile.toString(), fileName, type, readFile,
                        selectedPlugin);
                dialogInterface.cancel();
            }
        });

        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(getDrawable(R.drawable.inset_dialog));
        dialog.show();
    }


    /**
     * Private helper method to extract the displayed filename from the Cursor combined with the
     * Uri.
     * <p>
     * This method is needed because files from for example Google Drive get an automatically
     * generated uri that does not contain the actual file name. This method allows to
     * extract the filename displayed in the Android file picker.
     * <p>
     * To ensure uniqueness, a hash of the uri path will be prepended before the filename.
     *
     * @param uri the Uri to get the displayed filename from
     * @return The displayed filename
     */
    private String getFileName(Uri uri) {

        String result;

        // Add hash to filename so we have a unique filename for different files with the same filename on different
        // locations
        if (uri.getPath() != null) {
            result = Integer.toString(uri.getPath().hashCode(), HEX_RADIX) + "_";
        } else {
            return null;
        }

        try (Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null, null)) {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                result += cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                return result;
            }
        }
        return null;
    }

    /**
     * Handles when leaving NavigationView (Drawer). Go back to main view with file-overview.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Handles selection of options in NavigationView (Drawer layout).
     * <p>
     * The NavigationView contains links to different screens.
     * Selecting one of these should navigate to the corresponding
     * view.
     *
     * @param item Selected menu item.
     * @return whether or not successful.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_help_feedback) {
            Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_plugin_market) {
            Intent intent = new Intent(MainActivity.this, MarketPluginListActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Shows the popup view for some info text.
     *
     * @param message Text to show
     */
    private void showPopUpView(String message) {
        // Create a LayoutInflater which will create the view for the pop-up
        LayoutInflater inflater = LayoutInflater.from(this);
        View promptView = inflater.inflate(R.layout.popup_card, mRecyclerView, false);

        // Set the message of the TextView
        TextView messageText = promptView.findViewById(R.id.tv_message);
        messageText.setText(message);

        // Create a builder to build the actual alertdialog from the previous inflated view
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(true);

        // Create and show the pop-up
        alertDialogBuilder.create().show();
    }
}
