package com.aurora.market;

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
    //private static final long serialVersionUID = 1;
    // TODO: Change mLogo to Image!!
    private String mLogo;
    private String mPluginName;
    private String mDescription;
    private URL mDownloadLink;


    /**
     * Dummy constructor for testing UI
     * @param logo
     * @param name
     * @param description
     * @param url
     */
    public MarketPlugin(String logo, String name, String description, String url){
        // TODO: Get image!
        this.mLogo = logo;
        this.mPluginName = name;
        this.mDescription = description;
        try {
            this.mDownloadLink = new URL(url);
        } catch (MalformedURLException e) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.SEVERE, null, e);
        }
    }

    public Bitmap getLogo() {

        byte[] decodedString = Base64.decode(mLogo, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
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
