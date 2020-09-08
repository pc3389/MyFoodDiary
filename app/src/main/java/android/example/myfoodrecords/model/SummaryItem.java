package android.example.myfoodrecords.model;

/**
 * Summary Item model to update the data in Summay Fragments
 */
public class SummaryItem {
    private String name;
    private float rating;
    private int count;

    public SummaryItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
