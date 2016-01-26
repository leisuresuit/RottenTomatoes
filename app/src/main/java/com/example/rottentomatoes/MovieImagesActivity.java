package com.example.rottentomatoes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.rottentomatoes.util.ViewUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tracy on 1/19/16.
 */
public class MovieImagesActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE_URLS = "image_urls";

    @Bind(R.id.pager) ViewPager mPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_images);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String[] urls = intent.getStringArrayExtra(EXTRA_IMAGE_URLS);

        MovieImageAdapter adapter = new MovieImageAdapter(this, urls, AppHandles.getInstance().getImageLoader());
        mPager.setAdapter(adapter);
    }

    static class MovieImageAdapter extends PagerAdapter {
        private final LayoutInflater mLayoutInflater;
        private final String[] mUrls;
        private final ImageLoader mImageLoader;

        public MovieImageAdapter(Context context, String[] urls, ImageLoader imageLoader) {
            mLayoutInflater = LayoutInflater.from(context);
            mUrls = urls;
            mImageLoader = imageLoader;
        }

        @Override
        public int getCount() {
            return (mUrls != null) ? mUrls.length : 0;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = mLayoutInflater.inflate(R.layout.movie_image, container, false);
            NetworkImageView iv = ViewUtil.findViewById(v, R.id.image);
            iv.setImageUrl(mUrls[position], mImageLoader);
            container.addView(v);
            return v;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
