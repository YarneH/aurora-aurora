package com.aurora.market.data.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
final class NetworkUtils {
    /**
     * The tag used for Logs
     */
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    /**
     * The URL to the list of MarketPlugins
     */
    private static final String PLUGINLIST_URL =
            "https://pluginmarket.aurora-files.ml/plugin";


    /**
     * Constructor that should not be accessed
     */
    private NetworkUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Get the MarketPluginURL
     *
     * @return a URL, which locates to the plugins
     */
    static URL getMarketPluginURL() {
        try {
            return new URL(PLUGINLIST_URL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "exception", e);
        }
        return null;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try (
                InputStream in = urlConnection.getInputStream();
                Scanner scanner = new Scanner(in)
        ) {
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    static Bitmap getBitmapFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try (
                InputStream in = urlConnection.getInputStream()
        ) {
            return BitmapFactory.decodeStream(in);
        } finally {
            urlConnection.disconnect();
        }
    }
}
