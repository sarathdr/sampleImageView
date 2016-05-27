package com.sarath.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * ImageView sub class which helps to align and scale the drawable based
 * on the alignment specified.
 *
 * @author Sarath on 26/05/16.
 */
public class AlignedImageView extends ImageView {

    private static final String TAG = "AlignedImageView";

    /**
     * Decides the alignment of the drawable
     **/
    public enum AlignDrawable {

        /**
         * Aligns center vertically and fits the left end
         **/
        CENTER_LEFT(0f, 0.5f),

        /**
         * Aligns center vertically and fits the right end
         **/
        CENTER_RIGHT(1f, 0.5f),

        /**
         * Aligns center vertically and horizontally
         **/
        CENTER_CENTER(0.5f, 0.5f),

        /**
         * Fits bottom left end of the image and crops the rest
         **/
        BOTTOM_LEFT(0f, 1f),

        /**
         * Fits bottom right end of the image and crops the rest
         **/
        BOTTOM_RIGHT(1f, 1f),

        /**
         * Fits bottom end and align horizontally
         **/
        BOTTOM_CENTER(0.5f, 1f),

        /**
         * Fits top left end and crops the rest
         **/
        TOP_LEFT(0f, 0f),

        /**
         * Fits top right end and crops the rest
         **/
        TOP_RIGHT(1f, 0f),

        /**
         * Fits top end and  align horizontally
         **/
        TOP_CENTER(0.5f, 0f);

        /**
         * Holds horizontal offset factor
         **/
        private final float mHorizontalOffset;

        /**
         * Holds vertical offset factor
         **/
        private final float mVerticalOffset;

        AlignDrawable(float horizontalOffset, float verticalOffset) {
            mHorizontalOffset = horizontalOffset;
            mVerticalOffset = verticalOffset;
        }

        public float getHorizontalOffset() {
            return mHorizontalOffset;
        }

        public float getVerticalOffset() {
            return mVerticalOffset;
        }
    }


    private AlignDrawable mAlignDrawable;

    private static final AlignDrawable[] sAlignDrawable = {
            AlignDrawable.CENTER_LEFT,
            AlignDrawable.CENTER_RIGHT,
            AlignDrawable.CENTER_CENTER,
            AlignDrawable.BOTTOM_LEFT,
            AlignDrawable.BOTTOM_RIGHT,
            AlignDrawable.BOTTOM_CENTER,
            AlignDrawable.TOP_LEFT,
            AlignDrawable.TOP_RIGHT,
            AlignDrawable.TOP_CENTER,
    };


    public AlignedImageView(final Context context) {
        this(context, null);
    }

    public AlignedImageView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlignedImageView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (attrs != null) {
            final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AlignedImageView, defStyle, 0);
            final int index = ta.getInt(R.styleable.AlignedImageView_alignDrawable, -1);
            if (index >= 0) {
                mAlignDrawable = sAlignDrawable[index];
            }
        }

        // Set default as TOP_LEFT
        if (mAlignDrawable == null) {
            mAlignDrawable = AlignDrawable.TOP_LEFT;
        }

        // Make sure that the scale type is always matrix
        setScaleType(ScaleType.MATRIX);
    }

    /**
     * Sets the image matrix with proper scaling
     *
     * @param width  the width of the imageView
     * @param height the height of the imageView
     */
    private void setupScaleMatrix(int width, int height) {

        // Make sure that the view frame has been drawn
        if (width == 0 || height == 0) {
            return;
        }


        final Drawable drawable = getDrawable();

        // Do not do anything if the drawable is not set
        if (drawable == null) {
            return;
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Drawable loaded. Changing matrix");
        }

        final int intrinsicWidth = drawable.getIntrinsicWidth();
        final int intrinsicHeight = drawable.getIntrinsicHeight();

        // To make sure that its image drawable
        // with proper width and height.
        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            return;
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Frame Width: " + width);
            Log.d(TAG, "Frame Height: " + height);


            Log.d(TAG, "Drawable Width: " + intrinsicWidth);
            Log.d(TAG, "Drawable Height: " + intrinsicHeight);
        }

        float factorWidth = width / (float) intrinsicWidth;
        float factorHeight = height / (float) intrinsicHeight;
        float factor = Math.max(factorHeight, factorWidth);

        float dx = 0, dy = 0;

        if (factorHeight > factorWidth) {
            dx = width - intrinsicWidth * factor;
        } else {
            dy = height - intrinsicHeight * factor;
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Difference in Width: " + dx);
            Log.d(TAG, "Difference in Height: " + dy);
        }

        // We have to use our own matrix here to make
        // ImageView.setImageMatrix(Matrix matrix) to call
        // configureBounds(); invalidate();
        final Matrix matrix = new Matrix();

        // Scale the image and apply translation
        matrix.postScale(factor, factor, 0, 0);

        dx = Math.round(dx * mAlignDrawable.getHorizontalOffset());
        dy = Math.round(dy * mAlignDrawable.getVerticalOffset());

        matrix.postTranslate(dx, dy);
        setImageMatrix(matrix);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        // We have to recalculate image if the drawable changed from code
        if (mAlignDrawable != null) {
            setupScaleMatrix(getWidth(), getHeight());
        }

    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        if (changed) {
            // Do not call this method if nothing changed
            setupScaleMatrix(r - l, b - t);
        }

        return changed;
    }

}
