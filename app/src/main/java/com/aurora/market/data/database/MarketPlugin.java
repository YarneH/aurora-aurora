package com.aurora.market.data.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
     * A byte array representing the logo of the plugin
     */
    private byte[] mLogo;
    /**
     * The name of the plugin
     */
    private String mPluginName;
    /**
     * The description of the plugin
     */
    private String mDescription;
    /**
     * The creator of the plugin
     */
    private String mCreator;
    /**
     * The version of the plugin
     */
    private String mVersion;
    /**
     * The unique name of the plugin
     */
    private String mUniqueName;

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
    public MarketPlugin(byte[] logo, String name, String description, String creator, String version, String uniqueName,
                        String url){
        this.mPluginName = name;
        this.mUniqueName = uniqueName;
        this.mDescription = description;
        try {
            this.mDownloadLink = new URL(url);
        } catch (MalformedURLException e) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.SEVERE, null, e);
        }
        this.mLogo = logo;
        this.mVersion = version;
        this.mCreator = creator;
    }

    /**
     * Default getter
     * @return the logo of the MarketPlugin
     */
    public Bitmap getLogo() {
        if (mLogo != null) {
            return BitmapFactory.decodeByteArray(mLogo, 0, mLogo.length);
        } else {
            return null;
        }
    }

    /**
     * Default getter
     * @return the logo of the MarketPlugin
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Default getter
     * @return the plugin name of the MarketPlugin
     */
    public String getPluginName() {
        return mPluginName;
    }

    /**
     * Default getter
     * @return the downloadlink of the MarketPlugin, as URL
     */
    public URL getDownloadLink() {
        return mDownloadLink;
    }

    /**
     * Default getter
     * @return the name of the creator of the MarketPlugin
     */
    public String getCreator() {
        return mCreator;
    }

    /**
     * Default getter
     * @return the versioncode of the MarketPlugin
     */
    public String getVersion() {
        return mVersion;
    }

    /**
     * Default getter
     * @return the unique name of the plugin, used by the Package Installer
     */
    public String getUniqueName() {
        return mUniqueName;
    }
}
