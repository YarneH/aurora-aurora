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
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class FeedbackActivity extends AppCompatActivity {
    private static final int OK_RESPONSE_CODE = 200;
    private EditText mEditTextFeedback = null;

    /**
     * Runs on startup of the activity, in this case on startup of the app
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mEditTextFeedback = findViewById(R.id.et_feedback);
        mEditTextFeedback.setHint("Enter your feedback here");
    }

    // This will start a SendFeedbackTask and notice the user whether it succeeded or not

    /**
     * When the user clicks on the 'Send'-button,
     * this will start a SendFeedbackTask and notice the user whether it succeeded or not
     *
     * @param view
     */
    public void onFeedbackClick(View view) {
        boolean success = false;
        String input = mEditTextFeedback.getText().toString();

        if (!("").equals(input)) {
            String stringWebHook = "*:fire::fire:New feedback:fire::fire:* \n" + input;

            try {
                success = new SendFeedbackTask().execute(stringWebHook).get();
            } catch (Exception e) {
                Log.d("Feedback", "exception", e);
            }

            if (success) {
                Snackbar.make(view, "Feedback sent, thank you!", Snackbar.LENGTH_SHORT).show();
                mEditTextFeedback.setText("");
            } else {
                Snackbar.make(view, "Something went wrong. Please try again.", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(view, "Please enter your feedback first.", Snackbar.LENGTH_SHORT).show();
        }

    }

    /**
     * A task which will run asynchronously and send the feedback to our Slack channel
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
            HttpsURLConnection conn = null;

            try (AutoCloseable conc = conn::disconnect){
                // Setup the connection for the Slack Webhook
                URL url = new URL("https://hooks.slack.com/services/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");
                conn = (HttpsURLConnection) url.openConnection();
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

                conn.disconnect();
            } catch (Exception e) {
                Log.d("Feedback", "exception", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return success;
        }
    }
}
