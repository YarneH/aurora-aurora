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
     * Value used for the Intent Extra key PLUGIN_INPUT_TYPE to signal that the file is an
     * ExtractedText
     */
    public static final String PLUGIN_INPUT_TYPE_EXTRACTED_TEXT = "PLUGIN_INPUT_TYPE_EXTRACTED_TEXT";

    /**
     * Value used for the Intent Extra key PLUGIN_INPUT_TYPE to signal that the file is a
     * PluginObject
     */
    public static final String PLUGIN_INPUT_TYPE_OBJECT = "PLUGIN_INPUT_TYPE_OBJECT";

    /**
     * Intent Action used to indicate that a plugin failed
     */
    public static final String PLUGIN_PROCESSING_FAILED_ACTION = "com.aurora.auroralib.AURORA_PLUGIN_FAILED";

    /**
     * Key used for the reason why a plugin failed
     */
    public static final String PLUGIN_PROCESSING_FAILED_REASON = "reason";

    /**
     * Private constructor to prevent instantiation (the class only contains constants)
     */
    private Constants() {
    }

}
