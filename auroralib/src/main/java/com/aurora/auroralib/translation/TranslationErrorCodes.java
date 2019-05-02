package com.aurora.auroralib.translation;

public final class TranslationErrorCodes {
    /**
     * Indicates that the translation operation succeeded
     */
    public static final int TRANSLATION_SUCCESS = 0;

    /**
     * Indicates that the translation operation failed
     */
    public static final int TRANSLATION_FAIL = -1;

    /**
     * Indicates that the kernel could not be instantiated correctly
     */
    public static final int KERNEL_FAIL = -2;

    /**
     * Indicates that a RemoteException occurred
     */
    public static final int REMOTE_FAIL = -3;

    /**
     * Indicates that the translation service could not be reached
     */
    public static final int NOT_REACHED = -1000;

    /**
     * Empty private constructor so people cannot instantiate the class (it it just constants)
     */
    private TranslationErrorCodes() {
    }
}
