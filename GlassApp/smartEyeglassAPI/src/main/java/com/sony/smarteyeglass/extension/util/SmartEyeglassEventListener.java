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

/**
 * The event listener interface for SmartEyeglass events. Your implementation should
 * override the methods to provide handler callbacks for specific events.
 * Each event-handler function is executed in the context that created the associated instance of
 * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassControlUtils}.
 */
public class SmartEyeglassEventListener {

    /**
     * Called on completion of a voice-to-text operation.
     *
     * @param errorCode The result of the operation, one of these constants:
     *            <ul>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#VOICE_TEXT_INPUT_RESULT_OK}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#VOICE_TEXT_INPUT_RESULT_FAILED}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#VOICE_TEXT_INPUT_RESULT_CANCEL}</li>
     *            </ul>
     * @param text      The text transcription of the voice input.
     */
    public void onVoiceTextInput(final int errorCode, final String text) { }

    /**
     * Called when a dialog is dismissed by user action or timeout.
     *
     * @param code For a dialog with developer-defined buttons, the
     *      0-based index of the button used to close the dialog.
     *      For a simple dialog, 0 if the dialog was closed with the
     *      OK button, or -1 if the dialog timed out.
     */
    public void onDialogClosed(final int code) { }

    /**
     * Called when an error occurred while starting camera recording.
     * Your handler must call
     * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassControlUtils#stopCamera}.
     *
     * @param error The reason for failure of the capture operation, one of these constants:
     *            <ul>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#ERROR_INVALID_PARAMETER}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#ERROR_FILE_ACCESS}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#ERROR_CAPTURE}</li>
     *            </ul>
     */
    public void onCameraErrorReceived(final int error) { }

    /**
     * Called when a new image is received in these camera modes:
     *            <ul>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_MODE_STILL}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_MODE_JPG_STREAM_LOW_RATE}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_MODE_JPG_STREAM_HIGH_RATE}</li>
     *            </ul>
     *
     * @param event The camera event object that contains the new image.
     */
    public void onCameraReceived(final CameraEvent event) { }

    /**
     * Called when a new image is sent to a file, in these camera modes:
     *            <ul>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#CAMERA_MODE_STILL_TO_FILE}</li>
     *            </ul>
     *
     * @param filePath The path and file name of the new JPEG file.
     */
    public void onCameraReceivedFile(final String filePath) { }

    /**
     * Called when the power mode changes.
     *
     * @param powerMode The new power mode value, one of
     *            <ul>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#POWER_MODE_HIGH}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#POWER_MODE_NORMAL}</li>
     *            </ul>
     */
    public void onChangePowerMode(final int powerMode) { }

    /**
     * Called when the display state changes.
     *
     * @param mode The new display state value, one of
     *            <ul>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#DISPLAY_STATUS_OFF}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#DISPLAY_STATUS_ON}</li>
     *            </ul>
     */
    public void onDisplayStatus(final int mode) { }

    /**
     * Called when the device has entered standby mode.
     */
    public boolean onConfirmationEnterStandby() {
        return true;
    }

    /**
     * Called when the standby mode changed.
     *
     * @param status The new standby mode, one of
     *            <ul>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#STANDBY_MODE_OFF}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#STANDBY_MODE_ON}</li>
     *            </ul>
     */
    public void onStandbyStatus(final int status) { }

    /**
     * Called when the battery status is received.
     *
     * @param value The remaining power in the battery, a percentage value.
     */
    public void onBatteryStatus(final int value) { }

    /**
     * Called when the telephony function status is received.
     *
     * @param status The telephony function status, one of:
     *            <ul>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#TELEPHONY_MODE_BT_HEADSET_ENABLE}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#TELEPHONY_MODE_BT_HEADSET_DISABLE}</li>
     *            </ul>
     */
    public void onTelephonyFunctionStatus(final int status) { }

    /**
     * Called when the registration operation for an AR rendering object has completed.
     *
     * @param result    The result of the operation, 0 on success.
     * @param objectId  The unique ID of the registered AR object.
     */
    public void onARRegistrationResult(int result, int objectId) {}

    /**
     * Called when the animation-enable operation for AR rendering has completed.
     *
     * @param result    The result of the operation, 0 on success.
     */
    public void onAREnableAnimationResponse(int result) {}

    /**
     * Called when the animation-disable operation for AR rendering has completed.
     *
     * @param result    The result of the operation, 0 on success.
     */
    public void onARDisableAnimationResponse(int result) {}

    /**
     * Called when the AR rendering engine requires display data for an AR object.
     * You handler should send the requested image data using
     * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassControlUtils#sendARObjectResponse}
     * or {@link com.sony.smarteyeglass.extension.util.SmartEyeglassControlUtils#sendARAnimationObject}.
     *
     * @param objectId  The object ID whose data is requested.
     */
    public void onARObjectRequest(int objectId) {}

     /**
     * Called when the draw operation for
     * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassControlUtils#showBitmapWithCallback} is completed.
     *
     * @param transactionNumber The transaction number that was passed to the showBitmapWithCallback call which has completed.
     * @param result The processing result.
     *            <ul>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#DISPLAY_DATA_RESULT_OK}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#DISPLAY_DATA_RESULT_CANNOT_DRAW}</li>
     *            </ul>
     */
    public void onResultShowBitmap(int transactionNumber, int result) {}

     /**
     * Called when the draw operation for
     * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassControlUtils#showImageWithCallback} is completed.
     *
     * @param transactionNumber The transaction number that was passed to the showImageWithCallback call which has completed.
     * @param result The processing result.
     *            <ul>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#DISPLAY_DATA_RESULT_OK}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#DISPLAY_DATA_RESULT_CANNOT_DRAW}</li>
     *            </ul>
     */
    public void onResultShowImage(int transactionNumber, int result) {}

     /**
     * Called when the draw operation for
     * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassControlUtils#sendARAnimationObjectWithCallback} is completed.
     *
     * @param transactionNumber The transaction number that was passed to the sendARAnimationObjectWithCallback call which has completed.
     * @param result The processing result.
     *            <ul>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#DISPLAY_DATA_RESULT_OK}</li>
     *            <li> {@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#DISPLAY_DATA_RESULT_CANNOT_DRAW}</li>
     *            </ul>
     */
    public void onResultSendAnimationObject(int transactionNumber, int result) {}
}
