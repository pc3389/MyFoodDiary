package android.example.myfoodrecords.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PlaceModel extends RealmObject implements Parcelable {

    @PrimaryKey
    private int id;

    private int foodId;
    private String placeId;
    private double lat;
    private double lng;
    private String placeName;
    private String address;
    private float placeRating;
    private boolean isPrivate;

    public PlaceModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getPlaceRating() {
        return placeRating;
    }

    public void setPlaceRating(float placeRating) {
        this.placeRating = placeRating;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public PlaceModel(Parcel in) {
        String[] data = new String[9];

        in.readStringArray(data);

        this.id = Integer.parseInt(data[0]);
        this.foodId = Integer.parseInt(data[1]);
        this.placeId = data[2];
        this.lat = Double.parseDouble(data[3]);
        this.lng = Double.parseDouble(data[4]);
        this.placeName = data[5];
        this.address = data[6];
        this.placeRating = Float.parseFloat(data[7]);
        this.isPrivate = Boolean.parseBoolean(data[8]);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{String.valueOf(this.id),
                String.valueOf(this.foodId),
                this.placeId,
                String.valueOf(this.lat),
                String.valueOf(this.lng),
                this.placeName,
                this.address,
                String.valueOf(this.placeRating),
                String.valueOf(this.isPrivate)});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PlaceModel createFromParcel(Parcel in) {
            return new PlaceModel(in);
        }

        public PlaceModel[] newArray(int size) {
            return new PlaceModel[size];
        }
    };
}
