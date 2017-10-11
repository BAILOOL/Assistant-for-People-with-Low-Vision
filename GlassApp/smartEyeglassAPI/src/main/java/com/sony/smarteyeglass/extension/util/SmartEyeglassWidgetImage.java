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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

import com.sony.smarteyeglass.sdk.R;


/**
 * Defines a widget bitmap that conforms to SmartEyeglass standards for
 * the top-level menu. Use to define a "card", the screen in the top menu
 * that starts your app.
 */
public class SmartEyeglassWidgetImage {

    private final Bitmap mBitmap;

    private Bitmap mIconBitmap;

    private final Canvas mCanvas;

    private String mText;

    private int mInnerLayoutResid;

    protected final Context mContext;

    protected final BitmapFactory.Options mBitmapOptions;

    protected final int mInnerWidth;

    protected final int mInnerHeight;

    /**
     * Initializes the SmartEyeglass widget image with
     * a frame size suitable for the SmartEyeglass display.
     *
     * @param context The context.
     */
    public SmartEyeglassWidgetImage(final Context context) {
        mContext = context;

        mText = null;
        mIconBitmap = null;
        mInnerLayoutResid = 0;

        mInnerWidth = mContext.getResources().getDimensionPixelSize(
                R.dimen.smarteyeglass_widget_width);
        mInnerHeight = mContext.getResources().getDimensionPixelSize(
                R.dimen.smarteyeglass_widget_height);

        mBitmap = Bitmap.createBitmap(mInnerWidth, mInnerHeight, Bitmap.Config.ARGB_8888);

        // Set the density to default to avoid scaling.
        mBitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        mCanvas = new Canvas(mBitmap);

        // Options to avoid scaling.
        mBitmapOptions = new BitmapFactory.Options();
        mBitmapOptions.inDensity = DisplayMetrics.DENSITY_DEFAULT;
        mBitmapOptions.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
        mBitmapOptions.inScaled = false;
    }

    /**
     * Retrieves the widget frame height for the SmartEyeglass.
     *
     * @param context The context.
     * @return The height in pixels.
     */
    public static int getSupportedWidgetHeight(final Context context) {
        return context.getResources()
                .getDimensionPixelSize(R.dimen.smarteyeglass_widget_height);
    }

    /**
     * Retrieves the widget frame width for the SmartEyeglass.
     *
     * @param context The context.
     * @return The width in pixels.
     */
    public static int getSupportedWidgetWidth(final Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smarteyeglass_widget_width);
    }

    /**
     * Sets a custom text.
     * Typically used for a card that shows only text, where no specific layout is needed.
     *
     * @param text The display text.
     * @return this.
     */
    public SmartEyeglassWidgetImage setText(final String text) {
        mText = text;
        return this;
    }

    /**
     * Sets the widget icon using a resource ID.
     *
     * @param iconId The resource ID of the icon to display.
     * @return this.
     */
    public SmartEyeglassWidgetImage setIconByResourceId(final int iconId) {
        mIconBitmap = BitmapFactory.decodeResource(mContext.getResources(), iconId, mBitmapOptions);
        return this;
    }

    /**
     * Sets the widget icon using a URI.
     *
     * @param iconUri The URI of the icon.
     * @return this.
     */
    public SmartEyeglassWidgetImage setIconByUri(final String iconUri) {
        if (iconUri == null) {
            return this;
        }

        Uri uri = Uri.parse(iconUri);
        if (uri != null) {
            try {
                mIconBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                // We use default density for all bitmaps to avoid scaling.
                mIconBitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
            } catch (IOException e) {

            }
        }
        return this;
    }

    /**
     * Creates an instance of this class that displays a custom layout
     * inside the widget frame.
     *
     * @param layoutId The resource ID of the layout.
     * @return this.
     */
    public SmartEyeglassWidgetImage setInnerLayoutResourceId(final int layoutId) {
        mInnerLayoutResid = layoutId;
        return this;
    }

    /**
     * Sets a custom text. Typically used when only text should be displayed
     * and no specific layout is needed.
     */
    private void draw() {
        LinearLayout root = new LinearLayout(mContext);
        root.setLayoutParams(new LayoutParams(mInnerWidth, mInnerHeight));

        LinearLayout linearLayout = (LinearLayout) LinearLayout.inflate(mContext,
                R.layout.smarteyeglass_widgetcard, root);

        LinearLayout innerFrame = (LinearLayout)linearLayout.findViewById(R.id.smarteyeglass_widget_inner);

        ImageView icon = (ImageView)innerFrame.findViewById(R.id.smarteyeglass_widget_icon);
        icon.setImageBitmap(mIconBitmap);

        if (null != mText) {
            TextView textView = (TextView) innerFrame
                    .findViewById(R.id.smarteyeglass_widget_text);
            textView.setText(mText);
        }

        ImageView customImage = (ImageView) linearLayout
                .findViewById(R.id.smarteyeglass_widget_inner_image);
        customImage.setImageBitmap(getInnerBitmap());

        linearLayout.measure(mInnerWidth, mInnerHeight);
        linearLayout
                .layout(0, 0, linearLayout.getMeasuredWidth(), linearLayout.getMeasuredHeight());

        linearLayout.draw(mCanvas);
    }

    /**
     * Gets the bitmap inside the frame.
     * @return A bitmap or null if no inner layout is applied.
     */
    private Bitmap getInnerBitmap() {
        if (mInnerLayoutResid != 0) {
            Bitmap innerBitmap = Bitmap.createBitmap(mInnerWidth, mInnerHeight,
                    Bitmap.Config.ARGB_8888);

            // Set the density to default to avoid scaling.
            innerBitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);

            LinearLayout root = new LinearLayout(mContext);
            root.setLayoutParams(new LayoutParams(mInnerWidth, mInnerHeight));

            LinearLayout innerLayout = (LinearLayout)LinearLayout.inflate(mContext,
                    mInnerLayoutResid, root);

            applyInnerLayout(innerLayout);

            innerLayout.measure(mInnerWidth, mInnerHeight);
            innerLayout.layout(0, 0, innerLayout.getMeasuredWidth(),
                    innerLayout.getMeasuredHeight());

            Canvas innerCanvas = new Canvas(innerBitmap);
            innerLayout.draw(innerCanvas);

            return innerBitmap;
        } else {
            return null;
        }
    }

    /**
     * Retrieves the bitmap inside the widget frame.
     * Example:
     * ((TextView)innerLayout.findViewById(R.id.my_custom_widget_city)).setText("Paris");
     */
    protected void applyInnerLayout(LinearLayout innerLayout) {
        throw new IllegalArgumentException(
                "applyInnerLayout() not implemented. Child class must override this method since innerLayoutResid != 0");
    }

    /**
     * Retrieves the bitmap for this widget.
     *
     * @return The bitmap object.
     */
    public Bitmap getBitmap() {
        draw();
        return mBitmap;
    }

}
