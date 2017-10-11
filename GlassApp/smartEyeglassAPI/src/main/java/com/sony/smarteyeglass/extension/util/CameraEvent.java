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
 * The camera event class passes data captured by the camera.
 */
public class CameraEvent {
    private final int mIndex;

    private final int mFrameId;

    private final long mTimestamp;

    private final byte[] mData;

    private final int mErrorStatus;

    /**
     * Creates an event object for camera operation.
     * @param index     identifies the type of data being passed, 0 for picture.
     * @param frameId   value used to identify the order of the captured data.
     * @param timeStamp milliseconds since the epoch (1970-01-01).
     * @param mData     picture data.
     */
    public CameraEvent(final int index, final int frameId, final long timeStamp, final byte[] mData) {
        this.mIndex = index;
        this.mFrameId = frameId;
        this.mTimestamp = timeStamp;
        this.mData = mData;
        this.mErrorStatus = 0;
    }

    /**
     * Creates an event object for error case.
     * @param index     identifies the type of data being passed, 0 for picture.
     * @param frameId   value used to identify the order of the captured data.
     * @param timeStamp milliseconds since the epoch (1970-01-01).
     * @param status    error status. 0 : no error. !0 : error occurred..
     */
    public CameraEvent(final int index, final int frameId, final long timeStamp, final int status) {
        this.mIndex = index;
        this.mFrameId = frameId;
        this.mTimestamp = timeStamp;
        this.mData = null;
        this.mErrorStatus = status;
    }

    /**
     * Identifies the type of data being returned.
     * @return 0 for picture.
     */
    public int getIndex() {
        return mIndex;
    }

    /**
     * Retrieves the frame ID of the captured data.
     * @return The frame ID.
     */
    public int getFrameId() {
        return mFrameId;
    }

    /**
     * Retrieves the timestamp of the captured data.
     *
     * @return The timestamp, milliseconds since the epoch (1970-01-01).
     */
    public long getTimestamp() {
        return mTimestamp;
    }

    /**
     * Retrieves the picture data.
     *
     * @return The picture data, or NULL on error.
     */
    public byte[] getData() {
        return mData;
    }

    /**
     * Retrieves error information.
     *
     * @return The error code, 0 on success
     */
    public int getErrorStatus() {
        return mErrorStatus;
    }

    @Override
    public String toString() {
        if (mData != null) {
            return String.format("CameraEvent: Index:%d, frame:%d, timestamp:%d, datalength:%d",
                    mIndex, mFrameId, mTimestamp, mData.length);
        }
        return String.format("CameraEvent: Index:%d, frame:%d, timestamp:%d, data is null",
                mIndex, mFrameId, mTimestamp);
    }
}
