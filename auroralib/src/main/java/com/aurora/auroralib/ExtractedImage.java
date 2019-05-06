package com.aurora.auroralib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ExtractedImage {

    private String mBase64EncodedImage;

    private String mCaption;

    /**
     * Constructor to create a base64Encoded image
     *
     * @param base64EncodedImage the base64 encode String
     */
    public ExtractedImage(@NonNull final String base64EncodedImage) {
        mBase64EncodedImage = base64EncodedImage;
    }

    /**
     * Constructor to create a base64Encoded image with a caption
     * @param base64EncodedImage the base64 encode String
     * @param caption           the caption String
     */
    public ExtractedImage(@NonNull final String base64EncodedImage,
                          @Nullable final String caption) {
        mBase64EncodedImage = base64EncodedImage;
        mCaption = caption;
    }

    /**
     * Copy constructor for a deep copy of an ExtractedImage
     *
     * @param extractedImage Image that needs copying
     */
    public ExtractedImage(final ExtractedImage extractedImage) {
        mBase64EncodedImage = extractedImage.mBase64EncodedImage;
        mCaption = extractedImage.mCaption;
    }

    /**
     * Returns the raw base64 encoded image
     *
     * @return the base64 encoded image
     */
    public @NonNull String getBase64EncodedImage() {
        return mBase64EncodedImage;
    }

    /**
     * Returns the decoded image as Bitmap
     *
     * @return the Bitmap of the image
     */
    @SuppressWarnings("unused")
    public @NonNull Bitmap getBitmap() {
        InputStream stream = new ByteArrayInputStream(Base64.decode(mBase64EncodedImage.getBytes()
                , Base64.DEFAULT));
        return BitmapFactory.decodeStream(stream);
    }

    public void setBase64EncodedImage(@NonNull final String base64EncodedImage) {
        this.mBase64EncodedImage = base64EncodedImage;
    }

    @SuppressWarnings("unused")
    public @Nullable String getCaption() {
        return mCaption;
    }

    public void setCaption(@Nullable final String caption) {
        this.mCaption = caption;
    }
}
