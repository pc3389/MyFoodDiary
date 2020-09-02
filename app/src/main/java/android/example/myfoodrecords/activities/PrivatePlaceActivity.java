package android.example.myfoodrecords.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.example.myfoodrecords.adapter.PrivatePlaceAdapter;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.utils.RealmHelper;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class PrivatePlaceActivity extends AppCompatActivity {

    private Realm realm;
    private RealmHelper helper;
    private RealmChangeListener realmChangeListener;
    private Button addButton;

    public static final int REQUSET_PRIVATE_PLACE = 11;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_address);

        setupRealm();
        setupUi();
    }

    private void setupRealm() {
        realm = Realm.getDefaultInstance();
        helper = new RealmHelper(realm);
        helper.selectPrivatePlaceFromDb();
    }

    private void setupUi() {
        getSupportActionBar().setTitle("Private Place");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addButton = findViewById(R.id.add_place_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivatePlaceActivity.this, MapsActivity.class);
                intent.putExtra(EditorActivity.KEY_REQUEST_CODE, REQUSET_PRIVATE_PLACE);
                startActivity(intent);
            }
        });
        recyclerView = findViewById(R.id.private_address_rc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        PrivatePlaceAdapter privatePlaceAdapter = new PrivatePlaceAdapter(helper.retrievePlaceAll(), this, this);
        recyclerView.setAdapter(privatePlaceAdapter);
        refresh();
    }

    private void refresh() {
        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                PrivatePlaceAdapter privatePlaceAdapter = new PrivatePlaceAdapter(helper.retrievePlaceAll(), PrivatePlaceActivity.this, PrivatePlaceActivity.this);
                recyclerView.setAdapter(privatePlaceAdapter);
            }
        };
        realm.addChangeListener(realmChangeListener);
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
}