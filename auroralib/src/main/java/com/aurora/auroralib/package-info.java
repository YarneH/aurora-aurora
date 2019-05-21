/**
 * This package contains the core functionality of Auroralib. All communication between Aurora
 * and plugin goes through this package.
 * <p>
 * For in depth documentation on how to create a new plugin for Aurora from scratch we refer you to
 * <a href="https://github.ugent.be/Aurora/basicplugin/wiki/Getting-Started">Getting Started</a>.
 *
 * <p>
 * The core concepts are as follows:
 * </p>
 * <p>
 * A plugins processor should extend from the abstract class
 * {@link com.aurora.auroralib.ProcessorCommunicator} and implement the method
 * <p>
 * {@link com.aurora.auroralib.ProcessorCommunicator#process(com.aurora.auroralib.ExtractedText)}.
 * This method takes as input an {@link com.aurora.auroralib.ExtractedText} object which
 * contains everything that could be extracted from a file by Aurora. This method will be
 * automatically called when you call the
 * {@link com.aurora.auroralib.ProcessorCommunicator#pipeline(com.aurora.auroralib.ExtractedText)}
 * which will automatically call the returned {@link com.aurora.auroralib.PluginObject}.
 *
 * <p>
 * A {@link com.aurora.auroralib.PluginObject} is a dataclass where the dataclass of the
 * plugin should extend from. This object will be stored in the cache by
 * Aurora so make sure all the fields are serializable. Currently adapters exist for
 * {@link edu.stanford.nlp.pipeline.Annotation} and lists of {@link android.graphics.Bitmap}.
 * These can be used by placing an &#64;JsonAdapter({@link com.google.gson.TypeAdapter}.class) in
 * front of the field.
 * </p>
 *
 * <p>
 * Lastly {@link com.aurora.auroralib.ProcessingFailedException} thrown by
 * {@link com.aurora.auroralib.ProcessorCommunicator#process(com.aurora.auroralib.ExtractedText)}
 * are caught by
 * {@link com.aurora.auroralib.ProcessorCommunicator#pipeline(com.aurora.auroralib.ExtractedText)}
 * which will elegantly return to Aurora and display a message to the user that the plugin failed.
 * </p>
 *
 * <p>
 * A plugin is launched by Aurora with an {@link android.content.Intent}. Make sure the
 * manifest of the frontend of your app contains an icon, a label, a description, the required
 * preprocessing services and an intent filter for
 * {@link com.aurora.auroralib.Constants#PLUGIN_ACTION} with mimetype *\/*. An example is shown
 * below.
 * </p>
 *
 * <p>
 * <pre>
 * {@code
 * <application
 *         android:allowBackup="true"
 *         android:icon="@mipmap/ic_launcher"
 *         android:label="@string/app_name"
 *         android:description="@string/description"
 *         android:roundIcon="@mipmap/ic_launcher_round"
 *         android:supportsRtl="true"
 *         android:theme="@style/AppTheme">
 *
 *         <!-- Required internal services -->
 *         <meta-data android:name="TEXT_EXTRACTION"
 *             android:value="true" />
 *         <meta-data android:name="IMAGE_EXTRACTION"
 *             android:value="true" />
 *         <meta-data android:name="NLP_TOKENIZE"
 *             android:value="true" />
 *         <meta-data android:name="NLP_SSPLIT"
 *             android:value="true" />
 *         <meta-data android:name="NLP_POS"
 *             android:value="true" />
 *
 *         <activity android:name=".MainActivity">
 *             <intent-filter>
 *                 <action android:name="com.aurora.auroralib.AURORA_PLUGIN"/>
 *                 <category android:name="android.intent.category.DEFAULT"/>
 *                 <data android:mimeType="*\/*"/>
 *             </intent-filter>
 *         </activity>
 * </application>
 * }
 * </pre>
 * </p>
 *
 * <p>
 * The intent contains a Uri in the data field and the input type
 * ({@link com.aurora.auroralib.Constants#PLUGIN_INPUT_TYPE_EXTRACTED_TEXT} or
 * {@link com.aurora.auroralib.Constants#PLUGIN_INPUT_TYPE_OBJECT}) in the
 * {@link com.aurora.auroralib.Constants#PLUGIN_INPUT_TYPE} extra. The classes
 * {@link com.aurora.auroralib.ExtractedText} and {@link com.aurora.auroralib.PluginObject}
 * contain methods to deserialize the the file referred by the Uri to the correct object. In
 * the case of a {@link com.aurora.auroralib.ExtractedText},
 * {@link com.aurora.auroralib.ProcessorCommunicator#pipeline(com.aurora.auroralib.ExtractedText)}
 * should be called while no processing should need to be done in the case of a
 * {@link com.aurora.auroralib.PluginObject}.
 * </p>
 *
 * <p>
 * Lastly Aurora contains different services that can be called. The caching service is
 * automagically called by
 * {@link com.aurora.auroralib.ProcessorCommunicator#pipeline(com.aurora.auroralib.ExtractedText)}.
 * The translation service can simply be instantiated by creating a new
 * {@link com.aurora.auroralib.translation.TranslationServiceCaller}.
 * </p>
 */
package com.aurora.auroralib;
