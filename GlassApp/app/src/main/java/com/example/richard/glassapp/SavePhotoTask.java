package com.example.richard.glassapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by richard on 17. 3. 7.
 */

public final class SavePhotoTask extends AsyncTask<byte[], String, String> {
    private final String fileName;
    private final File saveFolder;
    public SavePhotoTask(File folder,String name) {
        this.saveFolder = folder;
        this.fileName = name;
    }
    @Override
    protected String doInBackground(byte[]... jpeg) {
        File photo = new File(saveFolder,fileName);

        if (photo.exists()) {
            photo.delete();
        }

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(photo.getPath());
            fileOutputStream.write(jpeg[0]);
            Log.d(Constants.LOG_TAG, "Saved photo to " + fileName);
        } catch (java.io.IOException e) {
            Log.e(Constants.LOG_TAG, "Exception in saving photo : write", e);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (java.io.IOException e) {
                    Log.e(Constants.LOG_TAG, "Exception in saving photo : close", e);
                }
            }
        }

        return(null);
    }
}
