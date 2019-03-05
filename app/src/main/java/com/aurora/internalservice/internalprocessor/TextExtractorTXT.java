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
     */
    @Override
    public ExtractedText extract(String fileRef) {
        File file = new File(fileRef);
        FileReader fr = null;
        List<String> paragraphs = new ArrayList<>();
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException e) {
            Log.d("TextExtractorTXT", "Could not find file");
        }
        try (BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                //process the line
                paragraphs.add(line);
            }
        } catch (IOException e) {
            Log.d("TextExtractorTXT", "Could not read file");
        }
        return new ExtractedText(null, paragraphs);
    }
}
