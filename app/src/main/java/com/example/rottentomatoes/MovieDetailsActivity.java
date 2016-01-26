package com.example.rottentomatoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.example.rottentomatoes.model.Movie;
import com.example.rottentomatoes.network.MovieDetailsRequest;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tracy on 1/19/16.
 */
public class MovieDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_URL = "url";
    public static final String EXTRA_IMAGE_URL = "image_url";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_INFO = "info";

    @Bind(R.id.image) NetworkImageView mImage;
    @Bind(R.id.title) TextView mTitle;
    @Bind(R.id.info) TextView mInfo;
    @Bind(R.id.synopsis) TextView mSynopsis;
    @Bind(R.id.critics) TextView mCritics;
    @Bind(R.id.studio) TextView mStudio;
    @Bind(R.id.loading) ContentLoadingProgressBar mLoading;

    private Request mRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mImage.setImageUrl(intent.getStringExtra(EXTRA_IMAGE_URL), AppHandles.getInstance().getImageLoader());
        mTitle.setText(intent.getStringExtra(EXTRA_TITLE));
        mInfo.setText(intent.getStringExtra(EXTRA_INFO));
        retrieveDetails(intent.getStringExtra(EXTRA_URL));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mRequest != null) {
            mRequest.cancel();
        }
    }

    private void retrieveDetails(String url) {
        if (mRequest != null) {
            mRequest.cancel();
        }

        mRequest = new MovieDetailsRequest(
                url,
                new Response.Listener<Movie>() {
                    @Override
                    public void onResponse(Movie movie) {
                        mRequest = null;
                        mLoading.hide();

                        setText(mSynopsis, movie.synopsis);
                        setText(mCritics, movie.critics_consensus);
                        setText(mStudio, movie.studio);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mRequest = null;
                        mLoading.hide();

                        String msg = error.getLocalizedMessage();
                        if (TextUtils.isEmpty(msg)) {
                            if (error instanceof TimeoutError) {
                                msg = getString(R.string.error_timeout);
                            } else {
                                msg = getString(R.string.error_network);
                            }
                        }
                        new AlertDialog.Builder(MovieDetailsActivity.this)
                                .setMessage(msg)
                                .setPositiveButton(android.R.string.ok, null)
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    }
                }
        );

        AppHandles.getInstance().getRequestQueue().add(mRequest);
    }

    private void setText(TextView tv, CharSequence text) {
        tv.setText(text);
        tv.setVisibility(!TextUtils.isEmpty(text) ? View.VISIBLE : View.GONE);
    }
}
