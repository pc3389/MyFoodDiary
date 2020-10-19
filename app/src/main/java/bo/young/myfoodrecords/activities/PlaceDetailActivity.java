package bo.young.myfoodrecords.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.example.myfoodrecords.R;

import bo.young.myfoodrecords.model.PlaceModel;
import bo.young.myfoodrecords.utils.Constants;
import bo.young.myfoodrecords.utils.RealmHelper;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.Realm;

public class PlaceDetailActivity extends AppCompatActivity {

    private Realm realm;
    private RealmHelper helper;

    private int placeId;
    private EditText nameEditText;
    private EditText addressEditText;

    private PlaceModel newPlaceModel = new PlaceModel();
    private PlaceModel placeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        placeId = getIntent().getExtras().getInt(Constants.KEY_PLACE_ID);
        setupRealm();
        setupUi();
    }

    private void setupRealm() {
        realm = Realm.getDefaultInstance();
        helper = new RealmHelper(realm);
    }

    private void setupUi() {
        getSupportActionBar().setTitle("Place Detail");
        nameEditText = findViewById(R.id.place_detail_name_et);
        addressEditText = findViewById(R.id.place_detail_address_et);

        placeModel = helper.retrievePlaceWithId(placeId);
        nameEditText.setText(placeModel.getPlaceName());
        addressEditText.setText(placeModel.getAddress());
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
            // Save the data to Realm and finish
            if (!nameEditText.getText().toString().equals("")) {
                newPlaceModel.setPlaceName(nameEditText.getText().toString());
                newPlaceModel.setAddress(addressEditText.getText().toString());
                newPlaceModel.setId(placeModel.getId());
                newPlaceModel.setPrivate(placeModel.isPrivate());
                newPlaceModel.setLat(placeModel.getLat());
                newPlaceModel.setLng(placeModel.getLng());
                helper.insertPlace(newPlaceModel);
                finish();
            } else {
                Toast.makeText(this, getString(R.string.name_not_entered), Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * hide the keyboard when finished
     */
    private void hideKeyboard() {
        Activity activity = PlaceDetailActivity.this;
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null && inputManager != null) {
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            inputManager.hideSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }
}