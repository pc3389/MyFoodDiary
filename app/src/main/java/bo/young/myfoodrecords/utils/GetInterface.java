package bo.young.myfoodrecords.utils;

import bo.young.myfoodrecords.model.MovieArray;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetInterface {
    @GET("upcoming")
    Call<MovieArray> listMovie(@Query("api_key") String apiKey,
                               @Query("language") String language,
                               @Query("page") int pageNumber);
}
