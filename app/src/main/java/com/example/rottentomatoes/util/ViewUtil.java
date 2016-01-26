package com.example.rottentomatoes.util;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by larwang on 1/8/16.
 */
public class ViewUtil {

    public static <T extends View> T findViewById(Activity activity, @IdRes int id) {
        return (T) activity.findViewById(id);
    }

    public static <T extends View> T findViewById(Fragment fragment, @IdRes int id) {
        return (T) fragment.getView().findViewById(id);
    }

    public static <T extends View> T findViewById(View v, @IdRes int id) {
        return (T) v.findViewById(id);
    }

}
