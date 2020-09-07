package android.example.myfoodrecords.utils;

import android.example.myfoodrecords.model.Movie;
import android.example.myfoodrecords.model.MovieArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetInterface {
    @GET("upcoming?api_key=f147e6d0eaf5d22642d61e4e98630051&language=en-US&page=1")
    Call<MovieArray> listMovie();
}
