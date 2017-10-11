package com.example.richard.glassapp;

/**
 * Created by richard on 17. 3. 7.
 */

public final class Constants {
    /** The extension key. */
    public static final String EXTENSION_KEY =
            Constants.class.getPackage().getName() + ".key";

    /** The log tag. */
    public static final String LOG_TAG = "SampleCameraExtension";

    public static final String FILE_UPLOAD_URL = "http://143.248.39.254:5000/api/upload_all";

    /** Hides the default constructor. */
    private Constants() {
    }
}
