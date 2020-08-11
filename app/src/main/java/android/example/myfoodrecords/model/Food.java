package android.example.myfoodrecords.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Food extends RealmObject {

    @PrimaryKey
    private int id;

    private String name;
    private String rating;
    private String date;
    private String foodType;
    private String location;

    public Food(){}

    public Food(String name, String rating, String date, String foodType, String location) {
        this.name = name;
        this.rating = rating;
        this.date = date;
        this.foodType = foodType;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
