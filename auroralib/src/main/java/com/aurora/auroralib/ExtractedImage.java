package com.aurora.auroralib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

/**
 * Class representing an Image extracted from Files by Aurora. An ExtractedImage contains the
 * base64 encode image and possibly a caption.
 * <p>
 * Use {@link #getBitmap()} to retrieve the bitmap of the image.
 */
public class ExtractedImage {

    /**
     * Base 64 encode image
     */
    private String mBase64EncodedImage;

    /**
     * Caption of the image
     */
    private String mCaption;

    /**
     * Constructor to create a base64Encoded image
     *
     * @param base64EncodedImage the base64 encode String
     */
    public ExtractedImage(@NonNull final String base64EncodedImage) {
        mBase64EncodedImage = base64EncodedImage;
        mCaption = "";
    }

    /**
     * Constructor to create a base64Encoded image withØ a caption
     *
     * @param base64EncodedImage the base64 encode String
     * @param caption            the caption String
     */
    @SuppressWarnings("unused")
    public ExtractedImage(@NonNull final String base64EncodedImage,
                          @NonNull final String caption) {
        mBase64EncodedImage = base64EncodedImage;
        mCaption = caption;
    }

    /**
     * Copy constructor for a deep copy of an ExtractedImage
     *
     * @param extractedImage Image that needs copying
     */
    @SuppressWarnings("WeakerAccess")
    public ExtractedImage(@NonNull final ExtractedImage extractedImage) {
        mBase64EncodedImage = extractedImage.mBase64EncodedImage;
        mCaption = extractedImage.mCaption;
    }

    /**
     * Returns the raw base64 encoded image. In odd cases the String may not contain an image but
     * instead be an empty String.
     *
     * @return the base64 encoded image
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public String getBase64EncodedImage() {
        return mBase64EncodedImage;
    }

    /**
     * Sets the base64 encoded image of the {@link ExtractedImage}.
     *
     * @param base64EncodedImage String of the base64 encoded image
     */
    @SuppressWarnings("unused")
    public void setBase64EncodedImage(@NonNull final String base64EncodedImage) {
        mBase64EncodedImage = base64EncodedImage;
    }

    /**
     * Returns the decoded image as Bitmap. Can be null in special cases.
     *
     * @return the Bitmap of the image
     */
    @SuppressWarnings({"unused"})
    @Nullable
    public Bitmap getBitmap() {
        InputStream stream = new ByteArrayInputStream(Base64.decode(mBase64EncodedImage.getBytes()
                , Base64.DEFAULT));
        return BitmapFactory.decodeStream(stream);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    @NonNull
    public String getCaption() {
        if (mCaption == null) {
            return "";
        }
        return mCaption;
    }

    /**
     * Sets the caption of the {@link ExtractedImage}.
     *
     * @param caption String of the caption
     */
    @SuppressWarnings("unused")
    public void setCaption(@NonNull final String caption) {
        mCaption = caption;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExtractedImage other = (ExtractedImage) o;

        boolean equals;

        equals = this.getBase64EncodedImage().equals(other.getBase64EncodedImage());
        equals &= this.getCaption().equals(other.getCaption());

        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBase64EncodedImage(), getCaption());
    }
}
