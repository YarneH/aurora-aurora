package com.aurora.auroralib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Image {

    private String mBase64EncodedImage;

    private String mCaption;

    /**
     * Constructor to create a base64Encoded image
     *
     * @param base64EncodedImage the base64 encode String
     */
    public Image(@NonNull String base64EncodedImage) {
        mBase64EncodedImage = base64EncodedImage;
    }

    /**
     * Constructor to create a base64Encoded image with a caption
     * @param base64EncodedImage the base64 encode String
     * @param caption           the caption String
     */
    public Image(@NonNull String base64EncodedImage, @Nullable String caption) {
        mBase64EncodedImage = base64EncodedImage;
        mCaption = caption;
    }

    /**
     * Get the raw base64 encode image
     *
     * @return the base64 encoded image
     */
    public String getBase64EncodedImage() {
        return mBase64EncodedImage;
    }

    /**
     * Get the decoded image as Bitmap
     *
     * @return the Bitmap of the image
     */
    @SuppressWarnings("unused")
    public Bitmap getImage() {
        InputStream stream = new ByteArrayInputStream(Base64.decode(mBase64EncodedImage.getBytes()
                , Base64.DEFAULT));
        return BitmapFactory.decodeStream(stream);
    }

    public void setBase64EncodedImage(String base64EncodedImage) {
        this.mBase64EncodedImage = base64EncodedImage;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        this.mCaption = caption;
    }
}
