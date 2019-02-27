package com.aurora.aurora;

import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.spec.ECField;
import java.util.concurrent.ExecutionException;

public class FeedbackActivity extends AppCompatActivity {

    EditText mEditTextFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mEditTextFeedback = findViewById(R.id.et_feedback);
        mEditTextFeedback.setHint("Enter your feedback here");
    }

    // This will start a SendFeedbackTask and notice the user whether it succeeded or not
    public void onFeedbackClick(View view) {
        boolean success = false;
        String input = mEditTextFeedback.getText().toString();

        if (!input.equals("")) {
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

    static class SendFeedbackTask extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... urls) {
            boolean success = false;
            HttpURLConnection conn = null;

            try {
                // Setup the connection for the Slack Webhook
                URL url = new URL("https://hooks.slack.com/services/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");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                // Add the stringWebHook to the JSONObject
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("text", urls[0]);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                // Check if response is OK
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
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
