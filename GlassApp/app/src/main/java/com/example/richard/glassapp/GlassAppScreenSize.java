package com.example.richard.glassapp;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by richard on 17. 3. 7.
 */

public final class GlassAppScreenSize {
    /** The screen width. */
    private final int width;

    /** The screen height. */
    private final int height;

    /** An application context. */
    private final Context context;

    /**
     * Creates a new instance.
     *
     * @param context An application context.
     */
    public GlassAppScreenSize(final Context context) {
        this.context = context;
        Resources res = context.getResources();
        width = res.getDimensionPixelSize(R.dimen.smarteyeglass_control_width);
        height = res.getDimensionPixelSize(
                R.dimen.smarteyeglass_control_height);
    }

    /**
     * Returns an application context.
     *
     * @return An application context.
     */
    public Context getContext() {
        return context;
    }

    /**
     * Returns the screen width.
     *
     * @return The screen width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the screen height.
     *
     * @return The screen height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Compares this size to the specified size.
     *
     * @param width
     *            The width of size.
     * @param height
     *            The height of size.
     * @return {@code true} if the specified size is equal to this,
     *         {@code false} otherwise.
     */
    public boolean equals(final int width, final int height) {
        return this.width == width && this.height == height;
    }
}
