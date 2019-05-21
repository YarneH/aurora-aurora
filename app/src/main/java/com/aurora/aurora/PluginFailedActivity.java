package com.aurora.aurora;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aurora.auroralib.Constants;

import java.util.Objects;

/**
 * This activity will be shown when a plugin's processing failed and it sends an intent for this activity.
 */
public class PluginFailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin_failed);

        // Set up home icon in left upper corner
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle(R.string.something_went_wrong);

        Intent intentThatStartedActivity = getIntent();

        // Get the extra
        String reason = intentThatStartedActivity.getStringExtra(Constants.PLUGIN_PROCESSING_FAILED_REASON);

        /*
         * reference to the text view indicating the reason why the plugin failed
         */
        TextView mTextViewReason = findViewById(R.id.tv_reason);
        mTextViewReason.setText(reason);
        // Set button
        Button button = findViewById(R.id.btn_open);
        button.setOnClickListener((View view) -> {
            // Get uri and MimeType of the file to open it with another app
            Uri fileUri = Uri.parse(intentThatStartedActivity
                    .getStringExtra(Constants.PLUGIN_PROCESSING_FAILED_FILEURI));
            String mimeType = getContentResolver().getType(fileUri);

            Intent openWithOtherAppIntent = new Intent();
            openWithOtherAppIntent.setAction(Intent.ACTION_VIEW);
            openWithOtherAppIntent.setDataAndType(fileUri, mimeType);
            openWithOtherAppIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent chooser = Intent.createChooser(openWithOtherAppIntent,
                    "Choose another app to open the file");

            // Verify the intent will resolve to at least one activity
            if (openWithOtherAppIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            }
        });
    }
}
