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

import java.io.ByteArrayOutputStream;

import com.sony.smarteyeglass.SmartEyeglassControl;
import com.sonyericsson.extras.liveware.aef.control.Control;

import android.content.Intent;
import android.graphics.Bitmap;

/**
 * Abstract render object. Every render object to be registered must be
 * a member of
 */
public abstract class RenderObject {
    /**
     * Compression factor for conversion to PNG.
     */
    static final int PNG_QUALITY = 100;

    /**
     * Occlusion order for rendering.
     */
    private int order;

    /**
     * The image to display.
     */
    private Bitmap bitmap;

    /**
     * The object ID.
     */
    private final int objectId;

    /**
     * The object type.
     */
    private final int objectType;

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
     *
     */
    public RenderObject(final int objectId, final Bitmap bitmap,
            final int order, final int objectType) {
        this.objectId = objectId;
        this.bitmap = bitmap;
        this.order = order;
        this.objectType = objectType;
    }

    /**
     * Retrieves the object ID.
     * 
     * @return The object ID.
     */
    public final int getObjectId() {
        return objectId;
    }

    /**
     * Sets the bitmap.
     * 
     * @param bitmap The bitmap object.
     */
    public final void setBitmap(final Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * Retrieves the bitmap.
     * 
     * @return The bitmap object.
     */
    public final Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Sets the occlusion order value.
     * 
     * @param order The new order value, a positive integer.
     */
    public final void setOrder(final int order) {
        this.order = order;
    }

    /**
     * Retrieves the occlusion order value.
     * 
     * @return The order value.
     */
    public final int getOrder() {
        return this.order;
    }

    /**
     * Set the intent to resist information.
     * 
     * @param intent intent to set the extra.
     */
    public void toRegisterExtras(final Intent intent) {
        setObjectIdExtra(intent);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_AR_ORDER,
                this.order);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_AR_OBJECT_TYPE,
                objectType);

        if (bitmap != null) {
            intent.putExtra(SmartEyeglassControl.Intents.EXTRA_IMAGE_WIDTH,
                    bitmap.getWidth());
            intent.putExtra(SmartEyeglassControl.Intents.EXTRA_IMAGE_HEIGHT,
                    bitmap.getHeight());
        }
    }

    /**
     * Set the intent to object response information.
     * 
     * @param intent intent to set the extra.
     */
    public final void toObjectResponseExtras(final Intent intent) {
        setObjectIdExtra(intent);

        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream =
                    new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY,
                    byteArrayOutputStream);

            byte[] imageByteArray = byteArrayOutputStream.toByteArray();
            intent.putExtra(Control.Intents.EXTRA_DATA, imageByteArray);
        }
    }

    /**
     * Set the intent to move object information.
     * 
     * @param intent intent to set the extra.
     */
    public abstract void toMoveExtras(final Intent intent);

    /**
     * Set the intent to change order information.
     * 
     * @param intent intent to set the extra.
     */
    public final void toOrderExtras(final Intent intent) {
        setObjectIdExtra(intent);
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_AR_ORDER, order);
    }

    /**
     * Set the intent to object ID.
     * 
     * @param intent intent intent to set the extra.
     */
    protected final void setObjectIdExtra(final Intent intent) {
        intent.putExtra(SmartEyeglassControl.Intents.EXTRA_AR_OBJECT_ID,
                this.objectId);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (bitmap != null) {
            b.append("objectId = ")
                .append(getObjectId())
                .append(", W = ")
                .append(bitmap.getWidth())
                .append(", H = ")
                .append(bitmap.getHeight())
                .append(", order = ")
                .append(order);
        }
        return b.toString();
    }
}
