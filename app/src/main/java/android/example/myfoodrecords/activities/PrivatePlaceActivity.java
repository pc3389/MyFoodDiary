package android.example.myfoodrecords.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.example.myfoodrecords.MyApplication;
import android.example.myfoodrecords.PrivatePlaceAdapter;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.RealmHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class PrivatePlaceActivity extends AppCompatActivity {

    private Realm realm;
    private RealmHelper helper;
    private RealmChangeListener realmChangeListener;
    private Button addButton;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_address);

        setupRealm();
        setupUi();
    }

    private void setupRealm() {
//        realm = Realm.getInstance(MyApplication.placeConfig);
        realm = Realm.getDefaultInstance();
        helper = new RealmHelper(realm);
        helper.selectFavoritePlaceFromDb();
    }

    private void setupUi() {
        addButton = findViewById(R.id.add_place_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivatePlaceActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        recyclerView = findViewById(R.id.private_address_rc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        PrivatePlaceAdapter privatePlaceAdapter = new PrivatePlaceAdapter(helper.retirevePlaceAll(), this, this);
        recyclerView.setAdapter(privatePlaceAdapter);
        refresh();
    }

    private void refresh() {
        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                PrivatePlaceAdapter privatePlaceAdapter = new PrivatePlaceAdapter(helper.retirevePlaceAll(), PrivatePlaceActivity.this, PrivatePlaceActivity.this);
                recyclerView.setAdapter(privatePlaceAdapter);
            }
        };
        realm.addChangeListener(realmChangeListener);
    }
}