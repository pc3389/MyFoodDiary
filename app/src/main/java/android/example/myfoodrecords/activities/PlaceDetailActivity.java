package android.example.myfoodrecords.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.utils.RealmHelper;
import android.example.myfoodrecords.model.PlaceModel;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.Realm;

public class PlaceDetailActivity extends AppCompatActivity {

    private Realm realm;
    private RealmHelper helper;

    private int placeId;
    private EditText nameEditText;
    private EditText addressEditText;
    private int id = 0;

    private PlaceModel newPlaceModel = new PlaceModel();
    private PlaceModel placeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        placeId = getIntent().getExtras().getInt(MapsActivity.PLACE_ID_KEY);
        setupRealm();
        setupUi();
    }

    private void setupRealm() {
        realm = Realm.getDefaultInstance();
        helper = new RealmHelper(realm);
        helper.selectPrivatePlaceFromDb();
    }

    private void setupUi() {
        getSupportActionBar().setTitle("Place Detail");
        nameEditText = findViewById(R.id.place_detail_name_et);
        addressEditText = findViewById(R.id.place_detail_address_et);

        placeModel = helper.retrievePlaceWithId(placeId);
        nameEditText.setText(placeModel.getPlaceName());
        addressEditText.setText(placeModel.getAddress());
        id = placeModel.getId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_menu_save) {
            hideKeyboard();
            nullCheck();
            if (!nameEditText.getText().toString().equals("")) {
                newPlaceModel.setPlaceName(nameEditText.getText().toString());
                newPlaceModel.setAddress(addressEditText.getText().toString());
                newPlaceModel.setId(id);
                newPlaceModel.setPrivate(true);
                newPlaceModel.setLat(placeModel.getLat());
                newPlaceModel.setLng(placeModel.getLng());
                helper.insertPlace(newPlaceModel);
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideKeyboard() {
        Activity activity = PlaceDetailActivity.this;
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null && inputManager != null) {
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            inputManager.hideSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
    private void nullCheck() {
        if (nameEditText.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter the Food Name", Toast.LENGTH_SHORT).show();
        }
    }
}