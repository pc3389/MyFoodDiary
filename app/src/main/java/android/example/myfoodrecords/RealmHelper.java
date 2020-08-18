package android.example.myfoodrecords;

import android.example.myfoodrecords.activities.MainActivity;
import android.example.myfoodrecords.model.Food;
import android.example.myfoodrecords.model.PlaceModel;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmHelper {

    private Realm realm;
    private RealmResults<Food> foodRealmResults;
    private RealmResults<PlaceModel> placeRealmResults;

    public RealmHelper(Realm realm) {
        this.realm = realm;
    }

    public void selectFoodFromDb() {
        foodRealmResults = realm.where(Food.class).findAll();
    }

    public void selectPlaceFromDb() {
        placeRealmResults = realm.where(PlaceModel.class).findAll();
    }

    public void selectFavoritePlaceFromDb() {
        placeRealmResults = realm.where(PlaceModel.class).equalTo("isPrivate", true).findAll();
    }

    public void selectFavoriteFromDb() {
        foodRealmResults = realm.where(Food.class).equalTo("isFavorite", true).findAll();
    }

    //Insert & Update
    public void insertFood(final Food food) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int newKey = 0;
                Number maxId = realm.where(Food.class).max("id");
                if (food.getId() < 1) {
                    if (maxId == null) {
                        newKey = 1;
                    } else {
                        newKey = maxId.intValue() + 1;
                    }
                } else newKey = food.getId();
                food.setId(newKey);

                realm.insertOrUpdate(food);
            }
        });
    }

    public void insertPlace(final PlaceModel place) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int newKey = 0;
                Number maxId = realm.where(PlaceModel.class).max("id");
                if (place.getId() < 1) {
                    if (maxId == null) {
                        newKey = 1;
                    } else {
                        newKey = maxId.intValue() + 1;
                    }
                } else newKey = place.getId();
                place.setId(newKey);

                realm.copyToRealmOrUpdate(place);
            }
        });
    }

    //Read
    public List<Food> retireveFoodAll() {
        List<Food> foodList = new ArrayList<>(foodRealmResults);
        return foodList;
    }

    public List<PlaceModel> retirevePlaceAll() {
        List<PlaceModel> placeList = new ArrayList<>(placeRealmResults);
        return placeList;
    }

    public Food retrieveFoodWithId(int foodId) {
        Food food = new Food();
        food = realm.where(Food.class).equalTo("id", foodId).findFirst();
        return food;
    }

    public PlaceModel retrievePlaceWithId(int placeId) {
        PlaceModel placeModel = new PlaceModel();
        placeModel = realm.where(PlaceModel.class).equalTo("id", placeId).findFirst();
        return placeModel;
    }


    //DeleteAll
    public void deleteAll() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Food> results = realm.where(Food.class).findAll();
                results.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.context, "Successfully deleted the data", Toast.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(MainActivity.context, "Failed to delete the data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Delete
    public void delete(final int id) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Food> result = realm.where(Food.class).equalTo("id", id).findAll();
                result.deleteAllFromRealm();
            }
        });
    }

}
