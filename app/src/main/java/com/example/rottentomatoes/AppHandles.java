package com.example.rottentomatoes;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.IOException;

/**
 * Created by larwang on 7/13/15.
 */
public class AppHandles {

    private static AppHandles sInstance;

    public static AppHandles initInstance(Activity activity) {
        if (sInstance == null) {
            sInstance = new AppHandles(activity);
        }
        return sInstance;
    }

    public static AppHandles getInstance() {
        return sInstance;
    }

    private final DiskLruCache mDiskLruCache;
    private final RequestQueue mRequestQueue;
    private final ImageLoader mImageLoader;

    private AppHandles(Activity activity) {
        DiskLruCache diskLruCache = null;
        try {
            diskLruCache = DiskLruCache.open(activity.getFilesDir(), 1, 1, 128 * 1024 * 1024); // 32MB cap
        } catch (IOException e) {
        }
        mDiskLruCache = diskLruCache;

        Cache cache = new DiskBasedCache(activity.getFilesDir(), 32 * 1024 * 1024); // 32MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>(50);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public DiskLruCache getDiskLruCache() {
        return mDiskLruCache;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

}
