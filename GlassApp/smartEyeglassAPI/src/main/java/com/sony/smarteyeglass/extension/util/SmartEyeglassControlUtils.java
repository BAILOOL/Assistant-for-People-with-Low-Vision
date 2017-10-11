/*
Copyright (c) 2013, Sony Corporation.

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

 * Neither the name of the Sony Corporation nor the names
  of its contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sony.smarteyeglass.extension.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sony.smarteyeglass.SmartEyeglassControl;
import com.sony.smarteyeglass.extension.util.ar.RenderObject;
import com.sony.smarteyeglass.sdk.R;

/**
 * This class provides the methods that you use to handle most
 * SmartEyeglass-specific functionality. The API provides these features:
 * <ul>
 * <li><a href="#Camera">Camera control</a></li>
 * <li><a href="#Voice Text Input">Audio control</a></li>
 * <li><a href="#Dialog">Built-in UI utilities</a></li>
 * <li><a href="#Settings">Configuration options</a></li>
 * </ul>
 * <p>Use of these methods requires the "com.sony.smarteyeglass.permission.SMARTEYEGLASS"
 * permission in the app&#39;s Android manifest. Camera and voice-to-text intents
 * require additional specific permissions as noted.</p>
 * <a name="Camera"></a>
 * <h3>Camera control</h3>
 * <p>The Camera API provides access to two camera functions:</p>
 * <ul>
 * <li>Picture  : Take and store a still image</li>
 * <li>JPG Stream : Get JPEG stream data for image recognition</li>
 * </ul>
 * <p>
 * Your camera control app starts and stops camera operation
 * in one of these modes. In JPEG stream mode, image capture
 * begins automatically and continues until camera function is terminated.
 * To capture a still image, you call {@link #requestCameraCapture()}
 *  after starting the camera.
 * </p>
 *
 * <a name="Voice Text Input"></a>
 * <h3>Audio control</h3>
 * <p>
 * SmartEyeglass hardware includes a microphone on the controller that can
 * be used for voice input that is automatically translated to text. To initiate
 * the asynchronous voice input operation, call {@link #enableVoiceTextInput()}.
 * Provide a handler for the result that is passed back upon completion of the operation.
 * See {@link com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener#onVoiceTextInput}.
 * </p>
 * <p>
 * The SmartEyeglass is also a Bluetooth headset, and you can use the microphone and speaker
 * for host-phone audio functions, including sound recording and playback, and telephony.
 * In general, these functions use the standard Android BT HFP API.
 * This class provides a method to query the current telephony status,
 * {@link #requestTelephonyFunctionStatus()}.
 * </p>
 * <a name="Dialog"></a>
 * <h3>Built-in UI utilites</h3>
 * <p>Utilities that help you create a UI for your app include built-in animation effects
 * for layer transitions, and predefined configurable dialogs. </p>
 * <p>
 * The dialog methods make it easy to define and display a dialog with a message
 *  and optional choice buttons. Provide a handler for the result that is passed
 * back to your app when the user dismisses the dialog, or it times out.
 * See {@link com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener#onDialogClosed}.
 * </p>
 *
 * <a name="Settings"></a>
 * <h3>Configuration options</h3>
 * <p>
 * You can enable and disable various configuration options for the SmartEyeglass, including:
 * <ul>
 * <li>Power modes (low/high power consumption and standby)</li>
 * <li>Safe display mode</li>
 * <li>Screen depth</li>
 * <li>Sound effects for button-press feedback</li>
 * </ul>
 * </p>
 * <h3>User input event handling</h3>
 * <p>
 * Users can tap or swipe(left/right) on the touch pad, or press the Back key
 * on the controller. You can override the handlers for these events that
 * are part of the Smart Extension APIs in the
 * <a href="http://developer.sonymobile.com/reference/sony-addon-sdk/packages">Sony Add-on SDK package</a> .
 * </p>
 *
 * <h3>Permissions</h3>
 * <p> Your app&#39;s Android manifest must declare this permissions to use SmartEyeglass
 * features:</p>
 * <p><code>com.sony.smarteyeglass.permission.SMARTEYEGLASS</code></p>
 * <p>In addition, to use the camera and voice-to-text input features, you must
 * request additional permissions:</p>
 * <ul>
 * <li>com.sony.smarteyeglass.permission.CAMERA</li>
 * <li>com.sony.smarteyeglass.permission.VOICE_TEXT_INPUT</li>
 * </ul>
 * <p>For example, to use the camera functions, include this element in the Android manifest:</p>
 * <p><code>&lt;uses-permission android:name="com.sony.smarteyeglass.permission.CAMERA"/&gt; </code></p>
 */
public class SmartEyeglassControlUtils extends BroadcastReceiver {

    private final int INVALID_DISP_OFFSET = -1;

    private final int INVALID_CAMERA_MODE = -1;

    private final int DIALOG_BUTTON_MAX_NUM = 3;

    private final float VERTICAL_RANGE_MAX = 60.0f;

    private final float VERTICAL_RANGE_MIN = 0.0f;

    private final int IMAGE_PIXEL_SIZE_MAX = 57822;

    private final int PNG_COMPLESS_QUALITY = 100;

    /**
     * supported camera mode.
     */
    private final List<Integer> CAMERA_SUPPORT_MODE = Arrays.asList(
            SmartEyeglassControl.Intents.CAMERA_MODE_STILL,
            SmartEyeglassControl.Intents.CAMERA_MODE_STILL_TO_FILE,
            SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_LOW_RATE,
            SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_HIGH_RATE
        );

    /**
     * supported still mode resolution.
     */
    private final List<Integer> CAMERA_STILL_SUPPORT_RESOLUTION = Arrays.asList(
            SmartEyeglassControl.Intents.CAMERA_RESOLUTION_3M,
            SmartEyeglassControl.Intents.CAMERA_RESOLUTION_1M,
            SmartEyeglassControl.Intents.CAMERA_RESOLUTION_VGA,
            SmartEyeglassControl.Intents.CAMERA_RESOLUTION_QVGA
        );

    /**
     * supported jpeg stream resolution.
     */
    private final List<Integer> CAMERA_JPEG_STREAM_SUPPORT_RESOLUTION = Arrays.asList(
            SmartEyeglassControl.Intents.CAMERA_RESOLUTION_QVGA
        );

    /**
     * supported quality.
     */
    private final List<Integer> CAMERA_SUPPORT_QUALITY = Arrays.asList(
            SmartEyeglassControl.Intents.CAMERA_JPEG_QUALITY_STANDARD,
            SmartEyeglassControl.Intents.CAMERA_JPEG_QUALITY_FINE,
            SmartEyeglassControl.Intents.CAMERA_JPEG_QUALITY_SUPER_FINE
        );

    /**
     * The context of the extension service.
     */
    private Context mContext;

    /**
     * Package name of the host application.
     */
    private final String mHostAppPackageName;

    /**
     * Default bitmap factory options that will be frequently used throughout
     * the extension to avoid any automatic scaling. Keep in mind that we are
     * not showing the images on the phone, but on the accessory.
     */
    private final BitmapFactory.Options mBitmapOptions;

    /**
     * The present rendering mode.
     */
    private int mRenderingMode = SmartEyeglassControl.Intents.MODE_NORMAL;

    /**
     * The present camera operational mode.
     */
    private int mRecodingMode = INVALID_CAMERA_MODE;

    private final Handler mHandler;
    private final SmartEyeglassEventListener mGeneralEventListener;
    private int mSetApiVersion = 0;

    /**
     * Creates an instance of this class.
     *
     * @param hostAppPackageName
     *            Package name of host application.
     * @param eventListener
     *            Optional. The associated event listener, or NULL if not needed.
     */
    public SmartEyeglassControlUtils(String hostAppPackageName,
            SmartEyeglassEventListener eventListener) {
        mHostAppPackageName = hostAppPackageName;
        mHandler = new Handler();

        if (eventListener != null) {
            mGeneralEventListener = eventListener;
        } else {
            mGeneralEventListener = new SmartEyeglassEventListener();
        }

        // Set some default bitmap factory options that we frequently will use.
        mBitmapOptions = new BitmapFactory.Options();
        mBitmapOptions.inDensity = DisplayMetrics.DENSITY_DEFAULT;
        mBitmapOptions.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
    }

    /**
     * Associates this instance with a service context.
     *
     * @param context
     *            The extension service context.
     */
    public final void activate(final Context context) {
        if (mContext != null) {
            deactivate();
        }
        mContext = context;

        // register BroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SmartEyeglassControl.Intents.CONTROL_VOICE_TEXT_INPUT_NOTIFY_RECOGNIZED_TEXT_EVENT_INTENT);
        filter.addAction(SmartEyeglassControl.Intents.CONTROL_AR_GET_OBJECT_REQUEST_INTENT);
        filter.addAction(SmartEyeglassControl.Intents.CONTROL_AR_REGISTER_OBJECT_RESPONSE_INTENT);
        filter.addAction(SmartEyeglassControl.Intents.CONTROL_AR_ENABLE_ANIMATION_RESPONSE_INTENT);
        filter.addAction(SmartEyeglassControl.Intents.CONTROL_AR_DISABLE_ANIMATION_RESPONSE_INTENT);
        filter.addAction(SmartEyeglassControl.Intents.CONTROL_BATTERY_GET_LEVEL_RESPONSE_INTENT);
        filter.addAction(SmartEyeglassControl.Intents.CONTROL_TELEPHONY_GET_MODE_RESPONSE_INTENT);
        filter.addAction(SmartEyeglassControl.Intents.CONTROL_CAMERA_NOTIFY_ERROR_EVENT_INTENT);
        filter.addAction(SmartEyeglassControl.Intents.CONTROL_CAMERA_NOTIFY_CAPTURED_FILE_EVENT_INTENT);
        filter.addAction(SmartEyeglassControl.Intents.CONTROL_STANDBY_CONFIRM_REQUEST_INTENT);
        filter.addAction(SmartEyeglassControl.Intents.CONTROL_STANDBY_NOTIFY_CONDITION_EVENT_INTENT);
        filter.addAction(SmartEyeglassControl.Intents.CONTROL_POWER_MODE_NOTIFY_MODE_EVENT_INTENT);
        filter.addAction(SmartEyeglassControl.Intents.CONTROL_DIALOG_CLOSED_EVENT_INTENT);
        filter.addAction(SmartEyeglassControl.Intents.CONTROL_DISPLAY_NOTIFY_STATUS_EVENT_INTENT);
        filter.addAction(SmartEyeglassControl.Intents.CONTROL_DISPLAY_DATA_RESULT_INTENT);
        filter.addAction(SmartEyeglassControl.Intents.CONTROL_AR_ANIMATION_RESULT_INTENT);
        context.registerReceiver(this, filter);

        sendConfirmApiVersion(context);
    }

    /**
     * Unregisters this instance from the associated service context.
     *
     * You must call this method before destroying the associated context object
     * (for example, in the {@code onDestroy()} method of the {@code Activity}).
     * Failure to deactivate causes the Android framework to log an error.
     */
    public final void deactivate() {
        if (mContext == null) {
            return;
        }
        mContext.unregisterReceiver(this);
        mContext = null;
    }

    private class IntentRunner implements Runnable {
        protected Intent mIntent;
        IntentRunner(Intent intent) {
            mIntent = intent;
        }

        @Override
        public void run() {
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mHandler.post(new IntentRunner(intent) {
            @Override
            public void run() {
                String action = mIntent.getAction();
                if (action == SmartEyeglassControl.Intents.CONTROL_VOICE_TEXT_INPUT_NOTIFY_RECOGNIZED_TEXT_EVENT_INTENT) {
                    mGeneralEventListener.onVoiceTextInput(mIntent.getIntExtra(Control.Intents.EXTRA_ERROR_CODE, -1),
                            mIntent.getStringExtra(Control.Intents.EXTRA_TEXT));
                } else if (action == SmartEyeglassControl.Intents.CONTROL_POWER_MODE_NOTIFY_MODE_EVENT_INTENT) {
                    mGeneralEventListener.onChangePowerMode(mIntent.getIntExtra(SmartEyeglassControl.Intents.EXTRA_POWER_MODE, -1));
                } else if (action == SmartEyeglassControl.Intents.CONTROL_DISPLAY_NOTIFY_STATUS_EVENT_INTENT) {
                    mGeneralEventListener.onDisplayStatus(mIntent.getIntExtra(SmartEyeglassControl.Intents.EXTRA_DISPLAY_STATUS, -1));
                } else if (action == SmartEyeglassControl.Intents.CONTROL_DIALOG_CLOSED_EVENT_INTENT) {
                    mGeneralEventListener.onDialogClosed(mIntent.getIntExtra(SmartEyeglassControl.Intents.EXTRA_DIALOG_SELECTED_BUTTON_INDEX, -1));
                } else if (action == SmartEyeglassControl.Intents.CONTROL_BATTERY_GET_LEVEL_RESPONSE_INTENT) {
                    mGeneralEventListener.onBatteryStatus(mIntent.getIntExtra(SmartEyeglassControl.Intents.EXTRA_BATTERY_LEVEL, 0));
                } else if (action == SmartEyeglassControl.Intents.CONTROL_TELEPHONY_GET_MODE_RESPONSE_INTENT) {
                    mGeneralEventListener.onTelephonyFunctionStatus(mIntent.getIntExtra(SmartEyeglassControl.Intents.EXTRA_TELEPHONY_MODE, 0));
                } else if (action == SmartEyeglassControl.Intents.CONTROL_CAMERA_NOTIFY_ERROR_EVENT_INTENT) {
                    mGeneralEventListener.onCameraErrorReceived(mIntent.getIntExtra(Control.Intents.EXTRA_ERROR_CODE, -1));
                } else if (action == SmartEyeglassControl.Intents.CONTROL_CAMERA_NOTIFY_CAPTURED_FILE_EVENT_INTENT) {
                    mGeneralEventListener.onCameraReceivedFile(mIntent.getStringExtra(Control.Intents.EXTRA_DATA_URI));
                } else if (action == SmartEyeglassControl.Intents.CONTROL_STANDBY_CONFIRM_REQUEST_INTENT) {
                    boolean ret = mGeneralEventListener.onConfirmationEnterStandby();
                    Intent intent = new Intent();
                    intent.setAction(SmartEyeglassControl.Intents.CONTROL_STANDBY_CONFIRM_RESPONSE_INTENT);
                    intent.putExtra(SmartEyeglassControl.Intents.EXTRA_STANDBY_CONFIRMED_RESULT,
                            ret ? SmartEyeglassControl.Intents.STANDBY_CONFIRMED_RESULT_OK :
                                SmartEyeglassControl.Intents.STANDBY_CONFIRMED_RESULT_NG);
                    sendToHostApp(intent);
                } else if (action == SmartEyeglassControl.Intents.CONTROL_STANDBY_NOTIFY_CONDITION_EVENT_INTENT) {
                    mGeneralEventListener.onStandbyStatus(mIntent.getIntExtra(SmartEyeglassControl.Intents.EXTRA_STANDBY_CONDITION, -1));
                } else if (action == SmartEyeglassControl.Intents.CONTROL_AR_GET_OBJECT_REQUEST_INTENT) {
                    mGeneralEventListener.onARObjectRequest(
                            mIntent.getIntExtra(SmartEyeglassControl.Intents.EXTRA_AR_OBJECT_ID, -1)
                            );
                } else if (action == SmartEyeglassControl.Intents.CONTROL_AR_REGISTER_OBJECT_RESPONSE_INTENT) {
                    mGeneralEventListener.onARRegistrationResult(
                            mIntent.getIntExtra(SmartEyeglassControl.Intents.EXTRA_AR_RESULT, -1),
                            mIntent.getIntExtra(SmartEyeglassControl.Intents.EXTRA_AR_OBJECT_ID, -1));
                } else if (action == SmartEyeglassControl.Intents.CONTROL_AR_ENABLE_ANIMATION_RESPONSE_INTENT) {
                    int result = mIntent.getIntExtra(SmartEyeglassControl.Intents.EXTRA_AR_RESULT, -1);

                    if (result == SmartEyeglassControl.Intents.AR_RESULT_OK) {
                        connectLocalServer(mIntent.getStringExtra(
                                SmartEyeglassControl.Intents.EXTRA_AR_ANIMATION_SOCKET_NAME));
                    }
                    mGeneralEventListener.onAREnableAnimationResponse(result);
                } else if (action == SmartEyeglassControl.Intents.CONTROL_AR_DISABLE_ANIMATION_RESPONSE_INTENT) {
                    int result = mIntent.getIntExtra(SmartEyeglassControl.Intents.EXTRA_AR_RESULT, -1);

                    if (result == SmartEyeglassControl.Intents.AR_RESULT_OK) {
                        closeLocalServer();
                    }
                    mGeneralEventListener.onARDisableAnimationResponse(result);
                } else if (action == SmartEyeglassControl.Intents.CONTROL_DISPLAY_DATA_RESULT_INTENT) {
                    int transaction = mIntent.getIntExtra(
                            SmartEyeglassControl.Intents.EXTRA_DISPLAY_DATA_TRANSACTION_NUMBER,
                            SmartEyeglassControl.Intents.INVALID_DISPLAY_DATA_TRANSACTION_NUMBER);
                    int result = mIntent.getIntExtra(SmartEyeglassControl.Intents.EXTRA_DISPLAY_DATA_RESULT,
                            SmartEyeglassControl.Intents.DISPLAY_DATA_RESULT_OK);
                    int type = mIntent.getIntExtra(SmartEyeglassControl.Intents.EXTRA_DISPLAY_DATA_TYPE,
                            SmartEyeglassControl.Intents.DISPLAY_DATA_TYPE_SHOW_BITMAP);

                    if (type == SmartEyeglassControl.Intents.DISPLAY_DATA_TYPE_SHOW_BITMAP) {
                        mGeneralEventListener.onResultShowBitmap(transaction, result);
                    } else if (type == SmartEyeglassControl.Intents.DISPLAY_DATA_TYPE_SHOW_IMAGE) {
                        mGeneralEventListener.onResultShowImage(transaction, result);
                    }
                } else if (action == SmartEyeglassControl.Intents.CONTROL_AR_ANIMATION_RESULT_INTENT) {
                    int transaction = mIntent.getIntExtra(
                            SmartEyeglassControl.Intents.EXTRA_DISPLAY_DATA_TRANSACTION_NUMBER,
                            SmartEyeglassControl.Intents.INVALID_DISPLAY_DATA_TRANSACTION_NUMBER);
                    int result = mIntent.getIntExtra(SmartEyeglassControl.Intents.EXTRA_DISPLAY_DATA_RESULT,
                            SmartEyeglassControl.Intents.DISPLAY_DATA_RESULT_OK);

                    mGeneralEventListener.onResultSendAnimationObject(transaction, result);
                }
            }
        });
    }

    /** */
    private void sendConfirmApiVersion(final Context context) {
        int apiVersion = context.getResources().getInteger(R.integer.api_version);

        if (apiVersion < mSetApiVersion) {
            throw new IllegalArgumentException(
                    context.getResources().getString(R.string.api_version_error_throw_message));
        }

        if (mSetApiVersion == 0) {
            mSetApiVersion = apiVersion;
        }

        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_API_VERSION_CONFIRM_INTENT);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_VERSION_DATA, mSetApiVersion);

        sendToHostApp(intent);
    }

    /**
    * Set the minimum version of the SmartEyeglass API the app requires.
    * Call this during initialization of the app so that HostApp can
    * ensure compatibility.
    * <p>
    * This allows the framework to warn the user if they need to install an update,
    * and exit gracefully from your app.
    * </p><p>
    * By default, the required API version is set to the highest level available in the SDK.
    * For the best user experience, you should explicitly set it to the highest API level
    * the app actually requires.
    * </p>
    * @param version The minimum required API version
    */
    public void setRequiredApiVersion(final int version) {
        mSetApiVersion = version;
    }

    /**
     * Displays an image resource on the SmartEyeglass screen.
     * Display can take some time for a large image.
     *
     * <p>If an image is larger than the screen size, it is not displayed on the screen.<br>
     * For screen size refer to
     * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#EXTRA_IMAGE_HEIGHT},
     * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#EXTRA_IMAGE_WIDTH}
     * </p>
     * @param resourceId The image resource ID.
     */
    public void showImage(final int resourceId) {
        if (Dbg.DEBUG) {
            Dbg.d("showImage: " + resourceId);
        }

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceId,
                mBitmapOptions);

        sendDisplayData(bitmap, INVALID_DISP_OFFSET, INVALID_DISP_OFFSET);
    }

    /**
     * Draws an image on the SmartEyeglass display. Returns the result in
     * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener#onResultShowImage}
     * when the operation is completed.
     *
     * <p>If an image is larger than the screen size, it is not displayed on the screen.<br>
     * For screen size refer to
     * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#EXTRA_IMAGE_HEIGHT},
     * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#EXTRA_IMAGE_WIDTH}
     * </p>
     * @param resourceId The image resource ID.
     * @param transactionNumber The transaction number.
     */
    public void showImageWithCallback(final int resourceId, final int transactionNumber) {
        if (Dbg.DEBUG) {
            Dbg.d("showImageWithCallback: " + resourceId);
        }

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceId,
                mBitmapOptions);

        sendDisplayDataWithCallback(bitmap, INVALID_DISP_OFFSET, INVALID_DISP_OFFSET,
                transactionNumber, SmartEyeglassControl.Intents.DISPLAY_DATA_TYPE_SHOW_IMAGE);
    }

     /**
     * Displays a bitmap on the SmartEyeglass screen.
     * Display can take some time for a large image.
     *
     * <p>If an image is larger than the screen size, it is not displayed on the screen.<br>
     * For screen size refer to
     * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#EXTRA_IMAGE_HEIGHT},
     * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#EXTRA_IMAGE_WIDTH}
     * </p>
     *
     * @param bitmap The bitmap object.
     */
    public void showBitmap(final Bitmap bitmap) {
        if (Dbg.DEBUG) {
            Dbg.d("showBitmap");
        }

        sendDisplayData(bitmap, INVALID_DISP_OFFSET, INVALID_DISP_OFFSET);
    }

    /**
     * Updates a part of the SmartEyeglass screen with a given bitmap.
     * Provide the screen position at which to place the upper-left corner
     * of the bitmap. Coordinates are in pixels from the screen origin, the upper left corner.
     * <p>If an image is larger than the screen size, it is not displayed on the screen.<br>
     * For screen size refer to
     * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#EXTRA_IMAGE_HEIGHT},
     * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#EXTRA_IMAGE_WIDTH}
     * </p>
     *
     * @param bitmap The bitmap to show.
     * @param x The x position at which to draw.
     * @param y The y position at which to draw.
     */
    public void showBitmap(final Bitmap bitmap, final int x, final int y) {
        if (Dbg.DEBUG) {
            Dbg.v("showBitmap x: " + x + " y: " + y);
        }

        sendDisplayData(bitmap, x, y);
    }

    /**
    * Draws an bitmap on the SmartEyeglass display. Returns the result in
    * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener#onResultShowBitmap}
    * when the operation is completed.
    * <p>If an image is larger than the screen size, it is not displayed on the screen.<br>
    * For screen size refer to
    * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#EXTRA_IMAGE_HEIGHT},
    * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#EXTRA_IMAGE_WIDTH}
    * </p>
    * @param bitmap The bitmap to show.
    * @param transactionNumber The transaction number.
    */
    public void showBitmapWithCallback(final Bitmap bitmap, final int transactionNumber) {
        if (Dbg.DEBUG) {
            Dbg.v("showBitmapWithCallback");
        }
        sendDisplayDataWithCallback(bitmap, INVALID_DISP_OFFSET, INVALID_DISP_OFFSET, transactionNumber,
                SmartEyeglassControl.Intents.DISPLAY_DATA_TYPE_SHOW_BITMAP);
    }

    /**
    * Draws an bitmap on a specific region of the SmartEyeglass display. Returns the result in
    * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener#onResultShowBitmap}
    * when the operation is completed.
    * <p>If an image is larger than the screen size, it is not displayed on the screen.<br>
    * For screen size refer to
    * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#EXTRA_IMAGE_HEIGHT},
    * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#EXTRA_IMAGE_WIDTH}
    * </p>
    * @param bitmap The bitmap to show.
    * @param x The x position.
    * @param y The y position.
    * @param transactionNumber The transaction number.
    */
    public void showBitmapWithCallback(final Bitmap bitmap,
            final int x, final int y, final int transactionNumber) {
        if (Dbg.DEBUG) {
            Dbg.v("showBitmapWithCallback x: " + x + " y: " + y);
        }
        sendDisplayDataWithCallback(bitmap, x, y, transactionNumber,
                SmartEyeglassControl.Intents.DISPLAY_DATA_TYPE_SHOW_BITMAP);
    }

    /** */
    private void sendDisplayData(final Bitmap bitmap, final int x, final int y) {

        // image size check
        if ((bitmap.getHeight() * bitmap.getWidth()) > IMAGE_PIXEL_SIZE_MAX) {
            throw new IllegalArgumentException("Images that are trying to display too large");
        }
        if (bitmap.getHeight() > mContext.getResources().getDimension(R.dimen.smarteyeglass_control_height)
                || bitmap.getWidth() > mContext.getResources().getDimension(R.dimen.smarteyeglass_control_width)) {
            throw new IllegalArgumentException("Images that are trying to display too large");
        }

        byte[] buffer = EightBitMonochromeImageEncoder.convert(bitmap, 0, bitmap.getHeight());

        Intent intent = new Intent(Control.Intents.CONTROL_DISPLAY_DATA_INTENT);
        intent.putExtra(Control.Intents.EXTRA_DATA, buffer);
        // since raw format then other side do not know width or height
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_IMAGE_HEIGHT, bitmap.getHeight());
        // since raw format then other side do not know width or height
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_IMAGE_WIDTH, bitmap.getWidth());
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_DATA_IS_RAW_FORMAT, 1);    // SmartEyeglass raw format

        if (x != INVALID_DISP_OFFSET && y != INVALID_DISP_OFFSET) {
            intent.putExtra(Control.Intents.EXTRA_X_OFFSET, x);
            intent.putExtra(Control.Intents.EXTRA_Y_OFFSET, y);
        }

        sendToHostApp(intent);
    }

    /** */
    private void sendDisplayDataWithCallback(final Bitmap bitmap, 
            final int x, final int y, final int transactionNumber, final int displayDataType) {
        // image size check
        if ((bitmap.getHeight() * bitmap.getWidth()) > IMAGE_PIXEL_SIZE_MAX) {
            throw new IllegalArgumentException("Images that are trying to display too large");
        }
        if (bitmap.getHeight() > mContext.getResources().getDimension(R.dimen.smarteyeglass_control_height)
                || bitmap.getWidth() > mContext.getResources().getDimension(R.dimen.smarteyeglass_control_width)) {
            throw new IllegalArgumentException("Images that are trying to display too large");
        }

        byte[] buffer = EightBitMonochromeImageEncoder.convert(bitmap, 0, bitmap.getHeight());

        Intent intent = new Intent(Control.Intents.CONTROL_DISPLAY_DATA_INTENT);
        intent.putExtra(Control.Intents.EXTRA_DATA, buffer);
        // since raw format then other side do not know width or height
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_IMAGE_HEIGHT, bitmap.getHeight());
        // since raw format then other side do not know width or height
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_IMAGE_WIDTH, bitmap.getWidth());
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_DATA_IS_RAW_FORMAT, 1);    // SmartEyeglass raw format
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_DISPLAY_DATA_TRANSACTION_NUMBER, transactionNumber);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_DISPLAY_DATA_TYPE, displayDataType);

        if (x != INVALID_DISP_OFFSET && y != INVALID_DISP_OFFSET) {
            intent.putExtra(Control.Intents.EXTRA_X_OFFSET, x);
            intent.putExtra(Control.Intents.EXTRA_Y_OFFSET, y);
        }

        sendToHostApp(intent);
    }

    /**
     * Initiates camera operation on the SmartEyeglass device, providing a file specification for saving
     * captured image data.
     *
     * @param filePath Path of a file to which to save captured image data.
     */
    public void startCamera(String filePath) throws ControlCameraException {
        if (Dbg.DEBUG) {
            Dbg.v("startCameraCapture");
        }

        if (mRecodingMode == INVALID_CAMERA_MODE) {
            throw new IllegalArgumentException("Camera mode has not been set. Please call method setCameraMode().");
        }

        if (mRecodingMode != SmartEyeglassControl.Intents.CAMERA_MODE_STILL_TO_FILE) {
            openSocket();
        }

        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_CAMERA_START_INTENT);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_CAMERA_VIDEO_SOCKET_NAME, mCameraSocketName[0]);

        if (filePath != null) {
            intent.putExtra(Control.Intents.EXTRA_DATA_URI, filePath);
        }
        sendToHostApp(intent);
    }

    /**
     * Initiates camera operation on the SmartEyeglass device.
     * When the recording mode is {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_MODE_STILL},
     * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_MODE_JPG_STREAM_LOW_RATE},
     * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_MODE_JPG_STREAM_HIGH_RATE},
     * the extension receives  {@link com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener#onCameraReceived}
     * callback methods when the capture process finishes.
     */
    public void startCamera() throws ControlCameraException {
        startCamera(null);
    }

    /**
     * Terminates camera operation on the SmartEyeglass device.
     */
    public void stopCamera() {
        if (mRecodingMode != SmartEyeglassControl.Intents.CAMERA_MODE_STILL_TO_FILE) {
            closeSocket();
        }

        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_CAMERA_STOP_INTENT);
        sendToHostApp(intent);
    }

    /**
     * Configures camera settings for the next camera operation.
     *
     * @param jpegQuality The desired JPEG quality (compression). One of:
     *            <ul>
     *            <li>{@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_JPEG_QUALITY_STANDARD}</li>
     *            <li>{@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_JPEG_QUALITY_FINE}</li>
     *            <li>{@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_JPEG_QUALITY_SUPER_FINE}</li>
     *            </ul>
     * @param resolution  The desired picture resolution. One of:
     *            <ul>
     *            <li>{@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_RESOLUTION_3M}</li>
     *            <li>{@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_RESOLUTION_1M}</li>
     *            <li>{@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_RESOLUTION_VGA}</li>
     *            <li>{@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_RESOLUTION_QVGA}</li>
     *            </ul>
     * @param recordingMode The camera mode. One of:
     *            <ul>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_MODE_STILL}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_MODE_STILL_TO_FILE}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_MODE_JPG_STREAM_LOW_RATE}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_MODE_JPG_STREAM_HIGH_RATE}</li>
     *            </ul>
     */
    public void setCameraMode(int jpegQuality, int resolution, int recordingMode) {
        if (Dbg.DEBUG) {
            Dbg.v("Camera mode set: jpegQuality: " + jpegQuality
                    + ", resolution: " + resolution );
        }

        if (!CAMERA_SUPPORT_MODE.contains(recordingMode)) {
            throw new IllegalArgumentException("recordingMode has illegal value");
        }
        if (!getSupportedResolutions(recordingMode).contains(resolution)) {
            throw new IllegalArgumentException("resolution has illegal value");
        }
        if (!CAMERA_SUPPORT_QUALITY.contains(jpegQuality)) {
            throw new IllegalArgumentException("jpegQuality has illegal value");
        }

        mRecodingMode = recordingMode;

        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_CAMERA_SET_MODE_INTENT);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_CAMERA_JPEG_QUALITY, jpegQuality);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_CAMERA_RESOLUTION, resolution);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_CAMERA_MODE, recordingMode);
        sendToHostApp(intent);
    }

    /** */
    private List<Integer> getSupportedResolutions(int recordingMode) {

        List<Integer> ret = null;

        switch(recordingMode) {
        case SmartEyeglassControl.Intents.CAMERA_MODE_STILL:
        case SmartEyeglassControl.Intents.CAMERA_MODE_STILL_TO_FILE:
            ret = CAMERA_STILL_SUPPORT_RESOLUTION;
            break;
        case SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_LOW_RATE:
        case SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_HIGH_RATE:
            ret = CAMERA_JPEG_STREAM_SUPPORT_RESOLUTION;
            break;
        default:
            ret = Arrays.asList();
            break;
        }

        return ret;
    }

    /**
     * Captures a still image when the camera module is running.
     */
    public void requestCameraCapture() {
        if (Dbg.DEBUG) {
            Dbg.v("requestCameraCapture");
        }
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_CAMERA_CAPTURE_STILL_INTENT);
        sendToHostApp(intent);
    }

    /**
     * Transitions the screen display to the given bitmap using a built-in animation effect
     * that looks like moving down to a lower layer.
     * Display can take some time for a large image.
     * If an image larger than the screen size,it is not displayed on the screen.<br>
     * For screen size refer to
     * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#EXTRA_IMAGE_HEIGHT},
     * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#EXTRA_IMAGE_WIDTH}
     *
     * @param bitmap The new bitmap to show.
     */
    public void moveLowerLayer(final Bitmap bitmap) {
        if (Dbg.DEBUG) {
            Dbg.d("showBitmap w/ animation");
        }

        byte[] buffer = EightBitMonochromeImageEncoder.convert(bitmap, 0, bitmap.getHeight());
        Intent intent = new Intent(Control.Intents.CONTROL_DISPLAY_DATA_INTENT);
        intent.putExtra(Control.Intents.EXTRA_DATA, buffer);
        // since raw format then other side do not know width or height
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_IMAGE_HEIGHT, bitmap.getHeight());
        // since raw format then other side do not know width or height
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_IMAGE_WIDTH, bitmap.getWidth());
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_DATA_IS_RAW_FORMAT, 1);    // SmartEyeglass raw format
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_LAYER_TRANSITION_EFFECT_TYPE,
                SmartEyeglassControl.Intents.LAYER_TRANSITION_MOVE_LOWER_LAYER);
        sendToHostApp(intent);
    }

    /**
     * Transitions the screen display to the given bitmap using a built-in animation effect
     * that looks like moving up to a higher layer.
     * Display can take some time for a large image.
     * If an image larger than the screen size,it is not displayed on the screen.<br>
     * For screen size refer to
     * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#EXTRA_IMAGE_HEIGHT},
     * {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#EXTRA_IMAGE_WIDTH}
     *
     * @param bitmap The new bitmap to show.
     */
    public void moveUpperLayer(final Bitmap bitmap) {
        if (Dbg.DEBUG) {
            Dbg.d("showBitmap w/ animation");
        }

        byte[] buffer = EightBitMonochromeImageEncoder.convert(bitmap, 0, bitmap.getHeight());
        Intent intent = new Intent(Control.Intents.CONTROL_DISPLAY_DATA_INTENT);
        intent.putExtra(Control.Intents.EXTRA_DATA, buffer);
        // since raw format then other side do not know width or height
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_IMAGE_HEIGHT, bitmap.getHeight());
        // since raw format then other side do not know width or height
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_IMAGE_WIDTH, bitmap.getWidth());
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_DATA_IS_RAW_FORMAT, 1);    // SmartEyeglass raw format
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_LAYER_TRANSITION_EFFECT_TYPE,
                SmartEyeglassControl.Intents.LAYER_TRANSITION_MOVE_UPPER_LAYER);
        sendToHostApp(intent);
    }

    /**
     * Transitions the screen display to the given layout using a built-in animation effect
     * that looks like moving down to a lower layer.
     * Display can take some time for a large image.
     *
     * @param layoutId The resource ID for the new layout to show.
     * @param layoutData The layout data bundle.
     */
    public void moveLowerLayer(final int layoutId, final Bundle[] layoutData) {
        if (Dbg.DEBUG) {
            Dbg.d("showLayout w/ animation");
        }

        Intent intent = new Intent(Control.Intents.CONTROL_PROCESS_LAYOUT_INTENT);
        intent.putExtra(Control.Intents.EXTRA_DATA_XML_LAYOUT, layoutId);
        if (layoutData != null && layoutData.length > 0) {
            intent.putExtra(Control.Intents.EXTRA_LAYOUT_DATA, layoutData);
        }
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_LAYER_TRANSITION_EFFECT_TYPE,
                SmartEyeglassControl.Intents.LAYER_TRANSITION_MOVE_LOWER_LAYER);
        sendToHostApp(intent);
    }

    /**
     * Transitions the screen display to the given layout using a built-in animation effect
     * that looks like moving up to a higher layer.
     * Display can take some time for a large image.
     *
     * @param layoutId The resource ID for the new layout to show.
     * @param layoutData The layout data bundle.
     */
    public void moveUpperLayer(final int layoutId, final Bundle[] layoutData) {
        if (Dbg.DEBUG) {
            Dbg.d("showLayout w/ animation");
        }

        Intent intent = new Intent(Control.Intents.CONTROL_PROCESS_LAYOUT_INTENT);
        intent.putExtra(Control.Intents.EXTRA_DATA_XML_LAYOUT, layoutId);
        if (layoutData != null && layoutData.length > 0) {
            intent.putExtra(Control.Intents.EXTRA_LAYOUT_DATA, layoutData);
        }
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_LAYER_TRANSITION_EFFECT_TYPE,
                SmartEyeglassControl.Intents.LAYER_TRANSITION_MOVE_UPPER_LAYER);
        sendToHostApp(intent);
    }

    /**
     * Enables vertical stroll support for specified TextView in the current layout.
     * @param textViewId The resource ID of the TextView.
     */
    public void sendTextViewLayoutId(final int textViewId) {
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_TEXT_SHOW_INTENT);
        intent.putExtra(Control.Intents.EXTRA_LAYOUT_REFERENCE, textViewId);
        sendToHostApp(intent);
    }

    /**
     * Displays a simple dialog.
     *
     * @param text   The message text.
     * @param mode   The dialog mode, one of:
     *            <ul>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#DIALOG_MODE_TIMEOUT}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#DIALOG_MODE_OK}</li>
     *            </ul>
     */
    public void showDialogMessage(final String text, final int mode) {
        if (Dbg.DEBUG) {
            Dbg.v("showDialogMessage: mode: " + mode + ", text: " + text);
        }
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_DIALOG_OPEN_INTENT);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_DIALOG_MODE, mode);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_DIALOG_MESSAGE, text);
        sendToHostApp(intent);
    }

    /**
     * Displays a customized dialog with up to three custom buttons. The user closes
     * the dialog by selecting one of the buttons.
     *
     * @param title  The dialog title.
     * @param message The message text.
     * @param buttons An array of one to three button labels. A 0-based index pointing into this array
     *        is returned to the {@link com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener#onDialogClosed}
     *        handler to indicate which button was selected.
     */
    public void showDialogMessage(final String title, final String message, final String[] buttons) {
        if (buttons.length > DIALOG_BUTTON_MAX_NUM) {
            throw new IllegalArgumentException("more than an upper limit on the number of buttons that can be set.");
        }
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_DIALOG_OPEN_INTENT);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_DIALOG_MODE,
                SmartEyeglassControl.Intents.DIALOG_MODE_USER_DEFINED);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_DIALOG_TITLE, title);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_DIALOG_MESSAGE, message);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_DIALOG_BUTTONS, buttons);
        sendToHostApp(intent);
    }

    /**
     * Enables the voice-to-text input feature. After that user can input text by pushing
     * the Talk button. When feature is active it shows a microphone icon on the screen.
     * <p>The system interrupts screen while voice-to-text input is in use.
     * During the operation, the app has no control and cannot receive input events from the
     * device keys or touch sensor.</p>
     * <p>The result of the operation is returned the
     * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener#onVoiceTextInput}
     * handler for your app.</p>
     */
    public void enableVoiceTextInput() {
        if (Dbg.DEBUG) {
            Dbg.v("enableVoiceTextInput");
        }
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_VOICE_TEXT_INPUT_ENABLE_INTENT);
        intent.putExtra(Control.Intents.EXTRA_DATA, 1);
        sendToHostApp(intent);
    }

    /**
     * Disables voice-to-text input feature and removes the microphone icon from the screen.
     */
    public void disableVoiceTextInput() {
        if (Dbg.DEBUG) {
            Dbg.v("disableVoiceTextInput");
        }
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_VOICE_TEXT_INPUT_ENABLE_INTENT);
        intent.putExtra(Control.Intents.EXTRA_DATA, 0);
        sendToHostApp(intent);
    }

    /**
     * Enables the safe-display mode, which  limits the display
     * to the lower half of the screen in order to minimize
     * interference with the user&#39;s field of view.
     */
    public void enableSafeDisplayMode() {
        if (Dbg.DEBUG) {
            Dbg.v("startSafeDisplayMode");
        }
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_SAFE_DISPLAY_MODE_SET_MODE_INTENT);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_SAFE_DISPLAY_MODE,
                SmartEyeglassControl.Intents.SAFE_DISPLAY_MODE_1);
        sendToHostApp(intent);
    }

    /**
     * Disables the safe-display mode, restoring full screen display.
     */
    public void disableSafeDisplayMode() {
        if (Dbg.DEBUG) {
            Dbg.v("stopSafeDisplayMode");
        }
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_SAFE_DISPLAY_MODE_SET_MODE_INTENT);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_SAFE_DISPLAY_MODE,
                SmartEyeglassControl.Intents.SAFE_DISPLAY_MODE_NONE);
        sendToHostApp(intent);
    }

    /**
     * Sets the power mode. In normal mode (the default), the SmartEyeglass connects via Bluetooth.
     * In high power mode, the connection can use Wi-Fi as well, which uses more power
     * but allows faster data transfer.
     *
     * @param powerMode The PowerMode value, one of
     *            <ul>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#POWER_MODE_HIGH}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#POWER_MODE_NORMAL}</li>
     *            </ul>
     */
    public void setPowerMode(final int powerMode) {
        if (Dbg.DEBUG) {
            Dbg.v("setPowerMode");
        }
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_POWER_MODE_SET_MODE_INTENT);
        intent.putExtra(Control.Intents.EXTRA_DATA, powerMode);
        sendToHostApp(intent);
    }

    /**
     * Initiates Standby mode to conserve power. Standby mode terminates automatically
     * when the user presses any button or touches the touch sensor.
     */
    public void requestEnterStandbyMode() {
        if (Dbg.DEBUG) {
            Dbg.v("requestEnterStandbyMode");
        }
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_STANDBY_ENTER_INTENT);
        sendToHostApp(intent);
    }

    /**
     * Disables the sound effect that provides feedback when the user
     * presses a button on the controller. The sound effect is on by default.
     */
    public void disableSoundEffect() {
        if (Dbg.DEBUG) {
            Dbg.v("disableSoundEffect");
        }
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_SOUND_EFFECT_SET_MODE_INTENT);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_SOUND_EFFECT_MODE,
                SmartEyeglassControl.Intents.SOUND_EFFECT_OFF);
        sendToHostApp(intent);
    }

    /**
     * Enables the sound effect that provides feedback when the user
     * presses a button on the controller. The sound effect is on by default.
     * Use this call to re-enable the effect after you have turned it off.
     */
    public void enableSoundEffect() {
        if (Dbg.DEBUG) {
            Dbg.v("enableSoundEffect");
        }
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_SOUND_EFFECT_SET_MODE_INTENT);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_SOUND_EFFECT_MODE,
                SmartEyeglassControl.Intents.SOUND_EFFECT_ON);
        sendToHostApp(intent);
    }

    /**
     * Controls screen depth, which is the apparent distance of the display plane from the user&#39;s eyes.
     *
     * @param depth An integer in the range -4 to 6.
     *            <ul>
     *            <li>0 (the default) is about 5 meters</li>
     *            <li>6 is nearest</li>
     *            <li>-4 is furthest, about 10 meters or more</li>
     *            </ul>
     */
    public void setScreenDepth(int depth) {
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_SCREEN_DEPTH_SET_DEPTH_INTENT);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_SCREEN_DEPTH, depth);
        sendToHostApp(intent);
    }

    /**
     * Requests notification of the battery status. See
     * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener#onBatteryStatus}
     */
    public void requestBatteryStatus() {
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_BATTERY_GET_LEVEL_REQUEST_INTENT);
        sendToHostApp(intent);
    }

     /**
     * Requests notification of the telephony function status. See
     * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener#onTelephonyFunctionStatus}
     */
    public void requestTelephonyFunctionStatus() {
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_TELEPHONY_GET_MODE_REQUEST_INTENT);
        sendToHostApp(intent);
    }

    /**
     * Turns AR rendering mode on or off.
     *
     * @param renderingMode The rendering mode. One of:
     *            <ul>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#MODE_NORMAL} (The default).</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#MODE_AR}</li>
     *            </ul>
     */
    public void setRenderMode(int renderingMode) {
        this.mRenderingMode = renderingMode;
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_AR_SET_MODE_INTENT);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_AR_MODE, renderingMode);
        sendToHostApp(intent);
    }

    /**
     * Limits the vertical range of cylindrical coordinate system.
     * Objects positioned outside of limit will stick to the edge of range.
     * Resulting field of view will be -range..range degrees.
     * @param range The new range value, must be between 0 and 60 degrees.
     */
    public void changeARCylindricalVerticalRange(float range) {
        if (mRenderingMode != SmartEyeglassControl.Intents.MODE_AR) {
            // error, not in AR mode so operation not possible
            throw new IllegalStateException("Not in AR mode");
        }

        if (range > VERTICAL_RANGE_MAX || range < VERTICAL_RANGE_MIN) {
            throw new IllegalStateException("invalid vertical range = " + range);
        }

        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_AR_CHANGE_CYLINDRICAL_VERTICAL_RANGE_INTENT);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_AR_CYLINDRICAL_VERTICAL_RANGE, range);
        sendToHostApp(intent);
    }

    /**
     * Registers an image to be rendered by the AR engine.
     * Registration is asynchronous. The result of registration is sent to the
     * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener#onARRegistrationResult}
     * handler for your app.
     *
     * @param object The configured render object, an instance of
     *         {@link com.sony.smarteyeglass.extension.util.ar.CylindricalRenderObject}
     *         or {@link com.sony.smarteyeglass.extension.util.ar.GlassesRenderObject}.
     */
    public void registerARObject(final RenderObject object) {
        if (mRenderingMode != SmartEyeglassControl.Intents.MODE_AR) {
            // error, not in AR mode so operation not possible
            throw new IllegalStateException("Not in AR mode");
        }

        if (object == null) {
            throw new IllegalArgumentException("object has illegal value");
        }

        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_AR_REGISTER_OBJECT_REQUEST_INTENT);
        object.toRegisterExtras(intent);

        sendToHostApp(intent);
    }

    /**
     * Executes the change of position of a registered AR object.
     * Before calling this method, update the position value in RenderObject.
     *
     * @param object The registered AR object.
     */
    public void moveARObject(final RenderObject object) {
        if (mRenderingMode != SmartEyeglassControl.Intents.MODE_AR) {
            // error, not in AR mode so operation not possible
            throw new IllegalStateException("Not in AR mode");
        }

        if (object == null) {
            throw new IllegalArgumentException("object has illegal value");
        }

        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_AR_MOVE_OBJECT_INTENT);
        object.toMoveExtras(intent);
        sendToHostApp(intent);
    }

    /**
     * Sends object data in response to a request from the AR engine for update of a
     * static object display. Call this in your handler for
     * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener#onARObjectRequest}.
     * Image of more than 57822 pixels can not be sent.
     *
     * @param object    The object identified in the request event.
     * @param result    The result passed in the request event.
     */
    public void sendARObjectResponse(final RenderObject object,
            final int result) {
        if (object == null) {
            throw new IllegalArgumentException("object has illegal value");
        }

        if (object.getBitmap() != null
                && (object.getBitmap().getHeight() * object.getBitmap().getWidth()) > IMAGE_PIXEL_SIZE_MAX) {
            throw new IllegalArgumentException("Images that are trying to display too large");
        }

        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_AR_GET_OBJECT_RESPONSE_INTENT);
        object.toObjectResponseExtras(intent);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_AR_RESULT, result);

        sendToHostApp(intent);
    }

    /**
     * Sends image data in response to a request from the AR engine for update of
     * an animation display. Call this in your handler for
     * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener#onARObjectRequest}.
     * Image of more than 57822 pixels can not be sent.
     *
     * @param objectId  The object identified in the request event.
     * @param bitmap    The bitmap for the next animation frame.
     */
    public void sendARAnimationObject(final int objectId, final Bitmap bitmap) {
        if (objectId <= 0) {
            throw new IllegalArgumentException("objectId has illegal value");
        }

        if (bitmap == null) {
            throw new IllegalArgumentException("bitmap has illegal value");
        }

        if ((bitmap.getHeight() * bitmap.getWidth()) > IMAGE_PIXEL_SIZE_MAX) {
            throw new IllegalArgumentException("Images that are trying to display too large");
        }

        if (mLocalRenderAnimationSocket != null) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG, PNG_COMPLESS_QUALITY, byteArrayOutputStream);

                byte[] imageByteArray = byteArrayOutputStream.toByteArray();

                DataOutputStream outputStream = new DataOutputStream(mLocalRenderAnimationSocket.getOutputStream());
                outputStream.writeInt(objectId);
                outputStream.writeInt(SmartEyeglassControl.Intents.INVALID_DISPLAY_DATA_TRANSACTION_NUMBER);
                outputStream.writeInt(imageByteArray.length);
                outputStream.write(imageByteArray);
            } catch (IOException e) {
                if (Dbg.DEBUG) {
                    Dbg.e(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Sends the image data of each frame of the animation object.
     * A processing result will be notified if display processing is completed.
     * Response method {@link com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener#onResultSendAnimationObject(int,int)}
     * Image of more than 57822 pixels can not be sent.
     * @param objectId The object ID.
     * @param bitmap    The bitmap image.
     * @param transactionNumber The transaction number.
     */
    public void sendARAnimationObjectWithCallback(final int objectId, final Bitmap bitmap,
            final int transactionNumber) {
        if (objectId <= 0) {
            throw new IllegalArgumentException("objectId has illegal value");
        }

        if (bitmap == null) {
            throw new IllegalArgumentException("bitmap has illegal value");
        }

        if ((bitmap.getHeight() * bitmap.getWidth()) > IMAGE_PIXEL_SIZE_MAX) {
            throw new IllegalArgumentException("Images that are trying to display too large");
        }

        if (mLocalRenderAnimationSocket != null) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, PNG_COMPLESS_QUALITY, byteArrayOutputStream);

                byte[] imageByteArray = byteArrayOutputStream.toByteArray();

                DataOutputStream outputStream = new DataOutputStream(mLocalRenderAnimationSocket.getOutputStream());
                outputStream.writeInt(objectId);
                outputStream.writeInt(transactionNumber);
                outputStream.writeInt(imageByteArray.length);
                outputStream.write(imageByteArray);
            } catch (IOException e) {
                if (Dbg.DEBUG) {
                    Dbg.e(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Executes the change of rendering order of a registered AR object.
     * Before calling this method, update the rendering order value in the object
     * using {@link com.sony.smarteyeglass.extension.util.ar.GlassesRenderObject#setOrder}
     * or {@link com.sony.smarteyeglass.extension.util.ar.CylindricalRenderObject#setOrder}.
     *
     * @param object The registered object.
     */
    public void changeARObjectOrder(final RenderObject object) {
        if (mRenderingMode != SmartEyeglassControl.Intents.MODE_AR) {
            // error, not in AR mode so operation not possible
            throw new IllegalStateException("Not in AR mode");
        }

        if (object == null) {
            throw new IllegalArgumentException("object has illegal value");
        }

        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_AR_CHANGE_OBJECT_ORDER_INTENT);
        object.toOrderExtras(intent);
        sendToHostApp(intent);
    }

    /**
     * Request to enable AR animation feature. For operation result:
     * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener#onAREnableAnimationResponse}
     */
    public void enableARAnimationRequest() {
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_AR_ENABLE_ANIMATION_REQUEST_INTENT);
        sendToHostApp(intent);
    }

    /**
     * Request to disables AR animation feature. For operation result:
     * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener#onARDisableAnimationResponse}
     */
    public void disableARAnimationRequest() {
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_AR_DISABLE_ANIMATION_REQUEST_INTENT);
        sendToHostApp(intent);
    }

    /**
     * Deletes an AR object.
     * @param object The registered object.
     */
    public void deleteARObject(final RenderObject object) {
        if (object == null) {
            throw new IllegalArgumentException("object has illegal value");
        }
        Intent intent = new Intent(SmartEyeglassControl.Intents.CONTROL_AR_DELETE_OBJECT_INTENT);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_AR_OBJECT_ID, object.getObjectId());
        sendToHostApp(intent);
    }

    /**
     * Defines a point in the real-world coordinate system.
     */
    public class PointInWorldCoordinate {
        /** The latitude, in degrees. */
        public double latitude;
        /** The longitude, in degrees. */
        public double longitude;
        /** The altitude, in meters above sea level. */
        public double altitude;
    }

    /**
     * Converts the position of an object in the real world
     * to the cylindrical coordinate system for a given user viewpoint.
     *
     * @param viewingLocation       A user viewpoint in the real-world coordinate system.
     * @param targetLocation        The position of an object in the real-world coordinate system.
     * @return                      A position in the cylindrical coordinate system.
     */
    public static PointF convertCoordinateSystemFromWorldToCylindrical(
            final PointInWorldCoordinate viewingLocation,
            final PointInWorldCoordinate targetLocation) {
        PointF point = new PointF();
        float[] results = {0, 0, 0};

        Location.distanceBetween(viewingLocation.latitude, viewingLocation.longitude,
                targetLocation.latitude, targetLocation.longitude, results);
        float distance = results[0];
        double angle = getAngle(viewingLocation.altitude, targetLocation.altitude, distance);
        int direction = getPosDirection(viewingLocation.latitude, viewingLocation.longitude,
                targetLocation.latitude, targetLocation.longitude);

        point.set((float) direction, (float) angle);

        return point;
    }

    /**
     * Calculate the angle to the target location from local.
     */
    private static double getAngle(final double startheight, final double endheight, final float poslenght) {
        double radian = Math.atan(Math.abs((endheight - startheight)) / poslenght);
        double degree = Math.toDegrees(radian);
        if (startheight > endheight) {
            return -degree;
        }
        return degree;
    }

    /**
     * Calculate the azimuth to the target location from local.
     */
    private static int getPosDirection(final double startlat, final double startlong, 
            final double endlat, final double endlon) {
        double slat = Math.toRadians(startlat);
        double elat = Math.toRadians(endlat);
        double slng = Math.toRadians(startlong);
        double elng = Math.toRadians(endlon);
        double Y = Math.sin(elng - slng) * Math.cos(elat);
        double X = Math.cos(slat) * Math.sin(elat) - Math.sin(slat) * Math.cos(elat) * Math.cos(elng - slng);
        double deg = Math.toDegrees(Math.atan2(Y, X));
        double angle = (deg + 360) % 360;
        return (int) (Math.abs(angle) + (1 / 7200));
    }

    /**
     * Sends an intent to HostApp, adding package names.
     *
     * @param intent The intent to send.
     */
    protected void sendToHostApp(final Intent intent) {
        ExtensionUtils.sendToHostApp(mContext, mHostAppPackageName, intent);
    }

    /** */
    private ServerThread mServerThread[] = {null, null};
    /** */
    private LocalServerSocket mLocalServerSocket[] = {null, null};
    /** */
    private final String mCameraSocketName[] = {"CameraImage"};
    /** */
    private LocalSocket mLocalRenderAnimationSocket;

    /** */
    private class CameraHandlerCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            CameraEvent cameraEvent = (CameraEvent) msg.obj;
            if (cameraEvent != null && mGeneralEventListener != null) {
                mGeneralEventListener.onCameraReceived(cameraEvent);
            }
            return true;
        }
    };

    /**
     * Creates a socket to read the camera data.
     */
    private void openSocket() throws ControlCameraException {
        try {
            // Open socket
            for (int i = 0; i < mCameraSocketName.length; i++) {
                mLocalServerSocket[i] = new LocalServerSocket(mCameraSocketName[i]);
                if (mLocalServerSocket[i] == null) {
                    if (Dbg.DEBUG) {
                        Dbg.e("Could not create new local server socket");
                    }
                }

                // Stop server listening thread if running
                if (mServerThread[i] != null) {
                    mServerThread[i].interrupt();
                    mServerThread[i] = null;
                }

                // Start server listening thread
                mServerThread[i] = new ServerThread(new Handler(new CameraHandlerCallback()), mLocalServerSocket[i], i);
                mServerThread[i].start();
            }
        } catch (IOException e) {
            if (Dbg.DEBUG) {
                Dbg.e(e.getMessage(), e);
            }
            throw new ControlCameraException(e.getMessage());
        }
    }

    /**
     * Closes the socket to read the sensor data.
     */
    private void closeSocket() {
        // Close socket
        for (int i = 0; i < mLocalServerSocket.length; i++) {
            try {
                if (mLocalServerSocket[i] != null) {
                    mLocalServerSocket[i].close();
                    mLocalServerSocket[i] = null;
                }
            } catch (IOException e) {
                if (Dbg.DEBUG) {
                    Dbg.w(e.getMessage(), e);
                }
            }
        }

        // Stop thread
        for (int i = 0; i < mServerThread.length; i++) {
            if (mServerThread[i] != null) {
                mServerThread[i].interrupt();
                mServerThread[i] = null;
            }
        }
    }

    /**
     * Provides a thread which can read from the socket.
     */
    private class ServerThread extends Thread {
        /** */
        private final Handler mHandler;
        /** */
        private final LocalServerSocket mLocalServerSocket;
        /** */
        private final int mIndex;

        /**
         * Creates a thread which can read from the socket.
         *
         * @param handler The handler to post messages on.
         */
        public ServerThread(final Handler handler,
                final LocalServerSocket localServerSocket, final int index) {
            mHandler = handler;
            mLocalServerSocket = localServerSocket;
            mIndex = index;
        }

        @Override
        public void run() {
            try {
                DataInputStream inStream = new DataInputStream(mLocalServerSocket.accept()
                        .getInputStream());
                while (!isInterrupted()) {
                    CameraEvent event = decodeCameraData(inStream, mIndex);
                    if (event != null) {
                        Message msg = new Message();
                        msg.obj = event;
                        mHandler.sendMessage(msg);
                    }
                }
            } catch (IOException e) {
                if (Dbg.DEBUG) {
                    Dbg.w(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Provides decoding packet from HostApp.
     */
    private CameraEvent decodeCameraData(final DataInputStream inStream,
            final int index) throws IOException {
        int totalLength = inStream.readInt();
        if (totalLength == 0) {
            return null;
        }
        int frameId = inStream.readInt();
        long timestamp = inStream.readLong();
        int dataSize = inStream.readInt();
        if (dataSize > 0) {
            byte[] image = new byte[dataSize];

            int bytesRead, totalBytes = 0;
            while (((bytesRead = inStream.read(image, totalBytes, dataSize - totalBytes)) != -1)
                    && (totalBytes < dataSize)) {
                totalBytes += bytesRead;
            }

            if (totalBytes == dataSize) {
                return new CameraEvent(index, frameId, timestamp, image);
            } else {
                return null;
            }
        } else {
            int status = inStream.readInt();
            return new CameraEvent(index, frameId, timestamp, status);
        }
    }

    /** */
    private static class EightBitMonochromeImageEncoder {

        public static byte[] convert(Bitmap bitmap, int rowOffset, int rowCount) {

            Config config = bitmap.getConfig();

            if (config == Config.ARGB_8888 || config == Config.RGB_565) {
            } else {
                throw new IllegalArgumentException("Only Bitmaps with config ARGB_8888 or RGB_565 are handled.");
            }

            int width = bitmap.getWidth();
            int height = rowCount;
            int pixelCount = width * height;
            int[] pixels = new int[pixelCount];

            bitmap.getPixels(pixels, 0, width, 0, rowOffset, width, Math.min(height, bitmap.getHeight() - rowOffset));

            byte[] resultArray = new byte[pixelCount];
            int bwByteIndex = 0;

            for (int i = 0; i < pixelCount; i++) {
                int pixel = pixels[i];
                int monochromePixcel = getLumaCorrectedMonochromeValueReducedDepth(pixel);
                //note that the RGB_565 type images also returns RGB8888 format when using getPixels

                resultArray[bwByteIndex] = (byte) monochromePixcel;
                bwByteIndex++;
            }

            return resultArray;
        }

        private static int getLumaCorrectedMonochromeValueReducedDepth(int argb) {
            // this function assumes the color format of RGBA8888 on the argb parameter
            int value, alpha, r, g, b;

            alpha = Color.alpha(argb);
            r = Color.red(argb);
            g = Color.green(argb);
            b = Color.blue(argb);

            value = (r * 299 + g * 587 + b * 114) / 1000; // monochrome by luma correction
            value = (value * alpha) / 255;

            return value;
        }
    }

    /**
     * Creates a socket to write the AR Animation data.
     * @param socketAddress LocalSocket address.
     */
    private void connectLocalServer(final String socketAddress) {
        closeLocalServer();

        try {
            mLocalRenderAnimationSocket = new LocalSocket();
            mLocalRenderAnimationSocket.connect(new LocalSocketAddress(socketAddress));
        } catch (Exception e) {
            if (Dbg.DEBUG) {
                Dbg.e("Failed connecting socket for AR Animation data.");
            }
        }
    }

    /**
     * Closes the socket to write the AR Animation data.
     */
    private void closeLocalServer() {
        if (mLocalRenderAnimationSocket != null) {
            try {
                mLocalRenderAnimationSocket.close();
            } catch (Exception e) {
                if (Dbg.DEBUG) {
                    Dbg.e("Failed closing socket for AR Animation data.");
                }
            }
            mLocalRenderAnimationSocket = null;
        }
    }
}
