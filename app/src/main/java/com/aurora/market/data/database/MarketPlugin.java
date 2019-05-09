package com.aurora.market.data.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Class representing a Plugin which can be downloaded from the PluginMarket
 */
public class MarketPlugin implements Serializable {
    /**
     * A Bitmap representing a base64 encoded image of the plugin
     */
    private Bitmap mLogo;
    /**
     * The name of the plugin
     */
    private String mPluginName;
    /**
     * The description of the plugin
     */
    private String mDescription;
    /**
     * The link the plugin can be downloaded from
     */
    private URL mDownloadLink;


    /**
     * Dummy constructor for testing UI
     * @param logo          a base64 encoded String representation of the image
     * @param name          a string representing the name of the plugin
     * @param description   a string representing the description of the plugin
     * @param url           a string representing the link the plugin can be downloaded from
     */
    public MarketPlugin(String logo, String name, String description, String url){
        this.mPluginName = name;
        this.mDescription = description;
        try {
            this.mDownloadLink = new URL(url);
        } catch (MalformedURLException e) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.SEVERE, null, e);
        }

        byte[] decodedString = Base64.decode(logo, Base64.DEFAULT);
        this.mLogo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public Bitmap getLogo() {
        return mLogo;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getPluginName() {
        return mPluginName;
    }

    public URL getDownloadLink() {
        return mDownloadLink;
    }
}
