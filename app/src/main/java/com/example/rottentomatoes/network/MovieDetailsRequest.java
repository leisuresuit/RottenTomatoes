package com.example.rottentomatoes.network;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.example.rottentomatoes.model.Movie;

/**
 * Created by larwang on 12/8/15.
 */
public class MovieDetailsRequest extends GsonRequest<Movie> {
    public MovieDetailsRequest(String url, Listener<Movie> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url + "?apikey=9txsnh3qkb5ufnphhqv5tv5z",
                Movie.class, null, null, listener, errorListener);
    }
}
