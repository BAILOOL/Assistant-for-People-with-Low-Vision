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
import android.graphics.PointF;

/**
 * Defines an object to be rendered by the AR engine using the
 * cylindrical coordinate system. Positions in this system are
 * placed on an imaginary cylinder surrounding the user, and may or
 * may not be currently visible in the glass display.
 */
public final class CylindricalRenderObject extends RenderObject {

    /** The horizontal position. */
    private float h;

    /** The vertical position. */
    private float v;

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
     * @param h The initial horizontal position in the cylindrical coordinate system.
     *      An absolute compass value in degrees, where 0.0 is north, 180.0 is south, and so on.
     * @param v The initial vertical position. An angle in radians, where positive values are up
     *      and negative values are down.
     */
    public CylindricalRenderObject(final int objectId, final Bitmap bitmap,
            final int order, final int objectType,
            final float h, final float v) {
        super(objectId, bitmap, order, objectType);
        this.h = h;
        this.v = v;
    }

    /**
     * Sets the position. After changing the position associated with
     * an object, execute the change by calling
     * {@link com.sony.smarteyeglass.extension.util.SmartEyeglassControlUtils#moveARObject(RenderObject)}.
     *
     * @param point The new position in the cylindrical coordinate system.
     */
    public void setPosition(final PointF point) {
        this.h = point.x;
        this.v = point.y;
    }

    /**
     * Retrieves the position.
     *
     * @return The object position in the cylindrical coordinate system.
     */
    public PointF getPosition() {
        return new PointF(this.h, this.v);
    }

    @Override
    public void toRegisterExtras(final Intent intent) {
        super.toRegisterExtras(intent);
        setPositionExtras(intent);
    }

    @Override
    public void toMoveExtras(final Intent intent) {
        setObjectIdExtra(intent);
        setPositionExtras(intent);
    }

    /**
     * Set the intent to object position.
     * 
     * @param intent Intent to set the extra.
     */
    private void setPositionExtras(final Intent intent) {
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_AR_COORDINATE_TYPE,
                SmartEyeglassControl.Intents.AR_COORDINATE_TYPE_CYLINDRICAL);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_AR_CYLINDRICAL_POS_H,
                this.h);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_AR_CYLINDRICAL_POS_V,
                this.v);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(super.toString())
            .append(", coordinate = ")
            .append("CYLINDRICAL, H = ")
            .append(h)
            .append(", V = ")
            .append(v);

        return b.toString();
    }
}
