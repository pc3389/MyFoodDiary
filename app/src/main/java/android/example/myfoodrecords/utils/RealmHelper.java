package android.example.myfoodrecords.utils;

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

    /**
     *
     */
    public void selectPrivatePlaceFromDb() {
        placeRealmResults = realm.where(PlaceModel.class)
                .equalTo("isPrivate", true)
                .findAll();
    }

    public void selectFavoriteFromDb() {
        foodRealmResults = realm.where(Food.class)
                .equalTo("isFavorite", true)
                .findAll();
    }

    /**
     *
     * @param food
     *
     */
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

    /**
     * Read Food data from Realm. Used to update the RecyclerView
     *
     * @return
     *
     */
    public List<Food> retrieveFoodAll() {
        List<Food> foodList = new ArrayList<>(foodRealmResults);
        return foodList;
    }

    public List<PlaceModel> retrievePlaceAll() {
        List<PlaceModel> placeList = new ArrayList<>(placeRealmResults);
        return placeList;
    }

    public List<Food> retrieveFoodWithNameSorted() {
        RealmResults<Food> foodRealmResults;
        foodRealmResults = realm.where(Food.class)
                .sort("name")
                .findAll();
        return new ArrayList<>(foodRealmResults);
    }
    public List<Food> retrieveFoodWithTypeSorted() {
        RealmResults<Food> foodRealmResults;
        foodRealmResults = realm.where(Food.class)
                .sort("foodType")
                .findAll();
        return new ArrayList<>(foodRealmResults);
    }

    public Food retrieveFoodWithId(int foodId) {
        Food food;
        food = realm.where(Food.class).equalTo("id", foodId).findFirst();
        return food;
    }

    public PlaceModel retrievePlaceWithId(int placeId) {
        PlaceModel placeModel;
        placeModel = realm.where(PlaceModel.class).equalTo("id", placeId).findFirst();
        return placeModel;
    }

    public List<Food> retrieveFoodListWithName(String foodName) {
        List<Food> food;
        food = realm.where(Food.class).equalTo("name", foodName).findAll();
        return food;
    }
    public List<Food> retrieveFoodListWithType(String foodType) {
        List<Food> food;
        food = realm.where(Food.class).equalTo("foodType", foodType).findAll();
        return food;
    }

    public List<Food> retrieveFoodListWithPlaceName(String placeName) {
        List<Food> food;
        food = realm.where(Food.class).equalTo("placeModel.placeName", placeName).findAll();
        return food;
    }


    /**
     *
     */
    public void deleteAllFood() {
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

    public void deleteAllPlaces() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<PlaceModel> results = realm.where(PlaceModel.class).findAll();
                results.deleteAllFromRealm();
            }
        });
    }

    /**
     *
     * @param id
     */
    public void deleteFood(final int id) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Food result = realm.where(Food.class).equalTo("id", id).findFirst();
                result.deleteFromRealm();
            }
        });
    }

    public void deletePlace(final int id) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                PlaceModel result = realm.where(PlaceModel.class).equalTo("id", id).findFirst();
                result.deleteFromRealm();
            }
        });
    }

}
