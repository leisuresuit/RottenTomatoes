package com.example.rottentomatoes.network;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.example.rottentomatoes.model.MoviesSearchResult;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by larwang on 12/8/15.
 */
public class MoviesSearchRequest extends GsonRequest<MoviesSearchResult> {
    public MoviesSearchRequest(String query, int limit, int pageNumber, Listener<MoviesSearchResult> listener, Response.ErrorListener errorListener) {
        super(Method.GET, String.format("http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=9txsnh3qkb5ufnphhqv5tv5z&q=%s&page_limit=%d&page=%d", encode(query), limit, pageNumber),
                MoviesSearchResult.class, null, null, listener, errorListener);
    }

    private static String encode(String query) {
        try {
            return URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return query;
    }
}
