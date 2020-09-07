package android.example.myfoodrecords.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieArray {
    @SerializedName("results")
    private List<Movie> results;

    public MovieArray(List<Movie> results) {
        this.results = results;
    }

    public MovieArray() {

    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
