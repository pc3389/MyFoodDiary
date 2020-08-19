package android.example.myfoodrecords.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.example.myfoodrecords.R;
import android.example.myfoodrecords.adapter.ItemViewAdapter;
import android.example.myfoodrecords.utils.RealmHelper;
import android.os.Bundle;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class SummaryDetailActivity extends AppCompatActivity {

    private Realm realm;
    private RealmHelper helper;

    private RecyclerView recyclerView;

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
        helper.selectFoodFromDb();
    }

    //TODO fix the adapter. not from all, by name (Food or Place)
    private void setupUi() {
        recyclerView = findViewById(R.id.private_address_rc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        ItemViewAdapter itemViewAdapter = new ItemViewAdapter(helper.retrieveFoodAll(), this);
        recyclerView.setAdapter(itemViewAdapter);
    }

}