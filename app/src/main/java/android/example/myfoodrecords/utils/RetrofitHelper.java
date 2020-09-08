package android.example.myfoodrecords.utils;

import android.net.Uri;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(String url) {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static String getMovieDataUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie");
        String url = builder.build().toString() + "/";
        return url;
    }

    public static String getPosterPathUrl(String posterPath) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath("original")
                .appendEncodedPath(posterPath);

        String posterUrl = builder.build().toString();

        return posterUrl;
    }

}
