package com.aurora.internalservice.internalcache;

import com.aurora.auroralib.PluginObject;
import com.aurora.util.MockContext;
import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class InternalCacheUnitTest {

    private static InternalCache mInternalCache;

    @BeforeClass
    public static void initialize() {
        mInternalCache = new InternalCache(new MockContext());
    }

    @Test
    public void InternalCache_cacheFile_shouldWriteObjectToFile() {
        // Create arguments
        String fileRef = "testFile.pdf";
        String title = "title";
        String text = "text";
        PluginObject pluginObject = new DummyPluginObject(title, text);
        String uniquePluginName = "DummyPlugin";

        // Call method under test
        mInternalCache.cacheFile(fileRef, pluginObject, uniquePluginName);

        // Check that file has been written to the right location
        File cachedFile = new File("testFile.aur");

        Assert.assertTrue(cachedFile.exists());

        // Read file
        try (BufferedReader reader = new BufferedReader(new FileReader(cachedFile))) {
            Gson gson = new Gson();
            DummyPluginObject readObject = gson.fromJson(reader, DummyPluginObject.class);

            Assert.assertEquals(title, readObject.getTitle());
            Assert.assertEquals(text, readObject.getText());
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Something went wrong while reading the file!");
            e.printStackTrace();
        }
    }

    @Test
    public void InternalCache_cacheFile_shouldReturnTrueOnSuccess() {
        // Create arguments
        String fileRef = "testFile.pdf";
        String title = "title";
        String text = "text";
        PluginObject pluginObject = new DummyPluginObject(title, text);
        String uniquePluginName = "DummyPlugin";

        // Call method under test
        boolean cacheResult = mInternalCache.cacheFile(fileRef, pluginObject, uniquePluginName);

        Assert.assertTrue(cacheResult);
    }

    @Test
    public void InternalCache_checkCacheForProcessedFile_shouldReturnProcessedFileName() {
        // First cache an element
        String fileRef = "testFile.pdf";
        String title = "title";
        String text = "text";
        PluginObject pluginObject = new DummyPluginObject(title, text);
        String uniquePluginName = "DummyPlugin";
        mInternalCache.cacheFile(fileRef, pluginObject, uniquePluginName);

        // Check if the cache has it
        String cachedFile = mInternalCache.checkCacheForProcessedFile(fileRef, uniquePluginName);

        // The file should be cached so the result should be equal to fileRef
        Assert.assertEquals(fileRef, cachedFile);
    }

    private class DummyPluginObject extends PluginObject {
        // Dummy attributes
        String title;
        String text;

        public DummyPluginObject(String title, String text) {
            this.title = title;
            this.text = text;
        }

        public String getTitle() {
            return title;
        }

        public String getText() {
            return text;
        }
    }
}
