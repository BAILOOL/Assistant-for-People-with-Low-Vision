package com.example.richard.glassapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;

import com.sony.smarteyeglass.SmartEyeglassControl;
import com.sony.smarteyeglass.extension.util.CameraEvent;
import com.sony.smarteyeglass.extension.util.ControlCameraException;
import com.sony.smarteyeglass.extension.util.SmartEyeglassControlUtils;
import com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by richard on 17. 3. 7.
 */

public final class GlassAppCameraControl extends ControlExtension {
    /**
     * Uses SmartEyeglass API version
     */
    private static final int SMARTEYEGLASS_API_VERSION = 3;
    public final int width;
    public final int height;
    /**
     * The application context.
     */
    private final Context context;
    /**
     * Instance of the Control Utility class.
     */
    private final SmartEyeglassControlUtils utils;
    private boolean saveToSdcard = false;
    private boolean cameraStarted = false;
    private int saveFileIndex;
    private int recordingMode = SmartEyeglassControl.Intents.CAMERA_MODE_STILL;
    private String saveFilePrefix;
    private File saveFolder;
    private int pointX;
    private int pointY;
    private int pointBaseX;

    private Bitmap bitmap = null;
    private String voiceInput = "";

    private static AtomicBoolean requestSent = new AtomicBoolean(false);

    /**
     * Creates an instance of this control class.
     *
     * @param context            The context.
     * @param hostAppPackageName Package name of host application.
     */
    public GlassAppCameraControl(final Context context, final String hostAppPackageName) {
        super(context, hostAppPackageName);
        Log.d(Constants.LOG_TAG, "GlassAppCameraControl");
        this.context = context;
        // Initialize listener for camera events
        SmartEyeglassEventListener listener = new SmartEyeglassEventListener() {
            // When camera operation has succeeded
            // handle result according to current recording mode
            @Override
            public void onCameraReceived(final CameraEvent event) {
                Log.d(Constants.LOG_TAG, "onCameraReceived");
                switch (recordingMode) {
                    case SmartEyeglassControl.Intents.CAMERA_MODE_STILL:
                        Log.d(Constants.LOG_TAG, "Camera Event coming: " + event.toString());
                        break;
                    case SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_HIGH_RATE:
                        Log.d(Constants.LOG_TAG, "Stream Event coming: " + event.toString());
                    case SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_LOW_RATE:
                        Log.d(Constants.LOG_TAG, "Stream Event coming: " + event.toString());
                        break;
                    default:
                        break;
                }
                cameraEventOperation(event);
            }

            // Called when camera operation has failed
            // We just log the error
            @Override
            public void onCameraErrorReceived(final int error) {
                Log.d(Constants.LOG_TAG, "onCameraErrorReceived: " + error);
            }

            // When camera is set to record image to a file,
            // log the operation and clean up
            @Override
            public void onCameraReceivedFile(final String filePath) {
                Log.d(Constants.LOG_TAG, "onCameraReceivedFile: " + filePath);
                updateDisplay();
            }

            // When voice-to-text operation is completed, show result
            @Override
            public void onVoiceTextInput(
                    final int errorCode, final String text) {
                // On success, build a display string that includes the
                // converted input.
                if (errorCode == SmartEyeglassControl.Intents.
                        VOICE_TEXT_INPUT_RESULT_OK) {
                    Log.d(Constants.LOG_TAG, "onVoiceTextInput() : " + text);
                    MainActivity.object.setTxtSpeechInput(text);
                    voiceInput = text;
                    MainActivity.object.setTxtInput(text);
                    utils.disableVoiceTextInput();
                    utils.enableVoiceTextInput();
                }
            }

        };
        utils = new SmartEyeglassControlUtils(hostAppPackageName, listener);
        utils.setRequiredApiVersion(SMARTEYEGLASS_API_VERSION);
        utils.activate(context);

        saveFolder = new File(Environment.getExternalStorageDirectory(), "SampleCameraExtension");
        saveFolder.mkdir();

        Log.d(Constants.LOG_TAG, "SaveFolder: " + saveFolder.getAbsolutePath());

        width = context.getResources().getDimensionPixelSize(R.dimen.smarteyeglass_control_width);
        height = context.getResources().getDimensionPixelSize(R.dimen.smarteyeglass_control_height);
    }

    @Override
    public void onTouch(ControlTouchEvent event) {
        super.onTouch(event);
        int action = event.getAction();
        if (action == Control.TapActions.DOUBLE_TAP){
            if (bitmap == null) return;

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            //Constants.getCameraData.compress(Bitmap.CompressFormat.JPEG, 100, bao);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
            byte [] ba = bao.toByteArray();
            String fileName = saveFilePrefix + String.format("%04d", saveFileIndex) + ".jpg";

            MainActivity.object.runUploading(bitmap, fileName);
        }

    }

    /**
     * Respond to tap on touch pad by triggering camera capture
     */
    /*
    @Override
    public void onTouch(final ControlTouchEvent event) {
        if (event.getAction() == Control.TapActions.SINGLE_TAP) {
            if (recordingMode == SmartEyeglassControl.Intents.CAMERA_MODE_STILL ||
                    recordingMode == SmartEyeglassControl.Intents.CAMERA_MODE_STILL_TO_FILE) {
                if (!cameraStarted) {
                    initializeCamera();
                }
                Log.d(Constants.LOG_TAG, "Select button pressed -> cameraCapture()");
                // Call for camera capture for Still recording modes.
                utils.requestCameraCapture();
            } else {
                if (!cameraStarted) {
                    initializeCamera();
                } else {
                    cleanupCamera();
                }
                updateDisplay();
            }
        }

    }
    */



    @Override
    public void onTap(int action, long timeStamp){
        super.onTap(action, timeStamp);

        if (action == Control.TapActions.SINGLE_TAP) {
            MainActivity.object.playText("Picture being taken");
            if (recordingMode == SmartEyeglassControl.Intents.CAMERA_MODE_STILL ||
                    recordingMode == SmartEyeglassControl.Intents.CAMERA_MODE_STILL_TO_FILE) {
                if (!cameraStarted) {
                    initializeCamera();
                }
                Log.d(Constants.LOG_TAG, "Select button pressed -> cameraCapture()");
                // Call for camera capture for Still recording modes.
                utils.requestCameraCapture();
            } else {
                if (!cameraStarted) {
                    initializeCamera();
                } else {
                    cleanupCamera();
                }
                updateDisplay();
            }
        }

    }



    @Override
    public void onSwipe(int direction) {
        Log.d(Constants.LOG_TAG, "swiped!");
        super.onSwipe(direction);

        /* avoid double swipe */
        boolean cont = requestSent.getAndSet(true);
        if (cont){
            return;
        }
        if (bitmap == null){
            requestSent.set(false);
            return;
        }

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        //Constants.getCameraData.compress(Bitmap.CompressFormat.JPEG, 100, bao);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
        byte [] ba = bao.toByteArray();
        String fileName = saveFilePrefix + String.format("%04d", saveFileIndex) + ".jpg";

        MainActivity.object.runUploading(bitmap, fileName);
    }

    public static void clearRequest(){
        requestSent.set(false);
    }

    /**
     * Call the startCamera, and start video recording or shooting.
     */
    private void initializeCamera() {
        try {
            Time now = new Time();
            now.setToNow();
            // Start camera with filepath if recording mode is Still to file
            if (recordingMode == SmartEyeglassControl.Intents.CAMERA_MODE_STILL_TO_FILE) {
                String filePath = saveFolder + "/" + saveFilePrefix + String.format("%04d", saveFileIndex) + ".jpg";
                saveFileIndex++;
                utils.startCamera(filePath);
            } else {
                // Start camera without filepath for other recording modes
                Log.d(Constants.LOG_TAG, "startCamera ");
                utils.startCamera();
            }
        } catch (ControlCameraException e) {
            Log.d(Constants.LOG_TAG, "Failed to register listener", e);
        }
        Log.d(Constants.LOG_TAG, "onResume: Registered listener");

        cameraStarted = true;
    }

    /**
     * Call the stopCamera, and stop video recording or shooting.
     */
    private void cleanupCamera() {
        utils.stopCamera();
        cameraStarted = false;
    }

    // When app becomes visible, set up camera mode choices
    // and instruct user to begin camera operation
    @Override
    public void onResume() {
        // Note: Setting the screen to be always on will drain the accessory
        // battery. It is done here solely for demonstration purposes.
        setScreenState(Control.Intents.SCREEN_STATE_ON);
        pointX = context.getResources().getInteger(R.integer.POINT_X);
        pointY = context.getResources().getInteger(R.integer.POINT_Y);

        Time now = new Time();
        now.setToNow();
        saveFilePrefix = "samplecamera_" + now.format2445() + "_";
        saveFileIndex = 0;

        // Read the settings for the extension.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        saveToSdcard = prefs.getBoolean(context.getString(R.string.preference_key_save_to_sdcard), true);
        int recMode = Integer.parseInt(prefs.getString(context.getString(R.string.preference_key_recordmode), "2"));
        int preferenceId = R.string.preference_key_resolution_still;

        switch (recMode) {
            case 0: // recording mode is still
                recordingMode = SmartEyeglassControl.Intents.CAMERA_MODE_STILL;
                break;
            case 1: // recording mode is still to file
                recordingMode = SmartEyeglassControl.Intents.CAMERA_MODE_STILL_TO_FILE;
                break;
            case 2: // recording mode is JPGStream Low
                recordingMode = SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_LOW_RATE;
                preferenceId = R.string.preference_key_resolution_movie;
                break;
            case 3: // recording mode is JPGStream High
                recordingMode = SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_HIGH_RATE;
                preferenceId = R.string.preference_key_resolution_movie;
                break;
        }

        // Get and show quality parameters
        int jpegQuality = Integer.parseInt(prefs.getString(
                context.getString(R.string.preference_key_jpeg_quality), "1"));
        int resolution = Integer.parseInt(prefs.getString(
                context.getString(preferenceId), "6"));

        // Set the camera mode to match the setup
        utils.setCameraMode(jpegQuality, resolution, recordingMode);
        utils.enableVoiceTextInput();

        cameraStarted = false;
        updateDisplay();
    }

    // Clean up any open files and reset mode when app is paused.
    @Override
    public void onPause() {
        // Stop camera.
        if (cameraStarted) {
            Log.d(Constants.LOG_TAG, "onPause() : stopCamera");
            cleanupCamera();
        }
        utils.disableVoiceTextInput();
    }

    // Clean up data structures on termination.
    @Override
    public void onDestroy() {
        utils.deactivate();
    }

    /**
     * Received camera event and operation each event.
     *
     * @param event
     */
    private void cameraEventOperation(CameraEvent event) {
        MainActivity.object.playText("Uploading picture");
        if (event.getErrorStatus() != 0) {
            Log.d(Constants.LOG_TAG, "error code = " + event.getErrorStatus());
            return;
        }

        if(event.getIndex() != 0){
            Log.d(Constants.LOG_TAG, "not oparate this event");
            return;
        }

        Bitmap bitmap1 = null;
        byte[] data = null;

        if ((event.getData() != null) && ((event.getData().length) > 0)) {
            data = event.getData();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            bitmap1 = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        }

        if (bitmap == null) {
            Log.d(Constants.LOG_TAG, "bitmap == null");
            return;
        }

        MainActivity.object.previewMedia(bitmap1);
        MainActivity.object.playText("Picture uploaded");

        /*
        if (saveToSdcard == true) {
            String fileName = saveFilePrefix + String.format("%04d", saveFileIndex) + ".jpg";
            new SavePhotoTask(saveFolder,fileName).execute(data);
            saveFileIndex++;
        }
        */

        /*
        if (recordingMode == SmartEyeglassControl.Intents.CAMERA_MODE_STILL) {
            Bitmap basebitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            basebitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
            Canvas canvas = new Canvas(basebitmap);
            Rect rect = new Rect(0, 0, width, height);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            canvas.drawBitmap(bitmap, rect, rect, paint);

            utils.showBitmap(basebitmap);
            return;
        }

        */
        Log.d(Constants.LOG_TAG, "Camera frame was received : #" + saveFileIndex);
        //updateDisplay();
    }

    private void updateDisplay()
    {
        Bitmap displayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        displayBitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        Canvas canvas = new Canvas(displayBitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(16);
        paint.setColor(Color.WHITE);
        // Update layout according to the camera mode
        switch (recordingMode) {
            case SmartEyeglassControl.Intents.CAMERA_MODE_STILL:
                canvas.drawText("Tap to capture : STILL", pointX, pointY, paint);
                break;
            case SmartEyeglassControl.Intents.CAMERA_MODE_STILL_TO_FILE:
                canvas.drawText("Tap to capture : STILL TO FILE", pointX, pointY, paint);
                break;
            case SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_HIGH_RATE:
            case SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_LOW_RATE:
                if (cameraStarted) {
                    canvas.drawText("JPEG Streaming...", pointBaseX, pointY, paint);
                    canvas.drawText("Tap to stop.", pointBaseX, (pointY * 2), paint);
                    canvas.drawText("Frame Number: " + Integer.toString(saveFileIndex), pointBaseX, (pointY * 3), paint);
                } else {
                    canvas.drawText("Tap to start JPEG Stream.", pointBaseX, pointY, paint);
                }
                break;
            default:
                canvas.drawText("wrong recording type.", pointBaseX, pointY, paint);
        }

        utils.showBitmap(displayBitmap);
    }
}
