package android.example.myfoodrecords.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.example.myfoodrecords.R;
import android.example.myfoodrecords.RealmHelper;
import android.example.myfoodrecords.model.PlaceModel;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.realm.Realm;

public class PlaceDetailActivity extends AppCompatActivity {

    private Realm realm;
    private RealmHelper helper;

    private int placeId;
    private EditText nameEditText;
    private EditText addressEditText;
    private Button saveButton;
    private Button deleteButton;

    private PlaceModel newPlaceModel = new PlaceModel();

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
        nameEditText = findViewById(R.id.place_detail_name_et);
        addressEditText = findViewById(R.id.place_detail_address_et);
        saveButton = findViewById(R.id.place_save_button);
        deleteButton = findViewById(R.id.place_delete_button);

        final PlaceModel placeModel = helper.retrievePlaceWithId(placeId);
        nameEditText.setText(placeModel.getPlaceName());
        addressEditText.setText(placeModel.getAddress());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPlaceModel.setPlaceName(nameEditText.getText().toString());
                newPlaceModel.setAddress(addressEditText.getText().toString());
                newPlaceModel.setId(placeId);
                newPlaceModel.setPrivate(true);
                newPlaceModel.setLat(placeModel.getLat());
                newPlaceModel.setLng(placeModel.getLng());
                helper.insertPlace(newPlaceModel);
                finish();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.deletePlace(placeModel.getId());
                finish();
            }
        });
    }
}