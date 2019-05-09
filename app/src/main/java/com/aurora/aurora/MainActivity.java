package com.aurora.aurora;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurora.auroralib.Constants;
import com.aurora.kernel.AuroraCommunicator;
import com.aurora.kernel.ContextNullException;
import com.aurora.kernel.Kernel;
import com.aurora.plugin.InternalServices;
import com.aurora.plugin.Plugin;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * The main activity of the application, started when the app is opened.
 * See the <a href=https://developer.android.com/guide/components/activities/activity-lifecycle>
 * android lifecycle (weblink)
 * </a>
 * for more information.
 * onCreate is called when this activity is launched.
 * <br>
 * Implements {@code NavigationView.OnNavigationItemSelectedListener} to listen to events
 * on the NavigationView.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Constant for radix of Integer.toString()
     */
    private static final int HEX_RADIX = 16;

    /**
     * The request-code used to start the file-chooser intent.
     * Chosen to be 1.
     */
    private static final int REQUEST_FILE_GET = 1;
    private static final int ANIMATION_DURATION = 500;
    private static final float END_POINT_OF_ANIMATION = 0.2F;


    /**
     * Toast that holds the dummy text after a file is searched for.
     * This will disappear after file-search is implemented.
     */
    private Toast mToast = null;

    /**
     * Contains placeholder-text when swapping between views via the NavigationView.
     */
    private TextView mTextViewMain = null;

    /**
     * Android view which is basically a scrollview, but efficiently
     * reuses the containers.
     * <br>
     * It contains all recently opened files.
     */
    private RecyclerView mRecyclerView = null;
    private Context mContext = this;

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
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Set up kernel */
        try {
            mKernel = Kernel.getInstance(this.getApplicationContext());
            mAuroraCommunicator = mKernel.getAuroraCommunicator();
        } catch (ContextNullException e) {
            Log.e("MainActivity",
                    "The kernel was not initialized with a valid android application context", e);
        }

        /*
        Set up plugins
        TODO: Now this is static, later the pluginmarket should register new plugins.
         */
        registerPlugins();


        /* Initialize FirebaseAnalytics */
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        /* Add toolbar when activity is created */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* The floating action button to add files */
        FloatingActionButton fab = findViewById(R.id.fab);
        /* Implementation of adding files in onClick */
        fab.setOnClickListener(view -> selectFile());

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

        /* Setup Main TextView */
        mTextViewMain = findViewById(R.id.tv_main);

        /* Setup RecyclerView */
        mRecyclerView = findViewById(R.id.rv_files);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CardFileAdapter adapter = new CardFileAdapter(mKernel, this);
        mRecyclerView.setAdapter(adapter);


        /* Show TextView when RecyclerView is empty */
        if (adapter.getItemCount() == 0) {
            findViewById(R.id.cl_empty_text).setVisibility(View.VISIBLE);
            ImageView arrow = findViewById(R.id.img_arrow);

            // Set the animation of the arrow in the startscreen
            TranslateAnimation mAnimation = new TranslateAnimation(
                    TranslateAnimation.ABSOLUTE, 0F,
                    TranslateAnimation.ABSOLUTE, 0F,
                    TranslateAnimation.RELATIVE_TO_PARENT, 0F,
                    TranslateAnimation.RELATIVE_TO_PARENT, END_POINT_OF_ANIMATION);
            mAnimation.setDuration(ANIMATION_DURATION);
            mAnimation.setRepeatCount(-1);
            mAnimation.setRepeatMode(Animation.REVERSE);
            mAnimation.setInterpolator(new LinearInterpolator());
            arrow.setAnimation(mAnimation);
        }

        // If opening the file is done from a file explorer
        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            // This method is also called when a file is opened from the file chooser
            onActivityResult(REQUEST_FILE_GET, RESULT_OK, getIntent());
        }
    }

    /**
     * Helper method that register the plugins with their info in the pluginregistry
     * TODO: when the pluginmarket is implemented, remove this method
     */
    private void registerPlugins() {
        final Plugin basicPlugin = new Plugin(
                "com.aurora.basicplugin",
                "Basic Plugin",
                null,
                "Basic plugin to open any file and display extracted text.",
                4,
                "v0.4",
                new ArrayList<>(Arrays.asList(
                        InternalServices.TEXT_EXTRACTION,
                        InternalServices.IMAGE_EXTRACTION,
                        InternalServices.NLP_TOKENIZE,
                        InternalServices.NLP_SSPLIT,
                        InternalServices.NLP_POS
                )));

        final Plugin souschefPlugin = new Plugin(
                "com.aurora.souschef",
                "Souschef",
                null,
                "Plugin to open recipes and display them in an enhanced way.",
                4,
                "v0.4",
                new ArrayList<>(Arrays.asList(
                        InternalServices.TEXT_EXTRACTION,
                        InternalServices.NLP_TOKENIZE,
                        InternalServices.NLP_SSPLIT,
                        InternalServices.NLP_POS
                )));

        final Plugin paperViewerPlugin = new Plugin(
                "com.aurora.paperviewer",
                "Paperviewer",
                null,
                "Plugin to open papers and display them in an enhanced way.",
                4,
                "v0.4",
                new ArrayList<>(Arrays.asList(
                        InternalServices.TEXT_EXTRACTION,
                        InternalServices.IMAGE_EXTRACTION
                )));

        // Register plugins in the registry
        mAuroraCommunicator.registerPlugin(basicPlugin);
        mAuroraCommunicator.registerPlugin(souschefPlugin);
        mAuroraCommunicator.registerPlugin(paperViewerPlugin);
    }

    /**
     * Creates an intent to open the file manager.
     * <p>
     * Creates an intent to open the file manager.
     * If more filetypes need to be opened, use a final String[].
     * </p>
     * <br>
     * <p>
     * For example: <br>
     * final String[] ACCEPT_MIME_TYPES = {
     * "application/pdf",
     * "image/*"
     * };
     * </p>
     * <br>
     * <p>
     * Intent intent = new Intent();
     * <br>
     * intent.setType("* / *");
     * <br>
     * intent.setAction(Intent.ACTION_GET_CONTENT);
     * <br>
     * intent.putExtra(Intent.EXTRA_MIME_TYPES,ACCEPT_MIME_TYPES);
     * </p
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
                     * TODO: convert to custom event, see https://firebase.google.com/docs/analytics/android/events
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
                            Log.d("FOUND_PLUGINS", info.activityInfo.packageName + " - " + info.getIconResource());
                            packageNames.add(info.activityInfo.packageName);
                        }

                        getPluginsAndShowChooser(packageNames, fileName, type, read);

                    } else {
                        Log.d("FOUND_PLUGINS", "NO PLUGINS FOUND");
                        showPopUpView("No plugins were found");
                    }


                } else {
                    showPopUpView("The selected file was null, please select another file!");
                }
            } catch (FileNotFoundException e) {
                showPopUpView("The file could not be found, please select another file!");
                Log.e("FILE_NOT_FOUND", "The file could not be found", e);
            }
        }
    }

    private void getPluginsAndShowChooser(List<String> packageNames, String fileName, String type,
                                          InputStream readFile){
        if (mAuroraCommunicator != null) {
            mAuroraCommunicator.getListOfPlugins(new Observer<List<Plugin>>() {
                private Disposable mDisposable;

                @Override
                public void onSubscribe(Disposable d) {
                    mDisposable = d;
                }

                @Override
                public void onNext(List<Plugin> plugins) {
                    // Show the dialog for selecting a plugin
                    List<Plugin> filteredListOfPlugins = new ArrayList<>();
                    for(Plugin plugin : plugins){
                        if (packageNames.contains(plugin.getUniqueName())){
                            filteredListOfPlugins.add(plugin);
                            packageNames.remove(plugin.getUniqueName());
                        }
                    }
                    for(String notRegisteredPackageName : packageNames){
                        Plugin notRegisteredPlugin = new Plugin(notRegisteredPackageName,
                                notRegisteredPackageName.substring(
                                        notRegisteredPackageName.lastIndexOf('.') + 1),
                                null,
                                getString(R.string.plugin_not_in_registry),
                                0,
                                "");
                        filteredListOfPlugins.add(notRegisteredPlugin);
                    }

                    showPluginAdapterAlertDialog(filteredListOfPlugins, fileName, type, readFile);

                }

                @Override
                public void onError(Throwable e) {
                    Log.e(MainActivity.class.getSimpleName(), "Error while trying to get the list of plugins", e);
                }

                @Override
                public void onComplete() {
                    mDisposable.dispose();
                }
            });
        }
    }


    private void showPluginAdapterAlertDialog(List<Plugin> plugins, String fileName, String type,
                                              InputStream readFile) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // Set title value.
        builder.setTitle(getString(R.string.select_plugin));
        // Set adapter and define the onClickListener
        builder.setAdapter(new PluginAdapter(plugins, this), (DialogInterface dialogInterface, int itemIndex) -> {
                    if (plugins.get(itemIndex).getUniqueName() != null) {
                        Plugin selectedPlugin = plugins.get(itemIndex);
                        Log.d("PLUGIN_SELECTED", selectedPlugin.getUniqueName());
                        mAuroraCommunicator.openFileWithPlugin(fileName, type, readFile,
                                selectedPlugin, getApplicationContext());
                    }});


        builder.setCancelable(true);
        builder.create();
        builder.show();
    }


    /**
     * Private helper method to extract the displayed filename from the Cursor combined with the
     * Uri.
     *
     * <p>
     * This method is needed because files from for example Google Drive get an automatically
     * generated uri that does not contain the actual file name. This method allows to
     * extract the filename displayed in the Android file picker.
     * </p>
     *
     * <p>
     * To ensure uniqueness, a hash of the uri path will be prepended before the filename.
     * </p>
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
     * Inflate the menu; this adds items to the action bar if it is present.
     *
     * @param menu The menu item that should be inflated.
     * @return boolean whether or not successful.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * <p>
     * Handles the selection of menu options in the AppBar (top bar).
     * </p>
     * <p>
     * The action bar will automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     * The AppBar of this activity only has the search button.
     * </p>
     *
     * @param item The selected menu item
     * @return Return false to allow normal menu processing to proceed, true to consume it here
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // get the id of the selected item.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            // Create a LayoutInflater which will create the view for the pop-up
            LayoutInflater li = LayoutInflater.from(this);
            View promptView = li.inflate(R.layout.search_prompt, mRecyclerView, false);
            final EditText userInput = promptView.findViewById(R.id.et_search_prompt);

            // Create a builder to build the actual alertdialog from the previous inflated view
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setView(promptView);
            alertDialogBuilder.setCancelable(true)
                    .setPositiveButton("Ok", (DialogInterface dialogInterface, int i) -> {
                        // Toast for demo
                        if (mToast != null) {
                            mToast.cancel();
                        }
                        mToast = Toast.makeText(MainActivity.this, "Search for "
                                + userInput.getText().toString(), Toast.LENGTH_SHORT);
                        mToast.show();
                    });
            // Create and show the pop-up
            alertDialogBuilder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * <p>
     * Handles selection of options in NavigationView (Drawer layout).
     * </p>
     * <p>
     * The NavigationView contains links to different screens.
     * Selecting one of these should navigate to the corresponding
     * view.
     * </p>
     *
     * @param item Selected menu item.
     * @return whether or not successful.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // TODO: Remove string and boolean if all activities are implemented
        // String for demo
        String text = "";
        boolean home = false;
        if (id == R.id.nav_about_us) {
            text = "About us";
            // Change text and visibility (Used for demo)
            mTextViewMain.setText(text);
        } else if (id == R.id.nav_help_feedback) {
            Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_home) {
            home = true;
        } else {
            text = "Settings";
            // Change text and visibility (Used for demo)
            mTextViewMain.setText(text);
        }
        if (home) {
            //mRecyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.cl_recycler_content).setVisibility(View.VISIBLE);
            mTextViewMain.setVisibility(View.INVISIBLE);
        } else {
            //mRecyclerView.setVisibility(View.INVISIBLE);
            findViewById(R.id.cl_recycler_content).setVisibility(View.INVISIBLE);
            mTextViewMain.setVisibility(View.VISIBLE);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void showPopUpView(String message) {
        // Create a LayoutInflater which will create the view for the pop-up
        LayoutInflater li = LayoutInflater.from(this);
        View promptView = li.inflate(R.layout.popup_card, mRecyclerView, false);

        // Set the message of the TextView
        TextView messageText = promptView.findViewById(R.id.tv_message);
        messageText.setText(message);

        // Create a builder to build the actual alertdialog from the previous inflated view
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);

        // Create and show the pop-up
        alertDialogBuilder.create().show();
    }
}
