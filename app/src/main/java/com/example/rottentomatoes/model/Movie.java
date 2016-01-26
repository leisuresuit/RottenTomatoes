package com.example.rottentomatoes.model;

import android.content.Context;

import com.example.rottentomatoes.R;

import java.io.Serializable;

/**
 * Created by tracy on 1/19/16.
 */
public class Movie implements Serializable {
    public String id;
    public String title;
    public String year;
    public String[] genres;
    public String mpaa_rating;
    public String runtime;
    public String critics_consensus;
    public ReleaseDates release_dates;
    public Ratings ratings;
    public String synopsis;
    public Posters posters;
    public Cast[] abridged_cast;
    public Director[] abridged_directors;
    public String studio;
    public MovieLinks links;

    public String getInfo(Context context) {
        return context.getString(R.string.info_format, mpaa_rating, runtime);
    }
}
