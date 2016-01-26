package com.example.rottentomatoes.model;

import java.io.Serializable;

/**
 * Created by larwang on 12/8/15.
 */
public class MoviesSearchResult implements Serializable {
    public int total;
    public Movie[] movies;
    public SearchResultLinks links;
}
