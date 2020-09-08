package bo.young.myfoodrecords.fragments;

import android.example.myfoodrecords.R;

import bo.young.myfoodrecords.model.Movie;
import bo.young.myfoodrecords.model.MovieArray;
import bo.young.myfoodrecords.adapter.MovieAdapter;
import bo.young.myfoodrecords.utils.GetInterface;
import bo.young.myfoodrecords.utils.RetrofitHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieFragment extends Fragment {

    private View rootView;

    private MovieAdapter movieAdapter;

    private List<Movie> movieList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        setupUi();
        getMovieData();

        return rootView;
    }

    /**
     * Fetch movie data from TMDB website and update UI
     */
    private void getMovieData() {
        String url = RetrofitHelper.getMovieDataUrl();
        GetInterface getData = RetrofitHelper.getRetrofitInstance(url).create(GetInterface.class);
        Call<MovieArray> call = getData.listMovie(getString(R.string.tmdb_api_key), "en-US", 1);
        call.enqueue(new Callback<MovieArray>() {
            @Override
            public void onResponse(Call<MovieArray> call, Response<MovieArray> response) {
                MovieArray movieArray = response.body();
                movieList = movieArray.getResults();
                Log.d("TAG","Response = "+movieArray);
                movieAdapter.setMovieList(movieList);
            }

            @Override
            public void onFailure(Call<MovieArray> call, Throwable t) {
                Log.d("TAG","Response = "+t.toString());
            }
        });
    }

    private void setupUi() {
        RecyclerView recyclerView = rootView.findViewById(R.id.movie_rc);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        movieAdapter = new MovieAdapter(movieList, getContext());
        recyclerView.setAdapter(movieAdapter);
    }

}
