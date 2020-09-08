package android.example.myfoodrecords.utils;

import android.example.myfoodrecords.model.Food;
import android.example.myfoodrecords.model.PlaceModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmHelper {

    private Realm realm;

    private RealmResults<Food> foodRealmResults;
    private Food food;
    private List<Food> foodList;

    public RealmHelper(Realm realm) {
        this.realm = realm;
    }

    public void selectAllFoodsFromDb() {
        foodRealmResults = realm.where(Food.class).findAll();
    }

    public void selectAllFavoriteFoodsFromDb() {
        foodRealmResults = realm.where(Food.class)
                .equalTo("isFavorite", true)
                .findAll();
    }


    /**
     * @param food
     */
    public void insertFood(final Food food) {
        try (Realm realm = Realm.getDefaultInstance()) {
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
    }

    public void insertPlace(final PlaceModel place) {
        try(Realm realm = Realm.getDefaultInstance()) {
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
    }

    /**
     * Read Food data from Realm. Used to update the RecyclerView
     *
     * @return
     */
    public List<Food> retrieveAllFoodFromSelectedDb() {
        foodList = new ArrayList<>(foodRealmResults);
        return foodList;
    }


    public List<PlaceModel> selectAndRetrieveAllPlaces() {
        RealmResults<PlaceModel> placeRealmResults = realm.where(PlaceModel.class)
                .equalTo("isPrivate", true)
                .findAll();
        List<PlaceModel> placeList = new ArrayList<>(placeRealmResults);
        return placeList;
    }

    public List<Food> retrieveFoodWithNameSorted() {
        foodRealmResults = realm.where(Food.class)
                .sort("name")
                .findAll();
        return new ArrayList<>(foodRealmResults);
    }

    public List<Food> retrieveFoodWithTypeSorted() {
        foodRealmResults = realm.where(Food.class)
                .sort("foodType")
                .findAll();
        return new ArrayList<>(foodRealmResults);
    }

    public Food retrieveFoodWithId(int foodId) {
        food = realm.where(Food.class).equalTo(Constants.REALM_ID, foodId).findFirst();
        return food;
    }

    public PlaceModel retrievePlaceWithId(int placeId) {
        PlaceModel placeModel = realm.where(PlaceModel.class).equalTo("id", placeId).findFirst();
        return placeModel;
    }

    public List<Food> retrieveFoodListWithName(String foodName) {
        foodList = realm.where(Food.class).equalTo("name", foodName).findAll();
        return foodList;
    }

    public List<Food> retrieveFoodListWithType(String foodType) {
        foodList = realm.where(Food.class).equalTo("foodType", foodType).findAll();
        return foodList;
    }

    public List<Food> retrieveFoodListWithPlaceName(String placeName) {
        foodList = realm.where(Food.class).equalTo("placeModel.placeName", placeName).findAll();
        return foodList;
    }


    /**
     *
     */
    public void deleteAllFood() {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Food> results = realm.where(Food.class).findAll();
                    if (!results.isEmpty()) {
                        results.deleteAllFromRealm();
                    }
                }
            });
        }
    }

    public void deleteAllPlaces() {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<PlaceModel> results = realm.where(PlaceModel.class).findAll();
                    if (!results.isEmpty()) {
                        results.deleteAllFromRealm();
                    }
                }
            });
        }
    }

    /**
     * @param id
     */
    public void deleteFood(final int id) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Food result = realm.where(Food.class).equalTo("id", id).findFirst();
                    if (result != null) {
                        result.deleteFromRealm();
                    }
                }
            });
        }
    }

    public void deleteFood() {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (food != null) {
                        food.deleteFromRealm();
                    }
                }
            });
        }
    }

    public void deletePlace(final int id) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    PlaceModel result = realm.where(PlaceModel.class).equalTo("id", id).findFirst();
                    if (result != null) {
                        result.deleteFromRealm();
                    }
                }
            });
        }
    }

}
