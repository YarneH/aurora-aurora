package com.aurora.aurora;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.aurora.auroralib.Constants;

/**
 * This activity will be shown when a plugin's processing failed and it sends an intent for this activity.
 */
public class PluginFailedActivity extends AppCompatActivity {

    /**
     * reference to the text view indicating the reason why the plugin failed
     */
    private TextView mTextViewReason = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin_failed);

        // Set up home icon in left upper corner
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle(R.string.something_went_wrong);

        Intent intentThatStartedActivity = getIntent();

        // Get the extra
        String reason = intentThatStartedActivity.getStringExtra(Constants.PLUGIN_PROCESSING_FAILED_REASON);

        // Set the text
        mTextViewReason = findViewById(R.id.tv_reason);
        mTextViewReason.setText(reason);
    }
}
