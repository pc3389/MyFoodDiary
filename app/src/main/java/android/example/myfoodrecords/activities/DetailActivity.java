package android.example.myfoodrecords.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.RealmHelper;
import android.example.myfoodrecords.model.Food;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import io.realm.Realm;

public class DetailActivity extends AppCompatActivity {

    private Realm realm;
    private RealmHelper helper;

    private Food food;

    private int id;

    private TextView mNameTextView;
    private TextView mRatingTextView;
    private TextView mDateTextView;
    private TextView mTypeTextView;
    private TextView mLocationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id = getIntent().getIntExtra("id", 0);

        mNameTextView = findViewById(R.id.detail_food_name_tv);
        mRatingTextView = findViewById(R.id.detail_rating_tv);
        mDateTextView = findViewById(R.id.detail_date_tv);
        mTypeTextView = findViewById(R.id.detail_food_type_tv);
        mLocationTextView = findViewById(R.id.detail_location_tv);

        mNameTextView.setText(food.getName());
        mRatingTextView.setText(food.getRating());
        mDateTextView.setText(food.getDate());
        mTypeTextView.setText(food.getFoodType());
        mLocationTextView.setText(food.getLocation());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.edit_menu) {
            Intent intent = new Intent(this, EditorActivity.class);
            intent.putExtra("id", food.getId());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}