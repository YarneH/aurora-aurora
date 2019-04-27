package com.aurora.market;

import android.media.Image;

import java.io.Serializable;
import java.net.URL;

/**
 * A Class representing a Plugin which can be downloaded from the PluginMarket
 */
public class MarketPlugin implements Serializable {
    Image mLogo;
    String mPluginName;
    String mDescription;
    URL mDownloadLink;


    /**
     * Dummy constructor for testing UI
     * @param logo
     * @param name
     * @param description
     * @param url
     */
    public MarketPlugin(Image logo, String name, String description, URL url){
        this.mLogo = logo;
        this.mPluginName = name;
        this.mDescription = description;
        this.mDownloadLink = url;
    }

    public Image getLogo() {
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
