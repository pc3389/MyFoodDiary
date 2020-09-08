package bo.young.myfoodrecords.adapter;

import android.content.Context;
import android.example.myfoodrecords.R;

import bo.young.myfoodrecords.model.Movie;
import bo.young.myfoodrecords.utils.RetrofitHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private Context context;

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView posterImageView;
        private TextView titleTextView;
        private TextView releaseDateTextView;
        private TextView ratingTextView;
        private TextView overviewTextView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.movie_poster_iv);
            titleTextView = itemView.findViewById(R.id.movie_title_tv);
            releaseDateTextView = itemView.findViewById(R.id.movie_date_tv);
            ratingTextView = itemView.findViewById(R.id.movie_rating_tv);
            overviewTextView = itemView.findViewById(R.id.movie_overview_tv);

        }
    }

    public MovieAdapter(List<Movie> movieList, Context context) {
        this.movieList = movieList;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.titleTextView.setText(movie.getMovieTitle());
        String posterPathUrl = RetrofitHelper.getPosterPathUrl(movie.getPosterPath());
        Glide.with(context).load(posterPathUrl).into(holder.posterImageView);
        holder.releaseDateTextView.setText(movie.getReleaseDate());
        holder.ratingTextView.setText(String.valueOf(movie.getRating()));
        holder.overviewTextView.setText(movie.getOverview());
    }

    @Override
    public int getItemCount() {
        if (movieList == null) {
            return 0;
        } else {
            return movieList.size();
        }
    }

    /**
     * @param movieList Receives the movie data after fetching and update the UI
     */
    public void setMovieList(List<Movie> movieList) {

        this.movieList = movieList;
        notifyDataSetChanged();
    }

}
