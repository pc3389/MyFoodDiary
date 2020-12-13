package bo.young.myfoodrecords.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.example.myfoodrecords.R;

import bo.young.myfoodrecords.model.Food;
import bo.young.myfoodrecords.model.PlaceModel;
import bo.young.myfoodrecords.utils.Constants;
import bo.young.myfoodrecords.utils.RealmHelper;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class DetailActivity extends AppCompatActivity {

    private Realm realm;
    private RealmHelper helper;
    private RealmChangeListener<Realm> realmChangeListener;

    private Food food;
    private final Context context = DetailActivity.this;

    private TextView nameTextView;
    private RatingBar ratingBar;
    private TextView dateTextView;
    private TextView typeTextView;
    private ImageView photoImageView;
    private Button showInMapButton;
    private TextView placeNameTextView;
    private TextView placeAddressTextView;
    private TextView descriptionTextView;
    private ConstraintLayout placeConstraintLayout;
    private String currentPhotoPath = null;
    private boolean isFromGallery = false;

    private AlertDialog deleteDialog;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setupRealm();
        setupUi();
        refresh();
    }

    /**
     * Receives the data (foodId) when the food item is selected
     * retireve the food data from realm using the foodId
     */
    private void setupRealm() {
        realm = Realm.getDefaultInstance();
        helper = new RealmHelper(realm);
        int foodId = getIntent().getIntExtra(Constants.KEY_ITEM_FOOD_ID, 0);
        food = helper.retrieveFoodWithId(foodId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
    }

    private void setupUi() {
        getSupportActionBar().setTitle(getString(R.string.detail_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameTextView = findViewById(R.id.detail_food_name_tv);
        ratingBar = findViewById(R.id.detail_rating_tv);
        dateTextView = findViewById(R.id.detail_date_tv);
        typeTextView = findViewById(R.id.detail_food_type_tv);
        photoImageView = findViewById(R.id.detail_food_iv);
        showInMapButton = findViewById(R.id.show_map_button);
        placeNameTextView = findViewById(R.id.detail_place_name_tv);
        placeAddressTextView = findViewById(R.id.detail_place_address_tv);
        descriptionTextView = findViewById(R.id.detail_description_tv);
        placeConstraintLayout = findViewById(R.id.detail_linear_layout_place);
        setupShowInMapButtonOnClick();

        isFavorite = food.getFavorite();
        loadData();
        photoOnClick();
    }

    private void loadData() {
        nameTextView.setText(food.getName());
        ratingBar.setRating(food.getRating());
        dateTextView.setText(food.getDate());
        typeTextView.setText(food.getFoodType());
        descriptionTextView.setText(food.getDescription());
        isFavorite = food.getFavorite();
        currentPhotoPath = food.getPhotoPath();
        showPlaceDetail(food.getPlaceModel());
        loadPhoto();
    }

    /**
     * Handles the OnClick event for showInMapButton
     * Shows the location in the Google Map
     */
    private void setupShowInMapButtonOnClick() {
        showInMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, MapsActivity.class);
                intent.putExtra(Constants.KEY_EDITOR_FOOD_ID, food.getId());
                intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.DETAIL_REQUEST);
                startActivity(intent);
            }
        });
    }

    /**
     * Handles the OnClick event for Photo(ImageView)
     * shows the photo in full-screen when clicked
     */
    private void photoOnClick() {
        photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (food.getPhotoPath() != null) {
                    Intent intent = new Intent(DetailActivity.this, PhotoFullscreenActivity.class);
                    intent.putExtra(Constants.KEY_FULL_SCREEN, food.getPhotoPath());
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * @param placeModel If the received food model has the placeModel (Place details),
     *                   shows the place details in the views
     *                   else, hide the views
     */
    private void showPlaceDetail(PlaceModel placeModel) {
        if (food.getPlaceModel() != null) {
            showInMapButton.setVisibility(View.VISIBLE);
            placeConstraintLayout.setVisibility(View.VISIBLE);
            placeNameTextView.setText(placeModel.getPlaceName());
            placeAddressTextView.setText(placeModel.getAddress());
        } else {
            showInMapButton.setVisibility(View.GONE);
            placeConstraintLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_detail, menu);
        //If the food is selected as favorite, show yellow star
        if (isFavorite) {
            menu.findItem(R.id.favorite_btn).setIcon(R.drawable.star_yellow);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // If the food is selected as favorite, show yellow star
        // If not, grey star
        if (id == R.id.favorite_btn) {
            if (!isFavorite) {
                item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.star_yellow, getTheme()));
                isFavorite = true;
            } else {
                item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.star_grey, getTheme()));
                isFavorite = false;
            }
            // Save the food data for automatic update (RealmChangeListener)
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(@NotNull Realm realm) {
                        food.setFavorite(isFavorite);
                    }
                });
            }
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.edit_menu) {
            // pass the foodId and start EditorActivity
            Intent intent = new Intent(this, EditorActivity.class);
            intent.putExtra("id", food.getId());
            startActivity(intent);
        } else if (id == R.id.delete_menu) {
            if (food == null) {
                Toast.makeText(DetailActivity.this, getString(R.string.delete_error_message), Toast.LENGTH_SHORT).show();
            } else {
                setupDeleteDialog();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * If the currentPhotoPath is null, show R.mipmap.ic_no_food
     * else, show the photo file in saved path
     */
    private void loadPhoto() {
        if (!isFinishing() && !isDestroyed()) {
             if (currentPhotoPath == null) {
                Glide.with(context)
                        .load(R.mipmap.ic_no_food)
                        .into(photoImageView);
            } else {
                Glide.with(context)
                        .load(currentPhotoPath)
                        .into(photoImageView);
            }
        }
    }

    /**
     * Add a realm change listener for automatic UI updates
     */
    private void refresh() {
        realmChangeListener = new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm o) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (food.isValid()) {
                            loadData();
                        }
                    }
                });
            }
        };
        realm.addChangeListener(realmChangeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && realmChangeListener != null) {
            realm.removeChangeListener(realmChangeListener);
            realm.close();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (deleteDialog != null) {
            if (deleteDialog.isShowing()) {
                outState.putInt(Constants.KEY_INSTANCE_DIALOG, 1);
                deleteDialog.hide();
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getInt(Constants.KEY_INSTANCE_DIALOG) == 1) {
            setupDeleteDialog();
        }
    }

    /**
     * Opens the dialog asking if the user still wants to delete the file
     * Implemented to prevent the mis-click, and to make sure the user really wants to delete the data
     */
    private void setupDeleteDialog() {
        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(context);
        deleteBuilder.setTitle(getString(R.string.delete_food_item))
                .setMessage(getString(R.string.delete_description))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (food.getPhotoPath() == null || isFromGallery) {
                            helper.deleteFood();
                            finish();
                        } else {
                            boolean deleteSuccessful = new File(food.getPhotoPath()).delete();
                            if (!deleteSuccessful) {
                                Toast.makeText(context, getString(R.string.delete_error_message), Toast.LENGTH_SHORT).show();
                                Log.d(Constants.TAG_DELETE_LOG, "Delete failed");
                            } else {
                                helper.deleteFood();
                                finish();
                            }
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert);
        deleteDialog = deleteBuilder.create();
        deleteDialog.show();
    }
}