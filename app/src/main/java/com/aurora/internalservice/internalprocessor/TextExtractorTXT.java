package com.aurora.internalservice.internalprocessor;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextExtractorTXT implements TextExtractor {

    /**
     * Extracts the text from a .txt file.
     * @param fileRef a reference to where the file can be found
     * @return an ExtractedText object without title and one line per paragraph.
     * @throws IOException if fileRef is invalid or the permissions to the file are insufficient
     */
    @Override
    public ExtractedText extract(String fileRef) throws IOException {
        File file = new File(fileRef);
        FileReader fr = null;
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException e) {
            Log.d("TextExtractorTXT", "Could not find file");
        }
        List<String> paragraphs;
        try (BufferedReader br = new BufferedReader(fr)) {
            paragraphs = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                //process the line
                paragraphs.add(line);
            }
        }
        return new ExtractedText(null, paragraphs);
    }
}
