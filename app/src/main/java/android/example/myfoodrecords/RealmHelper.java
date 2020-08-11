package android.example.myfoodrecords;

import android.example.myfoodrecords.activities.MainActivity;
import android.example.myfoodrecords.model.Food;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmHelper {

    Realm realm;
    RealmResults<Food> foodRealmResults;

    public RealmHelper(Realm realm) {
        this.realm = realm;
    }

    public void selectFromDb() {
        foodRealmResults = realm.where(Food.class).findAll();
    }

    //Insert & Update
    public void insert(final Food food) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int newKey = 0;
                Number maxId = realm.where(Food.class).max("id");
                if(food.getId() < 1) {
                    if(maxId == null) {
                        newKey = 1;
                    } else {newKey = maxId.intValue()+1;}
                } else newKey = food.getId();
                food.setId(newKey);

                realm.insertOrUpdate(food);
            }
        });
    }

    //Read
    public List<Food> retireve() {
        List<Food> foodList = new ArrayList<>(foodRealmResults);
        return foodList;
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
