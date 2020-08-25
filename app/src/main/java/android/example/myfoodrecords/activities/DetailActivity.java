package android.example.myfoodrecords.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.example.myfoodrecords.PhotoAsyncResponse;
import android.example.myfoodrecords.utils.PhotoUtil;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.utils.RealmHelper;
import android.example.myfoodrecords.adapter.ItemViewAdapter;
import android.example.myfoodrecords.model.Food;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;

public class DetailActivity extends AppCompatActivity implements PhotoAsyncResponse {

    private Realm realm;
    private RealmHelper helper;
    private RealmChangeListener realmChangeListener;

    private Food food;

    private int foodId;
    public static int DETAIL_REQUEST = 0;

    private TextView mNameTextView;
    private RatingBar mRatingBar;
    private TextView mDateTextView;
    private TextView mTypeTextView;
    private ImageView mPhotoImageView;
    private Button showInMapButton;
    private TextView mPlaceNameTextView;
    private TextView mPlaceAddressTextView;

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
        helper.selectFoodFromDb();

        foodId = getIntent().getIntExtra(ItemViewAdapter.KEY_ITEM_FOOD_ID, 0);

        food = realm.where(Food.class)
                .equalTo("id", foodId)
                .findFirst();
    }

    private void setupUi() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        foodId = getIntent().getIntExtra(ItemViewAdapter.KEY_ITEM_FOOD_ID, 0);

        mNameTextView = findViewById(R.id.detail_food_name_tv);
        mRatingBar = findViewById(R.id.detail_rating_tv);
        mDateTextView = findViewById(R.id.detail_date_tv);
        mTypeTextView = findViewById(R.id.detail_food_type_tv);
        mPhotoImageView = findViewById(R.id.detail_food_iv);
        showInMapButton = findViewById(R.id.show_map_button);
        mPlaceNameTextView = findViewById(R.id.detail_place_name_tv);
        mPlaceAddressTextView = findViewById(R.id.detail_place_address_tv);
        showMapOnClick();

        mNameTextView.setText(food.getName());
        mRatingBar.setRating(food.getRating());
        mDateTextView.setText(food.getDate());
        mTypeTextView.setText(food.getFoodType());
        isFavorite = food.getFavorite();
        if(food.getPlaceModel() != null) {
            mPlaceNameTextView.setText(food.getPlaceModel().getPlaceName());
            mPlaceAddressTextView.setText(food.getPlaceModel().getAddress());
        } else {
            showInMapButton.setVisibility(View.INVISIBLE);
        }

        reatingBarChangeListener();

        loadPhoto();
        refresh();

    }

    private void showMapOnClick() {
        showInMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, MapsActivity.class);
                intent.putExtra(EditorActivity.KEY_EDITOR_FOOD_ID, food.getId());
                intent.putExtra(EditorActivity.KEY_REQUEST_CODE, DETAIL_REQUEST);
                startActivity(intent);
            }
        });
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
                item.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.star_yellow, getTheme()));
                isFavorite = true;
            } else {
                item.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.star_grey, getTheme()));
                isFavorite = false;
            }
            saveFoodData();

        } else if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.edit_menu) {
            Intent intent = new Intent(this, EditorActivity.class);
            intent.putExtra("id", food.getId());
            startActivity(intent);
        } else if (id == R.id.delete_menu) {
            Food food = realm.where(Food.class).equalTo("id", foodId).findFirst();
            food.addChangeListener(new RealmChangeListener<RealmModel>() {
                @Override
                public void onChange(RealmModel realmModel) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DetailActivity.this, "Delete Successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NotNull Realm realm) {
                    if (food == null) {
                        Toast.makeText(DetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        food.deleteFromRealm();
                    }
                }
            });

            finish();
            return true;
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

    private void saveFoodData() {
        Food newfood = new Food();
        newfood.setId(food.getId());
        newfood.setName(food.getName());
        newfood.setFoodType(food.getFoodType());
        newfood.setDate(food.getDate());
        newfood.setRating(food.getRating());
        newfood.setPhotoPath(food.getPhotoPath());
        newfood.setFavorite(isFavorite);
        helper.insertFood(newfood);
    }

    private void refresh() {
        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                if (food.isValid() && food.getPlaceModel() != null) {
                    loadPhoto();
                    mPlaceNameTextView.setText(food.getPlaceModel().getPlaceName());
                    mPlaceAddressTextView.setText(food.getPlaceModel().getAddress());
                }
            }
        };
        realm.addChangeListener(realmChangeListener);
    }

    private void reatingBarChangeListener() {
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        food.setRating(rating);
                    }
                });
            }
        });
    }
}