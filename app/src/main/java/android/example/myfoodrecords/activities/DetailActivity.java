package android.example.myfoodrecords.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.example.myfoodrecords.PhotoAsyncResponse;
import android.example.myfoodrecords.PhotoUtil;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.RealmHelper;
import android.example.myfoodrecords.model.Food;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.Realm;

public class DetailActivity extends AppCompatActivity implements PhotoAsyncResponse {

    private Realm realm;
    private RealmHelper helper;

    private Food food;

    private int id;

    private TextView mNameTextView;
    private TextView mRatingTextView;
    private TextView mDateTextView;
    private TextView mTypeTextView;
    private TextView mLocationTextView;
    private ImageView mPhotoImageView;

    private boolean isFavorite;

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
        mPhotoImageView = findViewById(R.id.detail_food_iv);

        mNameTextView.setText(food.getName());
        mRatingTextView.setText(food.getRating());
        mDateTextView.setText(food.getDate());
        mTypeTextView.setText(food.getFoodType());
        mLocationTextView.setText(food.getLocation());
        isFavorite = food.getFavorite();

        loadPhoto();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_detail, menu);
        if(isFavorite) {
            menu.findItem(R.id.favorite_btn).setIcon(R.drawable.star_yellow);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.favorite_btn) {
            if(!isFavorite) {
                item.setIcon(getResources().getDrawable(R.drawable.star_yellow, getTheme()));
                isFavorite = true;
            } else {
                item.setIcon(getResources().getDrawable(R.drawable.star_grey, getTheme()));
                isFavorite = false;
            }
            saveData();

        }
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.edit_menu) {
            Intent intent = new Intent(this, EditorActivity.class);
            intent.putExtra("id", food.getId());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPhoto() {
        if(food.getPhotoPath() != null) {
            new PhotoUtil(food.getPhotoPath(), false);
            PhotoUtil.PhotoAsync photoAsync = new PhotoUtil.PhotoAsync();
            photoAsync.delegate = this;
            photoAsync.execute();
        }
    }

    @Override
    public void processFinish(Bitmap bitmap) {
        mPhotoImageView.setImageBitmap(bitmap);
    }

    private void saveData() {
        Food newfood = new Food();
        newfood.setId(food.getId());
        newfood.setName(food.getName());
        newfood.setFoodType(food.getFoodType());
        newfood.setDate(food.getDate());
        newfood.setRating(food.getRating());
        newfood.setLocation(food.getLocation());
        newfood.setPhotoPath(food.getPhotoPath());
        newfood.setFavorite(isFavorite);
        helper.insert(newfood);
    }

}