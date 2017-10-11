package com.example.richard.glassapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

/**
 * Created by richard on 17. 3. 7.
 */

public final class GlassAppPreferenceActivity extends PreferenceActivity {
    /** The ID for the Read Me dialog. */
    private static final int DIALOG_READ_ME = 1;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Loads the preferences from an XML resource.
        addPreferencesFromResource(R.xml.preference);

        // Handles Read Me.
        Preference pref =
                findPreference(getText(R.string.preference_key_read_me));
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference pref) {
                showDialog(DIALOG_READ_ME);
                return true;
            }
        });

    }

    @Override
    protected Dialog onCreateDialog(final int id) {
        if (id != DIALOG_READ_ME) {
            Log.w(Constants.LOG_TAG, "Not a valid dialog id: " + id);
            return null;
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.cancel();
            }
        };
        return new AlertDialog.Builder(this)
                .setMessage(R.string.preference_option_read_me_txt)
                .setTitle(R.string.preference_option_read_me)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(android.R.string.ok, listener)
                .create();
    }
}