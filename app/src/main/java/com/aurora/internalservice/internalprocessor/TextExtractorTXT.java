package com.aurora.internalservice.internalprocessor;

import android.util.Log;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TextExtractorTXT implements TextExtractor {

    /**
     * Extracts the text from a .txt file.
     * @param fileRef a reference to where the file can be found
     * @return an ExtractedText object without title and one line per paragraph.
     */
    @Override
    public ExtractedText extract(String fileRef) {
//        File file = new File(fileRef);
//        FileReader fr = null;
//        List<String> paragraphs = new ArrayList<>();
//        try {
//            fr = new FileReader(file);
//        } catch (FileNotFoundException e) {
//            Log.d("TextExtractorTXT", "Could not find file");
//        }
//        try (BufferedReader br = new BufferedReader(fr)) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                //process the line
//                paragraphs.add(line);
//            }
//        } catch (IOException e) {
//            Log.d("TextExtractorTXT", "Could not read file");
//        }
//        return new ExtractedText(null, paragraphs);
        // pass the path to the file as a parameter
        File file = new File(fileRef);
        List<String> paragraphs = new ArrayList<>();
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()){
                paragraphs.add(sc.nextLine());
            }
        } catch (FileNotFoundException e) {
            Log.d("TextExtractorTXT", e.getLocalizedMessage());
        }
        return new ExtractedText(null, paragraphs);
    }
}
