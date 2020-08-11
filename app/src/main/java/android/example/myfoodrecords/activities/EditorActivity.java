package android.example.myfoodrecords.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.example.myfoodrecords.R;
import android.example.myfoodrecords.RealmHelper;
import android.example.myfoodrecords.model.Food;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.Realm;

public class EditorActivity extends AppCompatActivity {

    private Realm realm;
    private RealmHelper helper;
    private Food food;

    private EditText mNameEditText;
    private EditText mTypeEditText;
    private EditText mDateEditText;
    private EditText mRatingEditText;
    private EditText mLocationEditText;

    int id = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        setupRealm();
        setupUi();
    }

    private void setupRealm() {
        realm = Realm.getDefaultInstance();
        helper = new RealmHelper(realm);
        helper.selectFromDb();

        id = getIntent().getIntExtra("id", 0);

        food = realm.where(Food.class)
                .equalTo("id", id)
                .findFirst();

    }
    private void setupUi() {

        mNameEditText = findViewById(R.id.editor_food_name_edit);
        mTypeEditText = findViewById(R.id.editor_food_type_edit);
        mDateEditText = findViewById(R.id.editor_date_edit);
        mRatingEditText = findViewById(R.id.editor_rating_edit);
        mLocationEditText = findViewById(R.id.editor_location_edit);

        if (id != 0) {
            mNameEditText.setText(food.getName());
            mTypeEditText.setText(food.getFoodType());
            mDateEditText.setText(food.getDate());
            mRatingEditText.setText(food.getRating());
            mLocationEditText.setText(food.getLocation());
        }


        Button saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nullCheck();
                saveData();
                finish();
            }
        });
    }

    private void nullCheck() {
        if (mNameEditText == null) {
            Toast.makeText(this, "Name cannot be null", Toast.LENGTH_SHORT);
        }
    }

    private void saveData() {
        Food food = new Food();
        food.setId(id);
        food.setName(mNameEditText.getText().toString());
        food.setFoodType(mTypeEditText.getText().toString());
        food.setDate(mDateEditText.getText().toString());
        food.setRating(mRatingEditText.getText().toString());
        food.setLocation(mLocationEditText.getText().toString());

        RealmHelper realmHelper = new RealmHelper(realm);
        realmHelper.insert(food);
    }


}