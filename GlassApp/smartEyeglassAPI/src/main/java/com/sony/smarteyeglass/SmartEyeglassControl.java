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

package com.sony.smarteyeglass;

/**
 * This utility class declares SmartEyeglass constants that you use with the
 * methods provided by {@link com.sony.smarteyeglass.extension.util.SmartEyeglassControlUtils}.
 * <p>
 * SmartEyeglass apps use the SmartExtension framework.
 * The SmartExtension framework extends the Android API to work
 * with Sony Smart Accessories, and the SmartEyeglass API further
 * extends the SmartExtension framework to work with SmartEyeglass device.
 * </p>
 */

public class SmartEyeglassControl {

    protected SmartEyeglassControl() {
    }

    /**
     * Intents sent between a SmartEyeglass app and the HostApp,and related constants..
     * All intents require the "com.sony.smarteyeglass.permission.SMARTEYEGLASS"
     * permission in the app&#39;s Android manifest. Camera and voice-to-text intents
     * require additional specific permissions as noted.
     */
    public interface Intents {
        /**
         * Sent from app to HostApp to request the current SmartEyeglass API version.
         * If the current version is lower than the version required by the app,
         * an error is thrown and the app is terminated.
         * This intent requires the "com.sony.smarteyeglass.permission.SMARTEYEGLASS"
         * permission.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link #EXTRA_VERSION_DATA}</li>
         * </ul>
         *
         * @since 2
         */
        static final String CONTROL_API_VERSION_CONFIRM_INTENT = "com.sony.smarteyeglass.control.API_VERSION_CONFIRM";

        /**
         * Sent from app to HostApp to show a text view on the accessory.
         * <p>
         * Intent-extra data: </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_LAYOUT_REFERENCE}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_TEXT_SHOW_INTENT = "com.sony.smarteyeglass.control.TEXT_SHOW";

        /**
         * Sent from app to HostApp to show a popup message.
         * <p>
         * Intent-extra data: </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link #EXTRA_DIALOG_MODE}</li>
         *   <li>{@link #EXTRA_DIALOG_TITLE} use if EXTRA_DIALOG_MODE is {@link #DIALOG_MODE_USER_DEFINED}</li>
         *   <li>{@link #EXTRA_DIALOG_MESSAGE}</li>
         *   <li>{@link #EXTRA_DIALOG_BUTTONS}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_DIALOG_OPEN_INTENT = "com.sony.smarteyeglass.control.DIALOG_OPEN";

        /**
         * Sent by HostApp when a popup dialog is closed by user action or after a timeout.
         * <p>
         * Intent-extra data: </p>
         * <ul>
         *   <li>{@link #EXTRA_DIALOG_SELECTED_BUTTON_INDEX}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_DIALOG_CLOSED_EVENT_INTENT = "com.sony.smarteyeglass.control.DIALOG_CLOSED_EVENT";

        /**
         * Sent from app to HostApp to initiate a voice-to-text input operation.
         * This intent requires the "com.sony.smarteyeglass.permission.VOICE_TEXT_INPUT" permission.
         * <p>
         * Intent-extra data: </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_VOICE_TEXT_INPUT_ENABLE_INTENT = "com.sony.smarteyeglass.control.VOICE_TEXT_INPUT_ENABLE";

        /**
         * Sent by HostApp when a voice-to-text input operation has been completed.
         * <p>
         * Intent-extra data: </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_ERROR_CODE}</li>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_TEXT}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_VOICE_TEXT_INPUT_NOTIFY_RECOGNIZED_TEXT_EVENT_INTENT = "com.sony.smarteyeglass.control.VOICE_TEXT_INPUT_NOTIFY_RECOGNIZED_TEXT_EVENT";


        /**
         * Sent from app to HostApp to change the power mode.
         * <p>
         * Intent-extra data: </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link #EXTRA_POWER_MODE}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_POWER_MODE_SET_MODE_INTENT = "com.sony.smarteyeglass.control.POWER_MODE_SET_MODE";

        /**
         * Sent by HostApp when the power mode has been changed.
         * <p>
         * Intent-extra data: </p>
         * <ul>
         *   <li>{@link #EXTRA_POWER_MODE}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_POWER_MODE_NOTIFY_MODE_EVENT_INTENT = "com.sony.smarteyeglass.control.POWER_MODE_NOTIFY_MODE_EVENT";

        /**
         * Sent from app to HostApp to change the safe display mode.
         * <p>
         * Intent-extra data: </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link #EXTRA_SAFE_DISPLAY_MODE}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_SAFE_DISPLAY_MODE_SET_MODE_INTENT = "com.sony.smarteyeglass.control.SAFE_DISPLAY_MODE_SET_MODE";

        /**
         * Sent from app to the HostApp to change the screen depth setting.
         * <p>
         * Intent-extra data: </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link #EXTRA_SCREEN_DEPTH}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_SCREEN_DEPTH_SET_DEPTH_INTENT = "com.sony.smarteyeglass.control.SCREEN_DEPTH_SET_DEPTH";

        /**
         * Sent from app to the HostApp to change the camera mode.
         * This intent requires the "com.sony.smarteyeglass.permission.CAMERA" permission.
         * <p>
         * Intent-extra data: </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link #EXTRA_CAMERA_MODE}</li>
         *   <li>{@link #EXTRA_CAMERA_JPEG_QUALITY}</li>
         *   <li>{@link #EXTRA_CAMERA_RESOLUTION}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_CAMERA_SET_MODE_INTENT = "com.sony.smarteyeglass.control.CAMERA_SET_MODE";

        /**
         * Sent from app to HostApp to initiate the camera image-capture operation.
         * This intent requires the "com.sony.smarteyeglass.permission.CAMERA" permission.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_DATA_URI}</li>
         *   <li>{@link #EXTRA_CAMERA_VIDEO_SOCKET_NAME}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_CAMERA_START_INTENT = "com.sony.smarteyeglass.control.CAMERA_START";

        /**
         * Sent from app to HostApp to take a photo.
         * This intent requires the "com.sony.smarteyeglass.permission.CAMERA" permission.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_CAMERA_CAPTURE_STILL_INTENT = "com.sony.smarteyeglass.control.CAMERA_CAPTURE_STILL";

        /**
         * Sent from app to HostApp to stop camera function.
         * This intent requires the "com.sony.smarteyeglass.permission.CAMERA" permission.
         *
         * @since 1
         */
        static final String CONTROL_CAMERA_STOP_INTENT = "com.sony.smarteyeglass.control.CAMERA_STOP";

        /**
         * Sent by HostApp when a record-to-file operation has finished.
         *
         * When camera mode is {@link #CAMERA_MODE_STILL_TO_FILE}, this intent notice is given to {@link #CONTROL_CAMERA_CAPTURE_STILL_INTENT}.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_DATA_URI}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_CAMERA_NOTIFY_CAPTURED_FILE_EVENT_INTENT = "com.sony.smarteyeglass.control.CAMERA_NOTIFY_CAPTURED_FILE_EVENT";

        /**
         * Sent by HostApp if starting the camera-recording operation throws an error.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_ERROR_CODE}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_CAMERA_NOTIFY_ERROR_EVENT_INTENT = "com.sony.smarteyeglass.control.CAMERA_NOTIFY_ERROR_EVENT";

        /**
         * Sent from app to HostApp to change the AR rendering mode.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link #EXTRA_AR_MODE}</li>
         * </ul>
         *
         * @since 2
         */
        static final String CONTROL_AR_SET_MODE_INTENT = "com.sony.smarteyeglass.control.AR_SET_MODE";

        /**
         * Send from app to HostApp to change the cylindrical height in AR mode.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link #EXTRA_AR_CYLINDRICAL_VERTICAL_RANGE}</li>
         * </ul>
         *
         * @since 2
         */
        static final String CONTROL_AR_CHANGE_CYLINDRICAL_VERTICAL_RANGE_INTENT = "com.sony.smarteyeglass.control.AR_CHANGE_CYLINDRICAL_VERTICAL_RANGE";

        /**
         * Sent from app to HostApp to initiate AR animation.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         * </ul>
         *
         * @since 2
         */
        static final String CONTROL_AR_ENABLE_ANIMATION_REQUEST_INTENT = "com.sony.smarteyeglass.control.AR_ENABLE_ANIMATION_REQUEST";

        /**
         * Sent by HostApp in response to {@link #CONTROL_AR_ENABLE_ANIMATION_REQUEST_INTENT}.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link #EXTRA_AR_ANIMATION_SOCKET_NAME}</li>
         *   <li>{@link #EXTRA_AR_RESULT}</li>
         * </ul>
         *
         * @since 2
         */
        static final String CONTROL_AR_ENABLE_ANIMATION_RESPONSE_INTENT = "com.sony.smarteyeglass.control.AR_ENABLE_ANIMATION_RESPONSE";

        /**
         * Sent from app to HostApp to terminate an animation.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         * </ul>
         *
         * @since 2
         */
        static final String CONTROL_AR_DISABLE_ANIMATION_REQUEST_INTENT = "com.sony.smarteyeglass.control.AR_DISABLE_ANIMATION_REQUEST";

        /**
         * Sent by HostApp in response to {@link #CONTROL_AR_DISABLE_ANIMATION_REQUEST_INTENT}.
         *
         * @since 2
         */
        static final String CONTROL_AR_DISABLE_ANIMATION_RESPONSE_INTENT = "com.sony.smarteyeglass.control.AR_DISABLE_ANIMATION_RESPONSE";

        /**
         * Sent from app to HostApp to register a new object in AR mode.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link #EXTRA_AR_OBJECT_ID}</li>
         *   <li>{@link #EXTRA_AR_COORDINATE_TYPE}</li>
         *   <li>{@link #EXTRA_AR_POS_X} (use if coordinate type is {@link #AR_COORDINATE_TYPE_GLASSES})</li>
         *   <li>{@link #EXTRA_AR_POS_Y} (use if coordinate type is {@link #AR_COORDINATE_TYPE_GLASSES})</li>
         *   <li>{@link #EXTRA_AR_CYLINDRICAL_POS_H} (use if coordinate type is {@link #AR_COORDINATE_TYPE_CYLINDRICAL})</li>
         *   <li>{@link #EXTRA_AR_CYLINDRICAL_POS_V} (use if coordinate type is {@link #AR_COORDINATE_TYPE_CYLINDRICAL})</li>
         *   <li>{@link #EXTRA_IMAGE_WIDTH}</li>
         *   <li>{@link #EXTRA_IMAGE_HEIGHT}</li>
         *   <li>{@link #EXTRA_AR_ORDER}</li>
         *   <li>{@link #EXTRA_AR_OBJECT_TYPE}</li>
         * </ul>
         *
         * @since 2
         */
        static final String CONTROL_AR_REGISTER_OBJECT_REQUEST_INTENT = "com.sony.smarteyeglass.control.AR_REGISTER_OBJECT_REQUEST";

        /**
         * Sent by HostApp in response to {@link #CONTROL_AR_REGISTER_OBJECT_REQUEST_INTENT}.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link #EXTRA_AR_RESULT}</li>
         *   <li>{@link #EXTRA_AR_OBJECT_ID}</li>
         * </ul>
         *
         * @since 2
         */
        static final String CONTROL_AR_REGISTER_OBJECT_RESPONSE_INTENT = "com.sony.smarteyeglass.control.AR_REGISTER_OBJECT_RESPONSE";

        /**
         * Sent by HostApp to request image data to render.
         * The app must respond by sending the {@link #CONTROL_AR_GET_OBJECT_RESPONSE_INTENT}
         * with image data to be rendered.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link #EXTRA_AR_OBJECT_ID}</li>
         * </ul>
         *
         * @since 2
         */
        static final String CONTROL_AR_GET_OBJECT_REQUEST_INTENT = "com.sony.smarteyeglass.control.AR_GET_OBJECT_REQUEST";

        /**
         * Sent from app to HostApp in response to {@link #CONTROL_AR_GET_OBJECT_REQUEST_INTENT}.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link #EXTRA_AR_RESULT}</li>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_DATA}</li>
         * </ul>
         *
         * @since 2
         */
        static final String CONTROL_AR_GET_OBJECT_RESPONSE_INTENT = "com.sony.smarteyeglass.control.AR_GET_OBJECT_RESPONSE";

        /**
         * Sent from app to HostApp to change the rendering order of an object.
         * An object with an order value of zero is drawn in the foreground, on top of other
         * objects. Objects with greater values are rendered further in the background.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link #EXTRA_AR_OBJECT_ID}</li>
         *   <li>{@link #EXTRA_AR_ORDER}</li>
         * </ul>
         *
         * @since 2
         */
        static final String CONTROL_AR_CHANGE_OBJECT_ORDER_INTENT = "com.sony.smarteyeglass.control.AR_CHANGE_OBJECT_ORDER";

        /**
         * Sent from app to HostApp to change the position of an object.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link #EXTRA_AR_OBJECT_ID}</li>
         *   <li>{@link #EXTRA_AR_COORDINATE_TYPE}</li>
         *   <li>{@link #EXTRA_AR_POS_X}</li> use if coordinate type is {@link #AR_COORDINATE_TYPE_GLASSES}
         *   <li>{@link #EXTRA_AR_POS_Y}</li> use if coordinate type is {@link #AR_COORDINATE_TYPE_GLASSES}
         *   <li>{@link #EXTRA_AR_CYLINDRICAL_POS_H}</li> use if coordinate type is {@link #AR_COORDINATE_TYPE_CYLINDRICAL}
         *   <li>{@link #EXTRA_AR_CYLINDRICAL_POS_V}</li> use if coordinate type is {@link #AR_COORDINATE_TYPE_CYLINDRICAL}
         * </ul>
         *
         * @since 2
         */
        static final String CONTROL_AR_MOVE_OBJECT_INTENT = "com.sony.smarteyeglass.control.AR_MOVE_OBJECT";

        /**
         * Sent from app to HostApp to delete a rendering object. An object ID value of zero will delete all objects.
         * Also note that all objects are automatically deleted when the control extension is paused or if the rendering mode is changed to standard rendering mode.
         * This intent requires the "com.sony.smarteyeglass.permission.SMARTEYEGLASS"
         * permission.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link #EXTRA_AR_OBJECT_ID}</li>
         * </ul>
         *
         * @since 2
         */
        static final String CONTROL_AR_DELETE_OBJECT_INTENT = "com.sony.smarteyeglass.control.AR_DELETE_OBJECT";

        /**
         * Sent by HostApp when the display is turned on or off, either manually by the user or automatically.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link #EXTRA_DISPLAY_STATUS}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_DISPLAY_NOTIFY_STATUS_EVENT_INTENT = "com.sony.smarteyeglass.control.DISPLAY_NOTIFY_STATUS_EVENT";

        /**
         * Sent from HostApp to app when display processing is completed, in response to
         * {@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#CONTROL_DISPLAY_DATA_INTENT}.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link #EXTRA_DISPLAY_DATA_TRANSACTION_NUMBER}</li>
         *   <li>{@link #EXTRA_DISPLAY_DATA_RESULT}</li>
         *   <li>{@link #EXTRA_DISPLAY_DATA_TYPE}</li>
         *  </ul>
         *
         * @since 2
         */
        static final String CONTROL_DISPLAY_DATA_RESULT_INTENT = "com.sony.smarteyeglass.control.DISPLAY_DATA_RESULT";

        /**
         *  Sent from HostApp to app when display processing of an AR animation object is completed.
         *  <p>
         *  Intent-extra data:
         *  </p>
         *  <ul>
         *    <li>{@link #EXTRA_DISPLAY_DATA_TRANSACTION_NUMBER}</li>
         *    <li>{@link #EXTRA_DISPLAY_DATA_RESULT}</li>
         *  </ul>
         *
         * @since 2
         */
        static final String CONTROL_AR_ANIMATION_RESULT_INTENT = "com.sony.smarteyeglass.control.AR_ANIMATION_RESULT";

        /**
         * Sent from app to HostApp to request the battery status.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_BATTERY_GET_LEVEL_REQUEST_INTENT = "com.sony.smarteyeglass.control.BATTERY_GET_LEVEL_REQUEST";

        /**
         * Sent by HostApp in response to a request for battery status.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link #EXTRA_BATTERY_LEVEL}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_BATTERY_GET_LEVEL_RESPONSE_INTENT = "com.sony.smarteyeglass.control.BATTERY_GET_LEVEL_RESPONSE";

        /**
         * Sent from app to HostApp to request the status of the telephony function.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_TELEPHONY_GET_MODE_REQUEST_INTENT = "com.sony.smarteyeglass.control.TELEPHONY_GET_MODE_REQUEST";

        /**
         * Sent by HostApp in response to a request for the status of the telephony function.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link #EXTRA_TELEPHONY_MODE}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_TELEPHONY_GET_MODE_RESPONSE_INTENT = "com.sony.smarteyeglass.control.TELEPHONY_GET_MODE_RESPONSE";

        /**
         * Sent from app to HostApp to enable or disable the sound effect that provides feedback for user input
         * actions on the controller (key press or touch pad action).
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link #EXTRA_SOUND_EFFECT_MODE}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_SOUND_EFFECT_SET_MODE_INTENT = "com.sony.smarteyeglass.control.SOUND_EFFECT_SET_MODE";

        /**
         * Sent by HostApp to request confirmation when the standby mode is changed.
         *
         * @since 1
         */
        static final String CONTROL_STANDBY_CONFIRM_REQUEST_INTENT = "com.sony.smarteyeglass.control.STANDBY_CONFIRM_REQUEST";

        /**
         * Sent from app to HostApp to provide confirmation of a change in standby mode.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         *   <li>{@link #EXTRA_STANDBY_CONFIRMED_RESULT}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_STANDBY_CONFIRM_RESPONSE_INTENT = "com.sony.smarteyeglass.control.STANDBY_CONFIRM_RESPONSE";

        /**
         * Sent by HostApp to request the current standby-mode status.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link #EXTRA_STANDBY_CONDITION}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_STANDBY_NOTIFY_CONDITION_EVENT_INTENT = "com.sony.smarteyeglass.control.STANDBY_NOTIFY_CONDITION_EVENT";

        /**
         * Sent from app to HostApp in response to a request for the current standby-mode status.
         * <p>
         * Intent-extra data:
         * </p>
         * <ul>
         *   <li>{@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_AEA_PACKAGE_NAME}</li>
         * </ul>
         *
         * @since 1
         */
        static final String CONTROL_STANDBY_ENTER_INTENT = "com.sony.smarteyeglass.control.STANDBY_ENTER";

        /**
         * Intent-extra, a SmartEyeglass API version number.
         * <p>
         * TYPE: (INTEGER) (int)
         * </p>
         * <p>
         * ALLOWED VALUES: >= 1 (INTEGER)
         * </p>
         *
         * @since 2
         */
        static final String EXTRA_VERSION_DATA = "version_data";

        /**
         * Intent-extra, whether data can be sent to the accessory in a raw format.
         * If not supplied, data is assumed to be in the standard image format.
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         * <p>
         * ALLOWED VALUES:
         * <ul>
         *   <li>0: &#39;The data is in standard image format(8bit bitmap format)&#39; This is the default. </li>
         *   <li>1: &#39;The data is in raw image format which can be sent directly to the accessory(8bit data sequences for every pixels)&#39;</li>
         * </ul>
         * </p>
         *
         * @since 1
         */
        static final String EXTRA_DATA_IS_RAW_FORMAT = "data_is_raw_format";

        /**
         * Intent-extra, a Dialog message mode.
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         * <p>
         * ALLOWED VALUES:
         * <ul>
         *   <li>{@link #DIALOG_MODE_TIMEOUT}</li>
         *   <li>{@link #DIALOG_MODE_OK}</li>
         *   <li>{@link #DIALOG_MODE_USER_DEFINED}</li>
         * </ul>
         * </p>
         *
         * @since 1
         */
        static final String EXTRA_DIALOG_MODE = "dialog_mode";

        /**
         * Intent-extra, a Dialog title.
         * <p>
         * TYPE: STRING
         * </p>
         *
         * @since 1
         */
        static final String EXTRA_DIALOG_TITLE = "dialog_title";

        /**
         * Intent-extra, a Dialog message.
         * <p>
         * TYPE: STRING
         * </p>
         *
         * @since 1
         */
        static final String EXTRA_DIALOG_MESSAGE = "dialog_message";

        /**
         * Intent-extra, user-defined buttons for a Dialog.
         * When you use this definition, please specify {@link #EXTRA_DIALOG_MODE} as {@link #DIALOG_MODE_USER_DEFINED}.
         * <p>
         * TYPE: STRING_ARRAY
         * </p>
         *
         * @since 1
         */
        static final String EXTRA_DIALOG_BUTTONS = "dialog_buttons";

        /**
         * Intent-extra, a camera capture mode.
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         * <p>
         * ALLOWED VALUES:
         * <ul>
         *   <li>{@link #CAMERA_MODE_STILL}</li>
         *   <li>{@link #CAMERA_MODE_STILL_TO_FILE}</li>
         *   <li>{@link #CAMERA_MODE_JPG_STREAM_LOW_RATE}</li>
         *   <li>{@link #CAMERA_MODE_JPG_STREAM_HIGH_RATE}</li>
         * </ul>
         * </p>
         *
         * @since 1
         */
        static final String EXTRA_CAMERA_MODE = "camera_mode";

        /**
         * Intent-extra, the JPEG quality setting for the camera.
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         * <p>
         * ALLOWED VALUES:
         * <ul>
         *   <li>{@link #CAMERA_JPEG_QUALITY_STANDARD}</li>
         *   <li>{@link #CAMERA_JPEG_QUALITY_FINE}</li>
         *   <li>{@link #CAMERA_JPEG_QUALITY_SUPER_FINE}</li>
         * </ul>
         * </p>
         *
         * @since 1
         */
        static final String EXTRA_CAMERA_JPEG_QUALITY= "camera_jpeg_quality";

        /**
         * Intent-extra, the resolution setting for the camera.
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         * <p>
         * ALLOWED VALUES:
         * <ul>
         *   <li>{@link #CAMERA_RESOLUTION_3M}</li>
         *   <li>{@link #CAMERA_RESOLUTION_1M}</li>
         *   <li>{@link #CAMERA_RESOLUTION_VGA}</li>
         *   <li>{@link #CAMERA_RESOLUTION_QVGA}</li>
         * </ul>
         * </p>
         *
         * @since 1
         */
        static final String EXTRA_CAMERA_RESOLUTION = "camera_resolution";

        /**
         * Intent-extra, the name of the Android Local Server Socket
         * that is waiting for a connection from HostApp
         * to communicate picture data from the camera.
         * <p>
         * TYPE: STRING (String)
         * </p>
         *
         * @since 1
         */
        static final String EXTRA_CAMERA_VIDEO_SOCKET_NAME = "camera_video_socket_name";

        /**
         * Intent-extra, the width of a static image in pixels. Max value is 419..
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         *
         * @since 1
         */
        static final String EXTRA_IMAGE_WIDTH = "image_width";

        /**
         * Intent-extra, the height of a static image in pixels. Max value is 138..
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         *
         * @since 1
         */
        static final String EXTRA_IMAGE_HEIGHT = "image_height";

        /**
         * Intent-extra, the direction of a screen-layer animation.
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         * <p>
         * ALLOWED VALUES:
         * <ul>
         *   <li>{@link #LAYER_TRANSITION_MOVE_LOWER_LAYER}</li>
         *   <li>{@link #LAYER_TRANSITION_MOVE_UPPER_LAYER}</li>
         * </ul>
         * </p>
         *
         * @since 1
         */
        static final String EXTRA_LAYER_TRANSITION_EFFECT_TYPE = "layer_transition_effect_type";

        /**
         * Intent-extra, a power mode.
         * <p>
         * TYPE: INTEGER
         * </p>
         * <p>
         * ALLOWED VALUES:
         * <ul>
         *   <li>{@link #POWER_MODE_HIGH} (uses Wi-Fi connection for higher performance)</li>
         *   <li>{@link #POWER_MODE_NORMAL} (uses default Bluetooth connection)</li>
         * </ul>
         * </p>
         *
         * @since 1
         */
        static final String EXTRA_POWER_MODE = "power_mode";

        /**
         * Intent-extra, the display status.
         * <p>
         * TYPE: INTEGER
         * </p>
         * <p>
         * ALLOWED VALUES:
         * <ul>
         *   <li> {@link #DISPLAY_STATUS_OFF}</li>
         *   <li> {@link #DISPLAY_STATUS_ON}</li>
         * </ul>
         * </p>
         *
         * @since 1
         */
        static final String EXTRA_DISPLAY_STATUS = "display_status";

        /**
         * Intent-extra, the safe display mode.
         * <p>
         * TYPE: INTEGER
         * </p>
         * <p>
         * ALLOWED VALUES:
         * <ul>
         *   <li>{@link #SAFE_DISPLAY_MODE_NONE}</li>
         *   <li>{@link #SAFE_DISPLAY_MODE_1}</li>
         * </ul>
         * </p>
         */
        static final String EXTRA_SAFE_DISPLAY_MODE = "safe_display_mode";

        /**
         * Intent-extra, the telephony function status.
         * <p>
         * TYPE: INTEGER
         * </p>
         * <p>
         * ALLOWED VALUES:
         * <ul>
         *   <li> {@link #TELEPHONY_MODE_BT_HEADSET_DISABLE}</li>
         *   <li> {@link #TELEPHONY_MODE_BT_HEADSET_ENABLE}</li>
         * </ul>
         * </p>
         */
        static final String EXTRA_TELEPHONY_MODE = "telephony_mode";

        /**
         * Intent-extra, the selected dialog button index.
         * <p>
         * TYPE: INTEGER
         * </p>
         */
        static final String EXTRA_DIALOG_SELECTED_BUTTON_INDEX = "dialog_selected_button_index";

        /**
         * Intent-extra, the screen depth position.
         * <p>
         * TYPE: INTEGER
         * </p>
         * <p>
         * ALLOWED VALUES: from -4 to 6 (same as hardware settings preference)
         * <ul>
         *   <li> 0 is about 5 meters(default)</li>
         *   <li> 6 is near</li>
         *   <li> -4 is about 10 meters or more</li>
         * </ul>
         * </p>
         */
        static final String EXTRA_SCREEN_DEPTH = "screen_depth";

        /**
         * Intent-extra, the battery status.
         * <p>
         * TYPE: INTEGER (int) in battery charge percentage (0 - 100%)
         * </p>
         */
        static final String EXTRA_BATTERY_LEVEL = "battery_level";

        /**
         * Intent-extra, the sound effect setting.
         * <p>
         * TYPE: INTEGER
         * </p>
         * <p>
         * ALLOWED VALUES:
         * <ul>
         *   <li> {@link #SOUND_EFFECT_OFF}</li>
         *   <li> {@link #SOUND_EFFECT_ON}</li>
         * </ul>
         * </p>
         */
        static final String EXTRA_SOUND_EFFECT_MODE = "sound_effect_mode";

        /**
         * Intent-extra, the standby mode setting.
         * <p>
         * TYPE: INTEGER
         * </p>
         * <p>
         * ALLOWED VALUES:
         * <ul>
         *   <li> {@link #STANDBY_MODE_OFF}</li>
         *   <li> {@link #STANDBY_MODE_ON}</li>
         * </ul>
         * </p>
         */
        static final String EXTRA_STANDBY_CONDITION = "standby_condition";

        /**
         * Intent-extra, the standby confirm result.
         * <p>
         * TYPE: INTEGER
         * </p>
         * <p>
         * ALLOWED VALUES:
         * <ul>
         *   <li> {@link #STANDBY_CONFIRMED_RESULT_NG}</li>
         *   <li> {@link #STANDBY_CONFIRMED_RESULT_OK}</li>
         * </ul>
         * </p>
         */
        static final String EXTRA_STANDBY_CONFIRMED_RESULT = "standby_confirmed_result";

        /**
         * Standby mode changes confirm result NG.
         *
         * @since 1
         */
        static final int STANDBY_CONFIRMED_RESULT_NG = 0;

        /**
         * Standby mode changes confirm result OK.
         *
         * @since 1
         */
        static final int STANDBY_CONFIRMED_RESULT_OK = 1;

        /**
         *Dialog closes automatically after 5 seconds.
         *
         * @since 1
         */
        static final int DIALOG_MODE_TIMEOUT = 1;

        /**
         * Dialog requires a confirmation to close.
         *
         * @since 1
         */
        static final int DIALOG_MODE_OK = 2;

        /**
         * Dialog with user-defined message and buttons, requires a confirmation to close.
         *
         * @since 1
         */
        static final int DIALOG_MODE_USER_DEFINED = 3;

        /**
         * Standard JPEG Quality.
         *
         * @since 1
         */
        static final int CAMERA_JPEG_QUALITY_STANDARD = 1;

        /**
         * Fine JPEG Quality.
         *
         * @since 1
         */
        static final int CAMERA_JPEG_QUALITY_FINE = 2;

        /**
         * Super-fine JPEG Quality.
         *
         * @since 1
         */
        static final int CAMERA_JPEG_QUALITY_SUPER_FINE = 3;

        /**
         * 3 Megapixel camera resolution.
         *
         * @since 1
         */
        static final int CAMERA_RESOLUTION_3M = 0;

        /**
         * 1 Megapixel camera resolution.
         *
         * @since 1
         */
        static final int CAMERA_RESOLUTION_1M = 1;

        /**
         * VGA camera resolution.
         *
         * @since 1
         */
        static final int CAMERA_RESOLUTION_VGA = 4;

        /**
         * QVGA camera resolution.
         *
         * @since 1
         */
        static final int CAMERA_RESOLUTION_QVGA = 6;

        /**
         * Camera takes a still picture and saves the file
         * using a specified Socket. In this mode, a picture is
         * taken by {@link #CONTROL_CAMERA_CAPTURE_STILL_INTENT}. The captured
         * image is written using the SOCKET specified by
         * {@link #EXTRA_CAMERA_VIDEO_SOCKET_NAME}.
         *
         * @since 1
         */
        static final int CAMERA_MODE_STILL = 0;

        /**
         * Camera takes a still picture and saves the file
         * using a specified URI. In this mode, a picture is
         * taken by {@link #CONTROL_CAMERA_CAPTURE_STILL_INTENT}. The captured
         * image is saved on the file path specified as the
         * {@link com.sonyericsson.extras.liveware.aef.control.Control.Intents#EXTRA_DATA_URI}
         * parameter of {@link #CONTROL_CAMERA_START_INTENT}. After the image
         * capture, completion is reported by
         * {@link #CONTROL_CAMERA_NOTIFY_CAPTURED_FILE_EVENT_INTENT}.

         * @since 1
         */
        static final int CAMERA_MODE_STILL_TO_FILE = 1;

        /**
         * Camera takes still pictures continuously at 7.5fps.
         * In this mode, the image capture is started by
         * {@link #CONTROL_CAMERA_START_INTENT}. The captured images are written
         * using the SOCKET specified by {@link #EXTRA_CAMERA_VIDEO_SOCKET_NAME}.
         *
         * @since 1
         */
        static final int CAMERA_MODE_JPG_STREAM_LOW_RATE = 2;

        /**
         * Camera takes still pictures continuously at 15fps.
         * In this mode, the image capture is started by
         * {@link #CONTROL_CAMERA_START_INTENT}. The captured images are written
         * using the SOCKET specified by {@link #EXTRA_CAMERA_VIDEO_SOCKET_NAME}.
         *
         * @since 1
         */
        static final int CAMERA_MODE_JPG_STREAM_HIGH_RATE = 3;

        /**
         * Power mode constant value for high bandwidth (WiFi is used).
         *
         * @since 1
         */
        static final int POWER_MODE_HIGH = 0;

        /**
         * Power mode constant value for normal bandwidth (Bluetooth is used).
         * This value is used by default when starting an app.
         *
         * @since 1
         */
        static final int POWER_MODE_NORMAL = 1;

        /**
         * Safe display mode is off. The full display is rendered.
         *
         * @since 1
         */
        static final int SAFE_DISPLAY_MODE_NONE = 0;

        /**
         * A Safe display mode is on. Only the bottom half of the display is rendered.
         *
         * @since 1
         */
        static final int SAFE_DISPLAY_MODE_1 = 1;

        /**
         * Display is off.
         *
         * @since 1
         */
        static final int DISPLAY_STATUS_OFF = 0;

        /**
         * Display is on.
         *
         * @since 1
         */
        static final int DISPLAY_STATUS_ON = 1;

        /**
         * Sound effect feedback for the controller is off.
         *
         * @since 1
         */
        static final int SOUND_EFFECT_OFF = 0;

        /**
         * Sound effect feedback for the controller is on.
         *
         * @since 1
         */
        static final int SOUND_EFFECT_ON = 1;

        /**
         * Standby mode is off.
         *
         * @since 1
         */
        static final int STANDBY_MODE_OFF = 0;

        /**
         * Standby mode is on.
         *
         * @since 1
         */
        static final int STANDBY_MODE_ON = 1;

        /**
         * The telephony function is disabled in the BT Headset.
         *
         * @since 1
         */
        static final int TELEPHONY_MODE_BT_HEADSET_DISABLE = 0;

        /**
         * The telephony function is enabled in the BT Headset.
         *
         * @since 1
         */
        static final int TELEPHONY_MODE_BT_HEADSET_ENABLE = 1;

        /**
         * The layer transition animation type for moving down to a lower layer.
         *
         * @since 1
         */
        static final int LAYER_TRANSITION_MOVE_LOWER_LAYER = 0;

        /**
         * The layer transition animation type for moving up to an higher layer.
         *
         * @since 1
         */
        static final int LAYER_TRANSITION_MOVE_UPPER_LAYER = 1;

        /**
         * Error code returned when an invalid parameter is passed.
         * Return this value to {@link #CONTROL_CAMERA_START_INTENT}.
         *
         * @since 1
         */
        static final int ERROR_INVALID_PARAMETER = -1;

        /**
         * Error code returned when file cannot be accessed or the file path is invalid.
         * Return this value to {@link #CONTROL_CAMERA_CAPTURE_STILL_INTENT}.
         *
         * @since 1
         */
        static final int ERROR_FILE_ACCESS = -2;

        /**
         * Error code returned when the image-capture operation failed.
         * Return this value to {@link #CONTROL_CAMERA_CAPTURE_STILL_INTENT}.
         *
         * @since 3
         */
        static final int ERROR_CAPTURE = -3;

        /**
         * Intent-extra, the transaction number.
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         * <p>
         * ALLOWED VALUES: >= 0 (UINT16)
         * </p>
         *
         * @since 2
         */
        static final String EXTRA_DISPLAY_DATA_TRANSACTION_NUMBER = "display_data_transaction_number";

        /**
         * Intent-extra, the result of a draw operation.
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         * <p>
         * ALLOWED VALUES:
         * <ul>
         *   <li>{@link #DISPLAY_DATA_RESULT_OK}</li>
         *   <li>{@link #DISPLAY_DATA_RESULT_CANNOT_DRAW}</li>
         * </ul>
         * </p>
         *
         * @since 2
         */
        static final String EXTRA_DISPLAY_DATA_RESULT = "display_data_result";

        /**
         * Intent-extra, the display data type.
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         * <p>
         * ALLOWED VALUES:
         * <ul>
         *   <li>{@link #DISPLAY_DATA_TYPE_SHOW_BITMAP}</li>
         *   <li>{@link #DISPLAY_DATA_TYPE_SHOW_IMAGE}</li>
         * </ul>
         * </p>
         *
         * @since 2
         */
        static final String EXTRA_DISPLAY_DATA_TYPE = "display_data_type";

        /**
         * Transaction error for an invalid value.
         *
         * @since 2
         */
        static final int INVALID_DISPLAY_DATA_TRANSACTION_NUMBER = -1;

        /**
         * A draw operation result when the operation succeeded.
         *
         * @since 2
         */
        static final int DISPLAY_DATA_RESULT_OK = 0;

        /**
         * A draw operation result when the operation did not succeed.
         * 
         * @since 2
         */
        static final int DISPLAY_DATA_RESULT_CANNOT_DRAW = 1;

        /**
         * Drawing type for display processing, for a draw operation
         * initiated with showBitmap().
         *
         * @since 2
         */
        static final int DISPLAY_DATA_TYPE_SHOW_BITMAP = 0;

        /**
         * Drawing type for display processing, for a draw operation
         * initiated with showImage().
         *
         * @since 2
         */
        static final int DISPLAY_DATA_TYPE_SHOW_IMAGE = 1;

        /**
         * This value is returned result of voice-to-text input operation.
         * Status constant indicates successful voice-to-text input operation.
         *
         * @since 3
         */
        static final int VOICE_TEXT_INPUT_RESULT_OK = 0;

        /**
         * This value is returned result of voice-to-text input operation.
         * Status constant indicates failed voice-to-text input operation.
         *
         * @since 3
         */
        static final int VOICE_TEXT_INPUT_RESULT_FAILED = 1;

        /**
         * This value is returned result of voice-to-text input operation.
         * Status constant indicates canceled voice-to-text input operation.
         *
         * @since 3
         */
        static final int VOICE_TEXT_INPUT_RESULT_CANCEL = 2;

        /**
         * Intent-extra, the rendering mode, one of:
         * <ul>
         *   <li>{@link #MODE_NORMAL}</li>
         *   <li>{@link #MODE_AR}</li>
         * </ul>
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         *
         * @since 2
         */
        static final String EXTRA_AR_MODE = "ar_mode";

        /**
         * Intent-extra, the cylindrical vertical range in degrees, a positive value > 0 .
         * <p>
         * TYPE: FLOAT (float)
         * </p>
         *
         * @since 2
         */
        static final String EXTRA_AR_CYLINDRICAL_VERTICAL_RANGE = "ar_cylindrical_vertical_range";

        /**
         * Intent-extra, the name of the Android Local Server Socket
         * that is currently waiting for a connection from HostApp
         * <p>
         * TYPE: TEXT
         * </p>
         *
         * @since 2
         */
        static final String EXTRA_AR_ANIMATION_SOCKET_NAME = "ar_animation_socket_name";

        /**
         * Intent-extra, the object ID, a positive UINT16 > 0 ()
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         * @since 2
         */
        static final String EXTRA_AR_OBJECT_ID = "ar_object_id";

        /**
         * Intent-extra, the coordinate system type, one of:
         * <ul>
         *   <li>{@link #AR_COORDINATE_TYPE_GLASSES}</li>
         *   <li>{@link #AR_COORDINATE_TYPE_CYLINDRICAL}</li>
         * </ul>
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         *
         * @since 2
         */
        static final String EXTRA_AR_COORDINATE_TYPE = "ar_coordinate_type";

        /**
         * Intent-extra, the X position of the object, a pixel value in the range [0 to display_width].
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         *
         * @since 2
         */
        static final String EXTRA_AR_POS_X = "ar_pos_x";

        /**
         * Intent-extra, the Y position of the object, a pixel value in the range [0 to display_height].
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         *
         * @since 2
         */
        static final String EXTRA_AR_POS_Y = "ar_pos_y";

        /**
         * Intent-extra, the compass direction of the object in degrees,
         * a value in the range 0.0 to 360.0.
         * A  value of 0.0 means north and a value of 180.0 means south.
         * <p>
         * TYPE: FLOAT (float)
         * </p>
         *
         * @since 2
         */
        static final String EXTRA_AR_CYLINDRICAL_POS_H = "ar_cylindrical_pos_h";

        /**
         * Intent-extra, the top position of the object in degrees
         * in the range -90.0 to 90.0.
         * <p>
         * TYPE: FLOAT (float)
         * </p>
         * @since 2
         */
        static final String EXTRA_AR_CYLINDRICAL_POS_V = "ar_cylindrical_pos_v";

        /**
         * Intent-extra, the AR object type. One of:
         * <ul>
         *   <li>{@link #AR_OBJECT_TYPE_STATIC_IMAGE}</li>
         *   <li>{@link #AR_OBJECT_TYPE_ANIMATED_IMAGE}</li>
         * </ul>
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         *
         * @since 2
         */
        static final String EXTRA_AR_OBJECT_TYPE = "ar_object_type";

        /**
         * Intent-extra, the result of AR rendering operation. One of:
         * <ul>
         *   <li>{@link #AR_RESULT_OK}</li>
         *   <li>{@link #AR_RESULT_ERROR_PARAMETER_ERROR}</li>
         *   <li>{@link #AR_RESULT_ERROR_MEMORY_SHORTAGE}</li>
         *   <li>{@link #AR_RESULT_ERROR_SYSTEM}</li>
         * </ul>
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         *
         * @since 2
         */
        static final String EXTRA_AR_RESULT = "ar_result";

        /**
         * Intent-extra, the rendering order of the object,a positive integer.
         * A value of zero means in the foreground, object with higher values are further back.
         * <p>
         * TYPE: INTEGER (int)
         * </p>
         *
         * @since 2
         */
        static final String EXTRA_AR_ORDER = "ar_order";

        /**
         * The standard control rendering mode (full screen or part of a screen update).
         *
         * @since 2
         */
        static final int MODE_NORMAL = 0;

        /**
         * AR mode, renders registered objects according to their location, rendering order, and so on.
         *
         * @since 2
         */
        static final int MODE_AR = 1;

        /**
         * Coordinate system relative to the SmartEyeglass display,
         * an absolute position in pixels.
         *
         * @since 2
         */
        static final int AR_COORDINATE_TYPE_GLASSES = 0;

        /**
         * Cylindrical coordinate system for the space around the user, in degrees (0.0-360.0).
         *
         * @since 2
         */
        static final int AR_COORDINATE_TYPE_CYLINDRICAL = 1;

        /**
         * The object is a static image.
         *
         * @since 2
         */
        static final int AR_OBJECT_TYPE_STATIC_IMAGE = 0;

        /**
         * The object is an animated image.
         *
         * @since 2
         */
        static final int AR_OBJECT_TYPE_ANIMATED_IMAGE = 1;

        /**
         * The operation succeeded.
         *
         * @since 2
         */
        static final int AR_RESULT_OK = 0;

        /**
         * The operation failed due to incorrect parameter values.
         *
         * @since 2
         */
        static final int AR_RESULT_ERROR_PARAMETER_ERROR = 1;

        /**
         * The operation failed due to shortage of memory.
         *
         * @since 2
         */
        static final int AR_RESULT_ERROR_MEMORY_SHORTAGE = 2;

        /**
         * The operation failed due to a system error.
         *
         * @since 2
         */
        static final int AR_RESULT_ERROR_SYSTEM = 3;
    }
}
