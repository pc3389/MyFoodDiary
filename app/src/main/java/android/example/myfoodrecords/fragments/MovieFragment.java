package android.example.myfoodrecords.fragments;

import android.example.myfoodrecords.R;
import android.example.myfoodrecords.adapter.MovieAdapter;
import android.example.myfoodrecords.model.Movie;
import android.example.myfoodrecords.model.MovieArray;
import android.example.myfoodrecords.utils.GetInterface;
import android.example.myfoodrecords.utils.RealmHelper;
import android.example.myfoodrecords.utils.RetrofitHelper;
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

import io.realm.Realm;
import io.realm.RealmChangeListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieFragment extends Fragment {

    private Realm realm;
    private RealmHelper helper;
    private RealmChangeListener realmChangeListener;

    private String url;

    private View rootView;

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;

    private List<Movie> movieList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupRealm();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        setupUi();
        getMovieData();

        return rootView;
    }

    private void getMovieData() {
        url = RetrofitHelper.getMovieDataUrl();
        GetInterface getData = RetrofitHelper.getRetrofitInstance(url).create(GetInterface.class);
        Call<MovieArray> call = getData.listMovie();
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

    private void setupRealm() {
        realm = Realm.getDefaultInstance();
        helper = new RealmHelper(realm);
//        refresh();
    }

    private void setupUi() {
        recyclerView = rootView.findViewById(R.id.movie_rc);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        movieAdapter = new MovieAdapter(movieList, getContext());
        recyclerView.setAdapter(movieAdapter);
    }

    //TODO Movie Recycler
    /*
    private void refresh() {
        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                ItemViewAdapter adapter = new ItemViewAdapter(helper.retrieveAllFoodFromSelectedDb(), getActivity());
                recyclerView.setAdapter(adapter);
            }
        };
        realm.addChangeListener(realmChangeListener);
    }
     */

}
