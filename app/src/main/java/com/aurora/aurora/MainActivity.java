package com.aurora.aurora;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aurora.kernel.AuroraCommunicator;
import com.aurora.kernel.Kernel;

import java.io.InputStream;

/**
 * The main activity of the application, started when the app is opened.
 * See the <a href=https://developer.android.com/guide/components/activities/activity-lifecycle>
 *     android lifecycle (weblink)
 *     </a>
 * for more information.
 * onCreate is called when this activity is launched.
 * <br>
 * Implements {@code NavigationView.OnNavigationItemSelectedListener} to listen to events
 * on the NavigationView.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * The request-code used to start the file-chooser intent.
     * Chosen to be 1.
     */
    private static final int REQUEST_FILE_GET = 1;

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

    /**
     * An instance of the {@link Kernel}.
     */
    private Kernel kernel = null;
    /**
     * Delivers the communication between the environment and the Kernel.
     */
    private AuroraCommunicator auroraCommunicator = null;

    /**
     * Runs on startup of the activity, in this case on startup of the app.
     *
     * @param savedInstanceState Bundle with the state to reload.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Set system properties for DOCX */
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl");

        /* Add toolbar when activity is created */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* The floating action button to add files */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            /* Implementation of adding files in onClick */
            @Override
            public void onClick(View view) {
                selectFile();
            }
        });

        /* Listener and sync are for navigationView functionality */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /* Setup NavigationView and preselect 'Home' */
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);

        /* Setup Main TextView */
        mTextViewMain = (TextView) findViewById(R.id.tv_main);

        /* Setup RecyclerView */
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_files);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CardFileAdapter adapter = new CardFileAdapter();
        mRecyclerView.setAdapter(adapter);

        /* Setup Kernel */
        kernel = new Kernel();
        auroraCommunicator = kernel.getAuroraCommunicator();

        // TODO Remove this test code
        InputStream docFile = getResources().openRawResource(R.raw.apple_crisp);
        auroraCommunicator.openFileWithPlugin("", docFile, "apple_crisp.docx");

    }

    /**
     * <p>
     * Creates an intent to open the file manager. Can currently only select pdf files;
     * If more filetypes need to be opened, use a final String[].
     * </p>
     * <br>
     * <p>
     *     For example: <br>
     * final String[] ACCEPT_MIME_TYPES = {
     *         "application/pdf",
     *         "image/*"
     *   };
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_FILE_GET);
        }
    }

    /**
     * Is called when returning from the file-selection Intent.
     * @param requestCode code used to send the intent. {@value REQUEST_FILE_GET} in this case.
     * @param resultCode status code
     * @param data resulting data, a URI in case of file-selector
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE_GET && resultCode == RESULT_OK) {
            Uri textFile = data.getData();
            Toast.makeText(this, "A file with uri \"" + textFile + "\" was selected.", Snackbar.LENGTH_LONG).show();
            // Use File
        }
    }

    /**
     * Handles when leaving NavigationView (Drawer). Go back to main view with file-overview.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
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
     *     Handles the selection of menu options in the AppBar (top bar).
     * </p>
     * <p>
     *     The action bar will automatically handle clicks on the Home/Up button, so long
     *     as you specify a parent activity in AndroidManifest.xml.
     *     The AppBar of this activity only has the search button.
     * </p>
     * @param item The selected menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // get the id of the selected item.
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
                            mToast = Toast.makeText(MainActivity.this, "Search for "
                                    + userInput.getText().toString(), Toast.LENGTH_SHORT);
                            mToast.show();
                        }
                    });
            // Create and show the pop-up
            alertDialogBuilder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * <p>
     *     Handles selection of options in NavigationView (Drawer layout).
     * </p>
     * <p>
     *     The NavigationView contains links to different screens.
     *     Selecting one of these should navigate to the corresponding
     *     view.
     * </p>
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
        } else if (id == R.id.nav_help_feedback) {
            Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
            startActivity(intent);
            home = true;
        } else if (id == R.id.nav_home) {
            text = "Home";
            home = true;
        } else {
            text = "Settings";
        }
        // Change text and visibility (Used for demo)
        mTextViewMain.setText(text);
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

