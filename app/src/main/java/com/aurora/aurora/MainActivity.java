package com.aurora.aurora;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aurora.kernel.AuroraCommunicator;
import com.aurora.kernel.Kernel;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_FILE_GET = 1;

    // Toast and TextView used for demo and preventing queued Toasts
    private Context mContext = this;
    private Toast mToast = null;
    private TextView mTextViewMain = null;
    private RecyclerView mRecyclerView = null;

    /**
     * Create unique kernel instance (should be passed to every activity, fragment, adapter,...) that needs it
     */
    private Kernel mKernel = null;
    private AuroraCommunicator mAuroraCommunicator = null;

    /**
     * Firebase analytics
     */
    private FirebaseAnalytics mFirebaseAnalytics = null;

    /**
     * Runs on startup of the activity, in this case on startup of the app
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Set up kernel */
        mKernel = new Kernel(this.getApplicationContext());
        mAuroraCommunicator = mKernel.getAuroraCommunicator();

        /* Initialize FirebaseAnalytics */
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        /* Set system properties for DOCX */
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl");

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
    }

    /**
     * Creates an intent to open the file manager. Can currently only select pdf files;
     */
    protected void selectFile() {
        final String[] mimeTypes = {
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/plain"};
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
     * @param requestCode code used to send the intent
     * @param resultCode  status code
     * @param data        resulting data, a Uri in case of fileselector
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE_GET && resultCode == RESULT_OK) {
            Uri textFile = data.getData();

            try {
                if (textFile != null) {
                    Log.i("URI", textFile.toString());
                    ContentResolver cR = getApplicationContext().getContentResolver();
                    String type = MimeTypeMap.getSingleton().getExtensionFromMimeType(cR.getType(textFile));
                    Log.i("MIME", type);

                    /**
                     * Firebase Analytics
                     * TODO: convert to custom event, see https://firebase.google.com/docs/analytics/android/events
                     */
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, textFile.toString());
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
                    mFirebaseAnalytics.logEvent("NEW_FILE_OPENED", bundle);

                    InputStream read = getContentResolver().openInputStream(textFile);
                    mAuroraCommunicator.openFileWithPlugin(textFile.toString(), read, this.getApplicationContext());
                } else {
                    Toast.makeText(this, "The selected file was null", Snackbar.LENGTH_LONG).show();
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "The file could not be found", Snackbar.LENGTH_LONG).show();
                Log.e("FILE_NOT_FOUND", "The file could not be found", e);
            }
        }
    }

    /**
     * Leave NavigationView, back to main view
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            // Create a LayoutInflater which will create the view for the pop-up
            LayoutInflater li = LayoutInflater.from(this);
            View promptView = li.inflate(R.layout.search_prompt, null);
            final EditText userInput = promptView.findViewById(R.id.et_search_prompt);

            // Create a builder to build the actual alertdialog from the previous inflated view
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setView(promptView);
            alertDialogBuilder.setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Toast for demo
                            if (mToast != null) {
                                mToast.cancel();
                            }
                            mToast = Toast.makeText(mContext, "Search for "
                                    + userInput.getText().toString(), Toast.LENGTH_SHORT);
                            mToast.show();
                        }
                    });
            // Create and show the pop-up
            alertDialogBuilder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

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
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextViewMain.setVisibility(View.INVISIBLE);
        } else {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mTextViewMain.setVisibility(View.VISIBLE);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

