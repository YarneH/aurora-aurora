package com.aurora.auroralib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *  Adapter class for serializing {@code List<Bitmap>} in Gson. Annotate the List of Bitmaps
 *  with {@code @JsonAdapter(BitMapListAdapter.class)} to use this adapter.
 *
 */
@SuppressWarnings("unused")
public class BitmapListAdapter extends TypeAdapter {

    private static final String LOG_TAG = BitmapListAdapter.class.getSimpleName();

    /**
     * Writes one JSON value (an array, object, string, number, boolean or null)
     * for {@code value}.
     *
     * @param out   JsonWriter to write to
     * @param value the Java object to write. May be null.
     */
    @Override
    public void write(JsonWriter out, Object value) throws IOException {
        if(!(value instanceof List)) {
            Log.e(LOG_TAG, "Using BitmapListAdapter on an Object that is not a List");
        } else if( !((List) value).isEmpty() && !(((List) value).get(0) instanceof Bitmap)) {
            Log.e(LOG_TAG, "Using BitmapListAdapter on a List that does not contain Bitmaps");
        } else {
            List<Bitmap> bitmapList = (ArrayList<Bitmap>) value;

            // Starts a jsonArray
            out.beginArray();

            // For each bitmap in the list, add it base64 encoded to the array
            for (Bitmap bitmap: bitmapList) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                out.value(Base64.encodeToString(byteArray, Base64.DEFAULT));
            }

            //Ends the jsonArray
            out.endArray();
        }


    }

    /**
     * Reads one JSON value (an array, object, string, number, boolean or null)
     * and converts it to a Java object. Returns the converted object.
     *
     * @param in JsonReader to read from
     * @return the converted Java object. May be null.
     */
    @Override
    public Object read(JsonReader in) throws IOException {

        List<Bitmap> bitmaps = new ArrayList<>();

        // Begin reading the array
        in.beginArray();

        // For each base64 encoded String in the list, convert it to a Bitmap
        while (in.hasNext()) {
            InputStream stream = new ByteArrayInputStream(Base64.decode(in.nextString(),
                    Base64.DEFAULT));
            bitmaps.add(BitmapFactory.decodeStream(stream));
        }

        // End reading the array
        in.endArray();

        return bitmaps;
    }
}
