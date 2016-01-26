package com.example.rottentomatoes.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class FadeInNetworkImageView extends NetworkImageView {

    private static final int FADE_IN_TIME_MS = 250;
    private boolean mFadeIn;

    public FadeInNetworkImageView(Context context) {
        super(context);
    }

    public FadeInNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FadeInNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageUrl(String url, ImageLoader imageLoader) {
        // The maxWidth and maxHeight calculations are from NetworkImageView
        int width = getWidth();
        int height = getHeight();

        boolean wrapWidth = false, wrapHeight = false;
        if (getLayoutParams() != null) {
            wrapWidth = getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT;
            wrapHeight = getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        // Calculate the max image width / height to use while ignoring WRAP_CONTENT dimens.
        int maxWidth = wrapWidth ? 0 : width;
        int maxHeight = wrapHeight ? 0 : height;

        mFadeIn = !imageLoader.isCached(url, maxWidth, maxHeight);
        super.setImageUrl(url, imageLoader);
    }

    @Override
    public void setImageBitmap(Bitmap bmp) {
        super.setImageBitmap(bmp);

        if (mFadeIn) {
            fadeIn();
            mFadeIn = false;
        }
    }

    private void fadeIn() {
        ViewCompat.setAlpha(this, 0f);
        ViewCompat.animate(this).alpha(1.0f).setDuration(FADE_IN_TIME_MS);
    }
}