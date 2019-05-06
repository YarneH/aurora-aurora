package com.aurora.internalservice.internalprocessor;

import android.util.Log;

import com.aurora.auroralib.ExtractedText;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class TextExtractorTXT implements TextExtractor {

    /**
     * Extracts the text from a .txt file.
     *
     * @param fileRef       a reference to where the file can be found
     * @param extractImages useless parameter for .TXT
     * @return an ExtractedText object without title and one line per paragraph.
     */
    @Override
    public ExtractedText extract(InputStream file, String fileRef, boolean extractImages) {
        ExtractedText extractedText = new ExtractedText(fileRef, null);
        String content = extractStringFromTXT(file, fileRef);
        String[] splitContent = new String[0];
        if (content != null) {
            splitContent = content.split("\n\n");
        }
        if (splitContent.length > 1) {
            extractedText.setTitle(splitContent[0]);
            for (int i = 1; i < splitContent.length; i++) {
                extractedText.addSimpleSection(splitContent[i]);
            }
        } else if (splitContent.length == 1) {
            // Maybe this text file is split with one newline
            splitContent = splitContent[0].split("\n");
            if (splitContent.length > 1) {
                extractedText.setTitle(splitContent[0]);
                for (int i = 1; i < splitContent.length; i++) {
                    extractedText.addSimpleSection(splitContent[i]);
                }
            } else {
                extractedText.addSimpleSection(splitContent[0]);
            }
        }
        return extractedText;
    }


    /**
     * This method reads a txt file and puts all the content in a string
     *
     * @param inputStream this stream will be used to read the text
     * @param fileRef     the filename for error handling
     * @return the extracted plain text
     */
    private String extractStringFromTXT(InputStream inputStream, String fileRef) {
        try {
            // read fills buffer with data and returns
            Reader reader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(reader);

            StringBuilder outcome = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                outcome.append(line);
                outcome.append('\n');
            }
            br.close();
            Log.d("TXT", outcome.toString());
            return outcome.toString();
        } catch (FileNotFoundException ex) {
            Log.e("Unable to open file", "Unable to open file '" + fileRef + "'" + ex.getLocalizedMessage());
        } catch (IOException ex) {
            Log.e("Error reading file", "Error reading file'" + fileRef + "' " + ex.getLocalizedMessage());
        }
        return null;

    }
}
