package com.example.richard.glassapp;

import android.content.ContentValues;
import android.content.Context;

import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

/**
 * Created by richard on 17. 3. 7.
 */

public final class GlassAppRegistrationInformation extends RegistrationInformation {
    /**
     * The application context.
     */
    private final Context context;

    /**
     * Uses control API version
     */
    private static final int CONTROL_API_VERSION = 4;

    /**
     * Creates sensor registration object.
     *
     * @param context The context.
     */
    protected GlassAppRegistrationInformation(final Context context) {
        this.context = context;
    }

    @Override
    public int getRequiredControlApiVersion() {
        return CONTROL_API_VERSION;
    }

    @Override
    public int getRequiredSensorApiVersion() {

        return RegistrationInformation.API_NOT_REQUIRED;
    }

    @Override
    public int getRequiredNotificationApiVersion() {

        return RegistrationInformation.API_NOT_REQUIRED;
    }

    @Override
    public int getRequiredWidgetApiVersion() {
        return RegistrationInformation.API_NOT_REQUIRED;
    }

    @Override
    public ContentValues getExtensionRegistrationConfiguration() {
        String iconHostapp =
                ExtensionUtils.getUriString(context, R.drawable.icon);
        String iconExtension =
                ExtensionUtils.getUriString(context, R.drawable.icon_extension);

        ContentValues values = new ContentValues();
        values.put(Registration.ExtensionColumns.CONFIGURATION_ACTIVITY,
                GlassAppPreferenceActivity.class.getName());
        values.put(Registration.ExtensionColumns.CONFIGURATION_TEXT,
                context.getString(R.string.configuration_text));
        values.put(Registration.ExtensionColumns.NAME,
                context.getString(R.string.extension_name));
        values.put(Registration.ExtensionColumns.EXTENSION_KEY, Constants.EXTENSION_KEY);
        values.put(Registration.ExtensionColumns.HOST_APP_ICON_URI, iconHostapp);
        values.put(Registration.ExtensionColumns.EXTENSION_ICON_URI, iconExtension);
        values.put(Registration.ExtensionColumns.NOTIFICATION_API_VERSION,
                getRequiredNotificationApiVersion());
        values.put(Registration.ExtensionColumns.PACKAGE_NAME, context.getPackageName());
        return values;
    }

    @Override
    public boolean isDisplaySizeSupported(final int width, final int height) {
        GlassAppScreenSize size = new GlassAppScreenSize(context);
        return (size.equals(width, height));
    }
}