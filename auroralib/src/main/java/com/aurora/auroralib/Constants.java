package com.aurora.auroralib;

/**
 * A class containing constants used for interaction between aurora and the plugins.
 */
public final class Constants {
    /**
     * Intent Action used to launch plugins
     */
    public static final String PLUGIN_ACTION = "com.aurora.auroralib.AURORA_PLUGIN";

    /**
     * Key used for the Plugin Activity's Response to pass plugin object back to Aurora
     */
    public static final String PLUGIN_OUTPUT_OBJECT = "PLUGIN_OUTPUT_OBJECT";

    /**
     * Key used for the Intent Extra to signal the data type the uri, found in the data field,
     * refers to.
     */
    public static final String PLUGIN_INPUT_TYPE = "PLUGIN_INPUT_TYPE";

    /**
     * Key used for the Intent Extra value to that the file is ExtractedText
     */
    public static final String PLUGIN_INPUT_TYPE_EXTRACTED_TEXT = "PLUGIN_INPUT_TYPE_EXTRACTED_TEXT";

    /**
     * Key used for the Intent Extra value to that the file is PluginObject
     */
    public static final String PLUGIN_INPUT_TYPE_OBJECT = "PLUGIN_INPUT_TYPE_OBJECT";

    /**
     * Private constructor to prevent instantiation (the class only contains constants)
     */
    private Constants() {
    }

}
