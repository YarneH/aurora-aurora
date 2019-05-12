package com.aurora.auroralib.translation;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class of constants indicating results of the translation operation.
 */
public class TranslationResult implements Parcelable {

    /**
     * The reason why the translation failed
     */
    private int mErrorCode;

    /**
     * The translated sentences. Is null when {@link #getErrorCode()} is true
     */
    private String[] mTranslatedSentences;

    public static final Creator<TranslationResult> CREATOR = new Creator<TranslationResult>() {
        @Override
        public TranslationResult createFromParcel(Parcel in) {
            return new TranslationResult(in);
        }

        @Override
        public TranslationResult[] newArray(int size) {
            return new TranslationResult[size];
        }
    };

    protected TranslationResult(Parcel in) {

        mErrorCode = in.readInt();
        mTranslatedSentences = in.createStringArray();

    }

    public TranslationResult(Integer errorCode, String[] translatedSentences) {
        this.mErrorCode = errorCode;
        this.mTranslatedSentences = translatedSentences;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public Integer getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.mErrorCode = errorCode;
    }

    public String[] getTranslatedSentences() {
        return mTranslatedSentences;
    }

    public void setTranslatedSentences(String[] translatedSentences) {
        this.mTranslatedSentences = translatedSentences;
    }


    public static Creator<TranslationResult> getCREATOR() {
        return CREATOR;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(mErrorCode);
        parcel.writeStringArray(mTranslatedSentences);

    }
}
