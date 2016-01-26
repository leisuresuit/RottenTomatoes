package com.example.rottentomatoes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.rottentomatoes.model.Movie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by larwang on 10/23/15.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    public interface MovieItemListener {
        void onImageClick(Movie movie);
        void onInfoClick(View view, Movie movie);
    }

    private List<Movie> mMovies = new ArrayList<>();
    private MovieItemListener mListener;

    public void setListener(MovieItemListener listener) {
        mListener = listener;
    }

    public List<Movie> getMovies() {
        return mMovies;
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);
        MovieViewHolder holder = new MovieViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        final Movie movie = mMovies.get(position);
        holder.image.setImageUrl(movie.posters.thumbnail, AppHandles.getInstance().getImageLoader());
        holder.imageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onImageClick(movie);
                }
            }
        });
        holder.title.setText(movie.title);
        holder.info.setText(movie.getInfo(holder.itemView.getContext()));
        holder.infoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onInfoClick(holder.itemView, movie);
                }
            }
        });
    }

    public void clear() {
        mMovies = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addMovies(Movie[] movies) {
        if (movies != null) {
            mMovies.addAll(Arrays.asList(movies));
            notifyItemRangeInserted(mMovies.size() - movies.length, movies.length);
        }
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_container) View imageContainer;
        @Bind(R.id.info_container) View infoContainer;
        @Bind(R.id.image) NetworkImageView image;
        @Bind(R.id.title) TextView title;
        @Bind(R.id.info) TextView info;

        MovieViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }

}
