package com.example.richard.glassapp;

import android.util.Log;

import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

/**
 * Created by richard on 17. 3. 7.
 */
public class AppSampleExtensionService extends ExtensionService {

    public static AppSampleExtensionService object = null;
    /** Creates a new instance. */
    public AppSampleExtensionService() {
        super(Constants.EXTENSION_KEY);
        object = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Constants.LOG_TAG, "SampleCameraExtension : onCreate");
    }

    @Override
    protected RegistrationInformation getRegistrationInformation() {
        return new GlassAppRegistrationInformation(this);
    }

    @Override
    protected boolean keepRunningWhenConnected() {
        return false;
    }

    @Override
    public ControlExtension createControlExtension(final String hostAppPackageName) {
        boolean isApiSupported = DeviceInfoHelper
                .isSmartEyeglassScreenSupported(this, hostAppPackageName);
        if (isApiSupported) {
            return new GlassAppCameraControl(this, hostAppPackageName);
        } else {
            Log.d(Constants.LOG_TAG, "Service: not supported, exiting");
            throw new IllegalArgumentException(
                    "No control for: " + hostAppPackageName);
        }
    }

}
