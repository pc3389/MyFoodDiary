package android.example.myfoodrecords.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.adapter.ItemViewAdapter;
import android.example.myfoodrecords.adapter.SummaryAdapter;
import android.example.myfoodrecords.model.Food;
import android.example.myfoodrecords.utils.Constants;
import android.example.myfoodrecords.utils.RealmHelper;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class SummaryDetailActivity extends AppCompatActivity {

    private Realm realm;
    private RealmHelper helper;
    private RealmChangeListener realmChangeListener;

    private RecyclerView recyclerView;
    private List<Food> foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_detail);

        setupRealm();
        setupUi();
        refresh();
    }

    private void setupRealm() {
        realm = Realm.getDefaultInstance();
        helper = new RealmHelper(realm);
    }

    private void setupUi() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String valueFoodOrPlace = intent.getExtras().getString(SummaryAdapter.KEY_FOOD_OR_PLACE);
        String name = intent.getExtras().getString(SummaryAdapter.KEY_SUMMARY_NAME);

        //Check from which fragment the intent is sent and query the data from Realm accordingly
        if(valueFoodOrPlace.equals(Constants.FOOD_STRING)) {
            getSupportActionBar().setTitle(Constants.FOOD_STRING);
            foodList = helper.retrieveFoodListWithName(name);
        } else if(valueFoodOrPlace.equals(Constants.TYPE_STRING)){
            getSupportActionBar().setTitle(Constants.TYPE_STRING);
            foodList = helper.retrieveFoodListWithType(name);
        } else if(valueFoodOrPlace.equals(Constants.PLACE_STRING)) {
            getSupportActionBar().setTitle(Constants.PLACE_STRING);
            foodList = helper.retrieveFoodListWithPlaceName(name);
        }

        recyclerView = findViewById(R.id.summary_detail_rc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        ItemViewAdapter itemViewAdapter = new ItemViewAdapter(foodList, this);
        recyclerView.setAdapter(itemViewAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Add a realm change listener for automatic UI updates
     */
    private void refresh() {
        realmChangeListener = new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm o) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupUi();
                        if(foodList != null && foodList.isEmpty()) {
                            finish();
                        }
                    }
                });
            }
        };
        realm.addChangeListener(realmChangeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && realmChangeListener != null) {
            realm.removeChangeListener(realmChangeListener);
            realm.close();
        }
    }
}