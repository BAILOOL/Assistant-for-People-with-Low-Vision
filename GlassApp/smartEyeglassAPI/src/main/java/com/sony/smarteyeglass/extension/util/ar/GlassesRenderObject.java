/*
Copyright (c) 2011, Sony Mobile Communications Inc.
Copyright (c) 2014, Sony Corporation

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Mobile Communications Inc.
 nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

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

package com.sony.smarteyeglass.extension.util.ar;

import com.sony.smarteyeglass.SmartEyeglassControl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Defines an object to be rendered by the AR engine using the
 * glass coordinate system. The origin is the upper left corner of
 * the display area, and coordinate values are in pixels.
 */
public final class GlassesRenderObject extends RenderObject {

    /** The horizontal position. */
    private int x;

    /** The vertical position. */
    private int y;

    /**
     * Creates a new instance of this class.
     *
     * @param objectId A unique ID for the render object, a positive integer.
     *              Give the object a unique ID that will allow you
     *              to identify it in the results of asynchronous operations.
     * @param bitmap The bitmap image to be displayed with AR rendering.
     * @param order The occlusion order for this object, a positive integer.
     *              Zero renders the object in the foreground.
     * @param objectType Whether this object is static or animated. One of:
     *  <ul><li>{@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#AR_OBJECT_TYPE_STATIC_IMAGE}</li>
     *      <li>{@link com.sony.smarteyeglass.SmartEyeglassControl.Intents#AR_OBJECT_TYPE_ANIMATED_IMAGE}</li></ul>
     * @param x The initial horizontal position in the glass coordinate system.
     * @param y The initial vertical position in the glass coordinate system.
     */
    public GlassesRenderObject(final int objectId,
            final Bitmap bitmap, final int order,
            final int x, final int y, final int objectType) {
        super(objectId, bitmap, order, objectType);
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the position. After changing the position associated with
     * an object, execute the change by calling
     * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassControlUtils#moveARObject(RenderObject)}.
     *
     * @param point The new position in the glass coordinate system.
     */
    public void setPositon(final Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    /**
     * Retrieves the position.
     *
     * @return The object position in the glass coordinate system.
     */
    public Point getPosition() {
        return new Point(this.x, this.y);
    }

    @Override
    public void toRegisterExtras(final Intent intent) {
        super.toRegisterExtras(intent);
        setPositionExtra(intent);
    }

    @Override
    public void toMoveExtras(final Intent intent) {
        setObjectIdExtra(intent);
        setPositionExtra(intent);
    }

    /**
     * Set the intent to object position.
     * 
     * @param intent Intent to set the extra.
     */
    private void setPositionExtra(final Intent intent) {
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_AR_COORDINATE_TYPE,
                SmartEyeglassControl.Intents.AR_COORDINATE_TYPE_GLASSES);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_AR_POS_X, this.x);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_AR_POS_Y, this.y);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(super.toString())
            .append(", coordinate = ")
            .append("GLASSES, X = ")
            .append(x)
            .append(", Y = ")
            .append(y);

        return b.toString();
    }
}
