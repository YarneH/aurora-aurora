package com.aurora.auroralib;

public class Constants {
    /**
     * Intent Action used to launch plugins
     */
    public static final String PLUGIN_ACTION = "com.aurora.auroralib.AURORA_PLUGIN";

    /**
     * Key used for the Intent Extra to pass processed text to a plugin
     */
    public static final String PLUGIN_INPUT_TEXT = "PLUGIN_INPUT_TEXT";

    /**
     * Key used for the Intent Extra to pass processed text to a plugin (in the form of an ExtractedText object)
     */
    public static final String PLUGIN_INPUT_EXTRACTED_TEXT = "PLUGIN_INPUT_EXTRACTED_TEXT";


    /**
     * Key used for the Intent Extra to pass processed text to a plugin
     */
    public static final String PLUGIN_INPUT_OBJECT = "PLUGIN_INPUT_OBJECT";


    /**
     * Key used for the Plugin Activity's Response to pass plugin object back to Aurora
     */
    public static final String PLUGIN_OUTPUT_OBJECT = "PLUGIN_OUTPUT_OBJECT";

}
