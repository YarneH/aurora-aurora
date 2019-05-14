package com.aurora.internalservice.internalcache;

import com.aurora.auroralib.PluginObject;
import com.aurora.util.MockContext;
import com.google.gson.Gson;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InternalCacheUnitTest {

    private static InternalCache mInternalCache;

    @BeforeClass
    public static void initialize() {
        mInternalCache = new InternalCache(new MockContext());
        mInternalCache.clear();
    }

    @Test
    public void InternalCache_cacheFile_shouldWriteObjectToFile() {
        // Create arguments
        String fileRef = "-123456_testFile.pdf";
        String title = "title";
        String text = "text";
        String pluginObject = new DummyPluginObject1(title, text).toJSON();
        String uniquePluginName = "DummyPlugin";

        // Call method under test
        mInternalCache.cacheFile(fileRef, pluginObject, uniquePluginName);

        // Check that file has been written to the right location
        File cachedFile = new File("-123456_testFile.aur");

        Assert.assertTrue(cachedFile.exists());

        // Read file
        try (BufferedReader reader = new BufferedReader(new FileReader(cachedFile))) {
            Gson gson = new Gson();
            DummyPluginObject1 readObject = gson.fromJson(reader, DummyPluginObject1.class);

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
        String fileRef = "-123456_testFile.pdf";
        String title = "title";
        String text = "text";
        String pluginObject = new DummyPluginObject1(title, text).toJSON();
        String uniquePluginName = "DummyPlugin";

        // Call method under test
        boolean cacheResult = mInternalCache.cacheFile(fileRef, pluginObject, uniquePluginName);

        Assert.assertTrue(cacheResult);
    }

    @Test
    public void InternalCache_cacheFile_shouldReturnFalseOnFailure() {
        // Create arguments
        // Invalid filename works on windows. Linux should also have a problem with the slashes
        String invalidFileRef = "-1234_te///st??File!.pdf";
        String title = "title";
        String text = "text";
        String pluginObject = new DummyPluginObject1(title, text).toJSON();
        String uniquePluginName = "DummyPlugin";

        // Call method under test
        boolean cacheResult = mInternalCache.cacheFile(invalidFileRef, pluginObject, uniquePluginName);

        Assert.assertFalse(cacheResult);
    }

    @Test
    public void InternalCache_checkCacheForProcessedFile_shouldReturnProcessedFileName() {
        // First cache an element
        String fileRef = "-123456_testFile.pdf";
        String title = "title";
        String text = "text";
        String pluginObject = new DummyPluginObject1(title, text).toJSON();
        String uniquePluginName = "DummyPlugin";
        mInternalCache.cacheFile(fileRef, pluginObject, uniquePluginName);

        // Check if the cache has it
        CachedFileInfo cachedFile = mInternalCache.checkCacheForProcessedFile(fileRef, uniquePluginName);

        // Create cached file info that is expected
        CachedFileInfo expectedCachedFile = new CachedFileInfo(fileRef, uniquePluginName);

        // The file should be cached so the result should be equal to fileRef
        Assert.assertEquals(expectedCachedFile, cachedFile);
    }

    @Test
    public void InternalCache_getFullCache_shouldGetAtMostNCachedFiles() {
        // Set amount
        int amount = 3;

        // Add 5 cached Files
        addCacheFiles();

        // Call the method under test
        List<CachedFileInfo> cachedFiles = mInternalCache.getFullCache(amount);

        Assert.assertEquals(amount, cachedFiles.size());
    }

    @Test
    public void InternalCache_getFullCache_shouldGetAllCachedFiles() {
        // We will add 5 files
        int amount = 5;

        // Add 5 cached files
        addCacheFiles();

        // Call the method under test
        List<CachedFileInfo> cachedFiles = mInternalCache.getFullCache();

        Assert.assertEquals(amount, cachedFiles.size());
    }

    @Test
    public void InternalCache_retrieveFileFromCache_shouldGetFileRepresentation() {
        // We will add 5 + 1 file
        int amount = 6;

        // Add 5 cached files
        addCacheFiles();

        // Add one yourself
        String fileRef = "-1234567_award-winning-text.pdf";
        String title = "A Good Title";
        String text = "This is a very good text. Nobel prize worthy, even!";
        String pluginName = "DummyPlugin";

        String object1 = new DummyPluginObject1(title, text).toJSON();
        mInternalCache.cacheFile(fileRef, object1, pluginName);

        // Now, we want to retrieve the text again
        CachedProcessedFile cachedProcessedFile = mInternalCache.retrieveFile(fileRef, pluginName);

        // Reconstruct the object from the json
        DummyPluginObject1 reconstructedObject =
                DummyPluginObject1.fromJson(cachedProcessedFile.getJsonRepresentation(), DummyPluginObject1.class);

        // Assert that the two objects are equal
        Assert.assertEquals(title, reconstructedObject.getTitle());
        Assert.assertEquals(text, reconstructedObject.getText());
    }

    @Test
    public void InternalCache_removeFile_shouldRemoveFileIfFileInCache() {
        // Add multiple files to cache
        addCacheFiles();

        // Add one yourself
        String fileRef = "-123456_award-winning-text.pdf";
        String title = "A Good Title";
        String text = "This is a very good text. Nobel prize worthy, even!";
        String pluginName = "DummyPlugin";

        String object1 = new DummyPluginObject1(title, text).toJSON();
        mInternalCache.cacheFile(fileRef, object1, pluginName);

        // Now we want to remove this one file
        boolean successful = mInternalCache.removeFile(fileRef, pluginName);

        Assert.assertTrue(successful);
    }

    @Test
    public void InternalCache_removeFile_shouldNotRemoveFileIfFileIsNotInCache() {
        // Add multiple files to cache
        addCacheFiles();

        // Add one yourself
        String fileRef = "-123456_award-winning-text.pdf";
        String pluginName = "DummyPlugin";

        // Now we want to remove this one file
        boolean successful = mInternalCache.removeFile(fileRef, pluginName);

        Assert.assertFalse(successful);
    }

    @Test
    public void InternalCache_removeFilesByPlugin_shouldRemoveAllFilesByPlugin() {
        // Add multiple files to cache
        addCacheFiles();

        // Add multiple files by another plugin
        String fileRef1 = "-123456_award-winning-text.pdf";
        String fileRef2 = "-122455_bad-text.pdf";
        String title1 = "A Good Title";
        String title2 = "A Bad Title";
        String text1 = "This is a very good text. Nobel prize worthy, even!";
        String text2 = "This text will never win any prize!";
        String pluginName = "DummyPlugin";

        String object1 = new DummyPluginObject1(title1, text1).toJSON();
        String object2 = new DummyPluginObject1(title2, text2).toJSON();
        mInternalCache.cacheFile(fileRef1, object1, pluginName);
        mInternalCache.cacheFile(fileRef2, object2, pluginName);

        // Remove all files with the given plugin name
        boolean successful = mInternalCache.removeFilesByPlugin(pluginName);

        Assert.assertTrue(successful);
    }

    @Test
    public void InternalCache_removeFilesByPlugin_shouldNotRemoveFilesIfPluginNotInCache() {
        // Add multiple files to the cache
        addCacheFiles();
        String pluginName = "DummyPlugin";
        // Try to remove all files with the given plugin name
        boolean successful = mInternalCache.removeFilesByPlugin(pluginName);

        Assert.assertFalse(successful);
    }

    @Test
    public void InternalCache_clear_shouldRemoveAllFilesFromCache() {
        // Add multiple files to the cache
        addCacheFiles();

        // Call clear method to clear the cache
        boolean successful = mInternalCache.clear();

        Assert.assertTrue(successful);
    }

    @Test
    public void InternalCache_clear_shouldReturnTrueIfCacheAlreadyEmpty() {
        boolean successful = mInternalCache.clear();

        Assert.assertTrue(successful);
    }

    @Test
    public void InternalCache_updateCachedFileDate_shouldUpdateDateOfCachedFileInfo() {
        // Add file to cache
        String fileRef1 = "-123_dummyFileRef.docx";
        String object1 = new DummyPluginObject1("Title", "Text").toJSON();
        String pluginName = "com.aurora.dummyplugin";
        mInternalCache.cacheFile(fileRef1, object1, pluginName);

        // Get file from cache
        CachedFileInfo originalFileInfo = mInternalCache.checkCacheForProcessedFile(fileRef1, pluginName);

        // Update date
        mInternalCache.updateCachedFileDate(fileRef1, pluginName);

        // Get file from cache
        CachedFileInfo updatedFile = mInternalCache.checkCacheForProcessedFile(fileRef1, pluginName);

        // Compare file dates
        Assert.assertTrue(originalFileInfo.getLastOpened().before(updatedFile.getLastOpened()));
    }

    @Test
    public void InternalCache_getFullCache_shouldReturnFilesSortedOnDateMostRecent() {
        // Add files to cache
        String fileRef1 = "123_dummyFileRef1.docx";
        String fileRef2 = "456_dummyfileRef2.pdf";
        String object1 = new DummyPluginObject1("Title", "Text").toJSON();
        String object2 = new DummyPluginObject2("Title", 2, "Name").toJSON();
        String pluginName = "com.aurora.dummyplugin";

        mInternalCache.cacheFile(fileRef1, object1, pluginName);

        // Wait for 1 second to cache the file so the dateLastOpened is later
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mInternalCache.cacheFile(fileRef2, object2, pluginName);

        // Get files from cache
        List<CachedFileInfo> cachedFiles = mInternalCache.getFullCache();

        // Assert that the date of the first element is more recent than the date of the second element
        Assert.assertTrue(cachedFiles.get(0).getLastOpened().after(cachedFiles.get(1).getLastOpened()));
    }

    // After every test, reset the cache
    @After
    public void resetCache() {
        mInternalCache.clear();
    }

    /**
     * Helper method that adds 5 files to the cache of different types
     */
    private static void addCacheFiles() {
        String[] filerefs1 = {"-123_fileref1.pdf", "-456_fileref2.docx", "-789_fileref3.pdf"};
        String[] titles1 = {"Title1", "Title2", "Title3"};
        String[] texts1 = {"Text1", "Text2", "Text3"};
        String pluginName1 = "DummyPlugin1";

        for (int i = 0; i < titles1.length; i++) {
            // Create plugin object
            String object1 = new DummyPluginObject1(titles1[i], texts1[i]).toJSON();

            // Add to cache
            mInternalCache.cacheFile(filerefs1[i], object1, pluginName1);
        }

        String[] filerefs2 = {"-123_fileref4.docx", "-456_fileref5.txt"};
        String[] titles2 = {"Title4", "Title5"};
        int[] numbers = {2, 3};
        String[] names = {"Name4", "Name 5"};
        String pluginName2 = "DummyPlugin2";

        for (int i = 0; i < titles2.length; i++) {
            // Create plugin object
            String object2 = new DummyPluginObject2(titles1[i], numbers[i], names[i]).toJSON();

            // Add to cache
            mInternalCache.cacheFile(filerefs2[i], object2, pluginName2);
        }


    }

    private static class DummyPluginObject1 extends PluginObject {
        // Dummy attributes
        private String mTitle;
        private String mText;

        public DummyPluginObject1(String title, String text) {
            super("dummyfilename", "dummyplugin");
            mTitle = title;
            mText = text;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getText() {
            return mText;
        }
    }

    private static class DummyPluginObject2 extends PluginObject {
        // Dummy attributes
        private String mTitle;
        private int mNumber;
        private String mName;

        public DummyPluginObject2(String title, int number, String name) {
            super("dummyfilename", "dummyplugin");
            mTitle = title;
            mNumber = number;
            mName = name;
        }

        public String getTitle() {
            return mTitle;
        }

        public int getNumber() {
            return mNumber;
        }

        public String getName() {
            return mName;
        }
    }

}
