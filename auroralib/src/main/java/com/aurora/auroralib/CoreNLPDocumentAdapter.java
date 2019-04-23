package com.aurora.auroralib;

import com.google.gson.JsonParser;
import com.google.protobuf.util.JsonFormat;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import edu.stanford.nlp.pipeline.CoreNLPProtos;

/**
 * <p>
 *  An adapter class for converting CoreNLPProtos.Document based on Google's ProtoBuf scheme to
 *  Json with Gson and back.
 * </p>
 * <p>
 *     Fields that are of CoreNLPProtos.Document type should be annotated with @JsonAdapter
 *     (CoreNLPDocumentAdapter.class) and the correct adapter will then automatically be called.
 * </p>
 */
public class CoreNLPDocumentAdapter extends TypeAdapter {


    /**
     * Writes one JSON value (an array, object, string, number, boolean or null)
     * for {@code value}.
     *
     * @param out
     * @param value the Java object to write. May be null.
     */
    @Override
    public void write(JsonWriter out, Object value) throws IOException {
        // Call the printer of the JsonFormat class to convert the Person proto message to Json
        out.jsonValue(JsonFormat.printer().print((CoreNLPProtos.Document)value));
    }

    /**
     * Reads one JSON value (an array, object, string, number, boolean or null)
     * and converts it to a Java object. Returns the converted object.
     *
     * @param in
     * @return the converted Java object. May be null.
     */
    @Override
    public Object read(JsonReader in) throws IOException {
        // Create a builder for the Person message
        CoreNLPProtos.Document.Builder personBuilder = CoreNLPProtos.Document.newBuilder();
        // Use the JsonFormat class to parse the json string into the builder object
        // The Json string will be parsed fromm the JsonReader object
        JsonParser jsonParser = new JsonParser();
        JsonFormat.parser().merge(jsonParser.parse(in).toString(), personBuilder);
        // Return the built Person message
        return personBuilder.build();
    }
}
