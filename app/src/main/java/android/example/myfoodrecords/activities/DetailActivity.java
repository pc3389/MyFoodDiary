package android.example.myfoodrecords.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.utils.RealmHelper;
import android.example.myfoodrecords.adapter.ItemViewAdapter;
import android.example.myfoodrecords.model.Food;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class DetailActivity extends AppCompatActivity {

    private Realm realm;
    private RealmHelper helper;
    private RealmChangeListener realmChangeListener;

    private Food food;
    private final Context context = DetailActivity.this;

    private int foodId;
    public static int DETAIL_REQUEST = 0;
    public static String KEY_FULL_SCREEN = "photoFull";

    private TextView mNameTextView;
    private TextView mRatingTextView;
    private TextView mDateTextView;
    private TextView mTypeTextView;
    private ImageView mPhotoImageView;
    private Button showInMapButton;
    private TextView mPlaceNameTextView;
    private TextView mPlaceAddressTextView;
    private TextView mDescriptionTextView;
    private ConstraintLayout mPlaceConstraintLayout;
    private String currentPhotoPath = null;

    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setupRealm();
        setupUi();
        refresh();
    }

    private void setupRealm() {
        realm = Realm.getDefaultInstance();
        helper = new RealmHelper(realm);

        foodId = getIntent().getIntExtra(ItemViewAdapter.KEY_ITEM_FOOD_ID, 0);
        food = helper.retrieveFoodWithId(foodId);
    }

    private void setupUi() {
        getSupportActionBar().setTitle("Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNameTextView = findViewById(R.id.detail_food_name_tv);
        mRatingTextView = findViewById(R.id.detail_rating_tv);
        mDateTextView = findViewById(R.id.detail_date_tv);
        mTypeTextView = findViewById(R.id.detail_food_type_tv);
        mPhotoImageView = findViewById(R.id.detail_food_iv);
        showInMapButton = findViewById(R.id.show_map_button);
        mPlaceNameTextView = findViewById(R.id.detail_place_name_tv);
        mPlaceAddressTextView = findViewById(R.id.detail_place_address_tv);
        mDescriptionTextView = findViewById(R.id.detail_description_tv);
        mPlaceConstraintLayout = findViewById(R.id.detail_linear_layout_place);
        showMapOnClick();


        isFavorite = food.getFavorite();
        loadData();
        mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (food.getPhotoPath() != null) {
                    Intent intent = new Intent(DetailActivity.this, PhotoFullscreenActivity.class);
                    intent.putExtra(KEY_FULL_SCREEN, food.getPhotoPath());
                    startActivity(intent);
                }
            }
        });
    }

    private void loadData() {
        mNameTextView.setText(food.getName());
        mRatingTextView.setText(String.valueOf(food.getRating()));
        mDateTextView.setText(food.getDate());
        mTypeTextView.setText(food.getFoodType());
        mDescriptionTextView.setText(food.getDescription());
        isFavorite = food.getFavorite();

        if (food.getPlaceModel() != null) {
            showInMapButton.setVisibility(View.VISIBLE);
            mPlaceConstraintLayout.setVisibility(View.VISIBLE);
            mPlaceNameTextView.setText(food.getPlaceModel().getPlaceName());
            mPlaceAddressTextView.setText(food.getPlaceModel().getAddress());
        } else {
            showInMapButton.setVisibility(View.INVISIBLE);
            mPlaceConstraintLayout.setVisibility(View.INVISIBLE);
        }
        currentPhotoPath = food.getPhotoPath();
        loadPhoto();
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
        if (isFavorite) {
            menu.findItem(R.id.favorite_btn).setIcon(R.drawable.star_yellow);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.favorite_btn) {
            if (!isFavorite) {
                item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.star_yellow, getTheme()));
                isFavorite = true;
            } else {
                item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.star_grey, getTheme()));
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
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NotNull Realm realm) {
                    if (food == null) {
                        Toast.makeText(DetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    } else {
                        new AlertDialog.Builder(context)
                                .setTitle("Delete All Food Items")
                                .setMessage("Are you sure you want to delete All Food Items?")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        helper.deleteFood(food.getId());
                                        finish();
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPhoto() {
        if (!isFinishing() && !isDestroyed()) {
            if (currentPhotoPath == null) {
                Glide.with(context)
                        .load(R.mipmap.ic_no_food)
                        .into(mPhotoImageView);
            } else {
                Glide.with(context)
                        .load(currentPhotoPath)
                        .into(mPhotoImageView);
            }
        }
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
        newfood.setDescription(food.getDescription());
        newfood.setPlaceModel(food.getPlaceModel());
        helper.insertFood(newfood);
    }

    private void refresh() {
        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                if (food.isValid()) {
                    loadData();
                }
            }
        };
        realm.addChangeListener(realmChangeListener);
    }
}