package com.aurora.aurora;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <p>
 * Activity responsible for handling the feedback function.
 * </p>
 * <p>
 * The user can write some feedback in this activity and send it.
 * It will automatically be pushed to the Slack channel of this project.
 * This requires internet connection.
 * </p>
 */
public class FeedbackActivity extends AppCompatActivity {
    /**
     * HTTP response code for a successful communication.
     */
    private static final int OK_RESPONSE_CODE = 200;
    /**
     * Message prefixed to the actual feedback that arrives in the Slack.
     */
    private static final String FEEDBACK_MESSAGE_BASE = "*:fire::fire:New feedback:fire::fire:* \n";

    /**
     * @hide
     */
    private static final String FEEDBACK_WEBHOOK_URL =
            "https://hooks.slack.com/services/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";
    private EditText mEditTextFeedback = null;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditTextFeedback = findViewById(R.id.et_feedback);
        mEditTextFeedback.setHint(getString(R.string.enter_feedback));
    }

    /**
     * When the user clicks on the 'Send'-button,
     * this will start a SendFeedbackTask and notice the user whether it succeeded or not
     *
     * @param view a reference to the view
     */
    public void onFeedbackClick(View view) {
        boolean success = false;
        String input = mEditTextFeedback.getText().toString();

        if (!("").equals(input)) {
            String stringWebHook = FEEDBACK_MESSAGE_BASE + input;

            try {
                success = new SendFeedbackTask().execute(stringWebHook).get();
            } catch (Exception e) {
                Log.d("Feedback", "exception", e);
            }

            if (success) {
                Snackbar.make(view, R.string.feedback_sent, Snackbar.LENGTH_SHORT).show();
                mEditTextFeedback.setText("");
            } else {
                Snackbar.make(view, R.string.feedback_wrong, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(view, R.string.feedback_empty, Snackbar.LENGTH_SHORT).show();
        }

    }

    /**
     * A task which will run asynchronously and send the feedback to the Slack channel
     */
    static class SendFeedbackTask extends AsyncTask<String, Void, Boolean> {

        /**
         * The background task of the AsyncTask which is called when the task is executed
         *
         * @param args the 0th element is the input-string of the user
         * @return boolean which indicates whether the task succeeded or failed
         */
        protected Boolean doInBackground(String... args) {
            boolean success = false;

            try {
                URL url = new URL(FEEDBACK_WEBHOOK_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                try (AutoCloseable conc = conn::disconnect) {
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);

                    // Add the stringWebHook to the JSONObject
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("text", args[0]);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    // Check if response is OK
                    int responseCode = conn.getResponseCode();
                    if (responseCode == OK_RESPONSE_CODE) {
                        success = true;
                    }
                }
            } catch (Exception e) {
                Log.d("Feedback", "exception", e);
            }

            return success;
        }
    }
}
