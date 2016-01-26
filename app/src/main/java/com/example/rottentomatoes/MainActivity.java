package com.example.rottentomatoes;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.rottentomatoes.model.Movie;
import com.example.rottentomatoes.model.MoviesSearchResult;
import com.example.rottentomatoes.network.MoviesSearchRequest;
import com.example.rottentomatoes.util.ViewUtil;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener,
        MoviesAdapter.MovieItemListener {

    private static final String QUERY = "query";
    private static final int SEARCH_PAGE_LIMIT = 20;
    private static final int RETRIEVE_THRESHOLD = 10;

    private MoviesAdapter mAdapter;
    private Toolbar mToolbar;
    @Bind(R.id.loading) ContentLoadingProgressBar mLoading;
    @Bind(R.id.recycler_view) RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private String mQuery;
    private int mPageNumber = 1;
    private SearchView mSearchView;
    private Request mQueryRequest;
    private AsyncTask mGetCachedDataTask;

    private final RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (mSearchView != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
            }

            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItem = mLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (mQueryRequest == null) {
                if ((visibleItemCount + firstVisibleItem) >= (totalItemCount - RETRIEVE_THRESHOLD)) {
                    searchMovies(mQuery);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppHandles.initInstance(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initToolbar();

        mLoading.hide();

        mQuery = (savedInstanceState != null) ? savedInstanceState.getString(QUERY) : null;
        mAdapter = new MoviesAdapter();
        mAdapter.setListener(this);

        initRecyclerView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(QUERY, mQuery);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        mSearchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchView.setQuery(mQuery, false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mRecyclerView.removeOnScrollListener(mScrollListener);
        if (mQueryRequest != null) {
            mQueryRequest.cancel();
        }
        if (mGetCachedDataTask != null) {
            mGetCachedDataTask.cancel(true);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // no-op
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mPageNumber = 1;
        mAdapter.clear();
        mLoading.show();
        searchMovies(query);
        return true;
    }

    private void searchMovies(String query) {
        mQuery = query;
        if (TextUtils.isEmpty(mQuery)) {
            return;
        }

        mGetCachedDataTask = new AsyncTask<Void, Void, MoviesSearchResult>() {
            @Override
            protected MoviesSearchResult doInBackground(Void... params) {
                return getCachedResult();
            }
            @Override
            protected void onPostExecute(MoviesSearchResult result) {
                if (result != null) {
                    mLoading.hide();
                    handleSearchResult(result);
                } else {
                    retrieveMovies();
                }
            }
        }.execute();
    }

    private void retrieveMovies() {
        if (mQueryRequest != null) {
            mQueryRequest.cancel();
        }

        mRecyclerView.removeOnScrollListener(mScrollListener);
        mQueryRequest = new MoviesSearchRequest(mQuery, SEARCH_PAGE_LIMIT, mPageNumber,
                new Listener<MoviesSearchResult>() {
                    @Override
                    public void onResponse(MoviesSearchResult result) {
                        mQueryRequest = null;
                        mLoading.hide();

                        setCachedResult(result);
                        handleSearchResult(result);
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mQueryRequest = null;
                        mLoading.hide();

                        String msg = error.getLocalizedMessage();
                        if (TextUtils.isEmpty(msg)) {
                            if (error instanceof TimeoutError) {
                                msg = getString(R.string.error_timeout);
                            } else {
                                msg = getString(R.string.error_network);
                            }
                        }
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage(msg)
                                .setPositiveButton(android.R.string.ok, null)
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    }
                }
        );

        AppHandles.getInstance().getRequestQueue().add(mQueryRequest);
    }

    private void handleSearchResult(MoviesSearchResult result) {
        mAdapter.addMovies(result.movies);
        mPageNumber++;
        if (mAdapter.getItemCount() < result.total) {
            mRecyclerView.addOnScrollListener(mScrollListener);
        }
    }

    private MoviesSearchResult getCachedResult() {
        InputStream is = null;
        try {
            DiskLruCache.Snapshot snapshot = AppHandles.getInstance().getDiskLruCache().get(getKey());
            is = snapshot.getInputStream(0);
            ObjectInputStream ois = new ObjectInputStream(is);
            return (MoviesSearchResult) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    private void setCachedResult(MoviesSearchResult result) {
        OutputStream os = null;
        try {
            DiskLruCache.Editor editor = AppHandles.getInstance().getDiskLruCache().edit(getKey());
            os = editor.newOutputStream(0);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(result);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private String getKey() {
        return String.valueOf(mQuery.hashCode()) + mPageNumber;
    }

    @Override
    public void onImageClick(Movie movie) {
        List<String> urls = new ArrayList<>();
        for (Movie m : mAdapter.getMovies()) {
            if (m.posters.original != null) {
                urls.add(m.posters.original);
            }
        }
        String[] arr = new String[urls.size()];
        urls.toArray(arr);
        Intent intent = new Intent(this, MovieImagesActivity.class)
                .putExtra(MovieImagesActivity.EXTRA_IMAGE_URLS, arr);
        startActivity(intent);
    }

    @Override
    public void onInfoClick(View view, Movie movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class)
                .putExtra(MovieDetailsActivity.EXTRA_URL, movie.links.self)
                .putExtra(MovieDetailsActivity.EXTRA_IMAGE_URL, movie.posters.thumbnail)
                .putExtra(MovieDetailsActivity.EXTRA_TITLE, movie.title)
                .putExtra(MovieDetailsActivity.EXTRA_INFO, movie.getInfo(this));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, getString(R.string.movie_item_transition_event));
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    private void initToolbar() {
        mToolbar = ViewUtil.findViewById(this, R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(mLayoutManager = new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

}
