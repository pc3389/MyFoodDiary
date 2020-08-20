package android.example.myfoodrecords.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.adapter.ItemViewAdapter;
import android.example.myfoodrecords.adapter.SummaryAdapter;
import android.example.myfoodrecords.fragments.SummaryFragment;
import android.example.myfoodrecords.model.Food;
import android.example.myfoodrecords.utils.RealmHelper;
import android.os.Bundle;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class SummaryDetailActivity extends AppCompatActivity {

    private Realm realm;
    private RealmHelper helper;

    private RecyclerView recyclerView;
    private List<Food> foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_detail);

        setupRealm();
        setupUi();
    }

    private void setupRealm() {
        realm = Realm.getDefaultInstance();
        helper = new RealmHelper(realm);
    }

    private void setupUi() {
        Intent intent = getIntent();
        String valueFoodOrPlace = intent.getExtras().getString(SummaryAdapter.KEY_FOOD_OR_PLACE);
        String name = intent.getExtras().getString(SummaryAdapter.KEY_SUMMARY_NAME);

        if(valueFoodOrPlace.equals(SummaryFragment.foodString)) {
            foodList = helper.retrieveFoodListWithName(name);
        } else {
            foodList = helper.retrieveFoodListWithPlaceName(name);
        }

        recyclerView = findViewById(R.id.summary_detail_rc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        ItemViewAdapter itemViewAdapter = new ItemViewAdapter(foodList, this);
        recyclerView.setAdapter(itemViewAdapter);
    }

}