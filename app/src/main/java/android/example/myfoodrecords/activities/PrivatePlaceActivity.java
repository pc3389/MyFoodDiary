package android.example.myfoodrecords.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.example.myfoodrecords.adapter.PrivatePlaceAdapter;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.utils.RealmHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class PrivatePlaceActivity extends AppCompatActivity {

    private Realm realm;
    private RealmHelper helper;
    private RealmChangeListener realmChangeListener;
    private Context context;

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
    }

    private void setupUi() {
        context = PrivatePlaceActivity.this;
        getSupportActionBar().setTitle("Private Place");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.private_address_rc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        PrivatePlaceAdapter privatePlaceAdapter = new PrivatePlaceAdapter(helper.retrievePrivatePlaceAll(), this, this);
        recyclerView.setAdapter(privatePlaceAdapter);
        refresh();
    }

    private void refresh() {
        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                PrivatePlaceAdapter privatePlaceAdapter = new PrivatePlaceAdapter(helper.retrievePrivatePlaceAll(), PrivatePlaceActivity.this, PrivatePlaceActivity.this);
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
        if (id == R.id.add_place_menu) {
            Intent intent = new Intent(PrivatePlaceActivity.this, MapsActivity.class);
            intent.putExtra(EditorActivity.KEY_REQUEST_CODE, REQUSET_PRIVATE_PLACE);
            startActivity(intent);
        }

        if (id == R.id.delete_all_place) {
            new AlertDialog.Builder(context)
                    .setTitle("Delete All Locations")
                    .setMessage("Are you sure you want to delete all location details?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            helper.deleteAllPlaces();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_private_place, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (PrivatePlaceAdapter.dialog != null) {
            if (PrivatePlaceAdapter.dialog.isShowing()) {
                outState.putInt(EditorActivity.KEY_INSTANCE_DIALOG, 1);
            }
            PrivatePlaceAdapter.dialog.hide();
        }
        if (PrivatePlaceAdapter.deleteDialog != null) {
            if (PrivatePlaceAdapter.deleteDialog.isShowing()) {
                outState.putInt(EditorActivity.KEY_INSTANCE_DIALOG, 2);
            }
            PrivatePlaceAdapter.deleteDialog.hide();
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getInt(EditorActivity.KEY_INSTANCE_DIALOG) == 1) {
            PrivatePlaceAdapter.setupDialog();
        }
        if (savedInstanceState.getInt(EditorActivity.KEY_INSTANCE_DIALOG) == 2) {
            PrivatePlaceAdapter.setupDeleteDialog();
        }
    }
}