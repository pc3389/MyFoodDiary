package android.example.myfoodrecords.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.example.myfoodrecords.BuildConfig;
import android.example.myfoodrecords.MyEditText;
import android.example.myfoodrecords.adapter.PrivatePlaceAdapter;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.utils.Constants;
import android.example.myfoodrecords.utils.RealmHelper;
import android.example.myfoodrecords.model.Food;
import android.example.myfoodrecords.model.PlaceModel;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class EditorActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Realm realm;
    private RealmHelper helper;
    private RealmChangeListener<Realm> realmChangeListener;

    private Food food;
    private PlaceModel placeModel;

    private final Context context = EditorActivity.this;

    private MyEditText nameEditText;
    private MyEditText typeEditText;
    private MyEditText descriptionEditText;
    private TextView dateTextView;
    private ImageView calenderImageView;
    private RatingBar ratingBar;
    private ImageView photoImageView;
    private TextView placeNameTextView;
    private TextView placeAddressTextView;
    private ConstraintLayout placeConstraintLayout;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String KEY_INSTANCE_PHOTO = "savein";
    private static final String KEY_INSTANCE_PREVIOUS_PHOTO = "keyPreviouos";
    private static final String KEY_INSTANCE_DATE = "keyDate";
    private static final String KEY_INSTANCE_NAME = "keyName";
    private static final String KEY_INSTANCE_TYPE = "keyType";
    private static final String KEY_INSTANCE_RATING = "keyRating";
    private static final String KEY_INSTANCE_DESCRIPTION = "keyDescription";
    private static final String KEY_INSTANCE_PLACE = "keyPlace";
    private static final int REQUEST_PRIVATE_PLACE = 4;

    private DatePickerDialog datePickerDialog;
    private int year, month, day;
    private boolean isFavorite;
    private String date;

    private String currentPhotoPath = null;
    private String previousPhotoPath;
    private AlertDialog dialog;

    private int foodId = 0;
    private boolean hasPhoto = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        setupRealm();
        setupUi();
        datePickerSetUp();
    }

    private void setupRealm() {
        realm = Realm.getDefaultInstance();
        helper = new RealmHelper(realm);

        foodId = getIntent().getIntExtra("id", 0);

        food = realm.where(Food.class)
                .equalTo("id", foodId)
                .findFirst();
    }

    private void setupUi() {
        getSupportActionBar().setTitle("Edit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        todayDate();

        nameEditText = findViewById(R.id.editor_food_name_edit);
        typeEditText = findViewById(R.id.editor_food_type_edit);
        descriptionEditText = findViewById(R.id.editor_description_edit);
        dateTextView = findViewById(R.id.editor_date_edit);
        dateTextView.setText(date);
        calenderImageView = findViewById(R.id.calender_iv);
        ratingBar = findViewById(R.id.editor_rating_edit);
        photoImageView = findViewById(R.id.editor_food_iv);
        placeNameTextView = findViewById(R.id.editor_place_name_tv);
        placeAddressTextView = findViewById(R.id.editor_place_address_tv);
        placeConstraintLayout = findViewById(R.id.location_layout);
        TextView locationTextView = findViewById(R.id.push_to_edit_image);

        //Shows the dialog when locationTextView is clicked
        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupPlaceDialog();
            }
        });

        photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        loadData();

        // hasPhoto is only used to verify that the previous photo file should be deleted or not
        // Set the hasPhoto to false here to prevent the file getting deleted without saving
        hasPhoto = false;

        refresh();
    }

    /**
     * Opens the DatePickerDialog when clicking dataTextView or calenderImageView
     */
    private void datePickerSetUp() {
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDatePickerDialog();
            }
        });
        calenderImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDatePickerDialog();
            }
        });
    }

    /**
     * Show the DatePickerDialog and automatically set the date to saved date(today if null) when it is shown
     */
    private void setupDatePickerDialog() {
        datePickerDialog = DatePickerDialog.newInstance(EditorActivity.this, year, month, day);
        datePickerDialog.setThemeDark(false);
        datePickerDialog.showYearPickerFirst(false);
        datePickerDialog.setTitle("Date Picker");
        datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
    }

    /**
     * set date variables(year, month, day and date) to the today's one
     */
    private void todayDate() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        date = day + "/" + (month + 1) + "/" + year;
    }

    /**
     * When the date is set in the DatePickerDialog,
     * show the date in the DateTextView
     */
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        dateTextView.setText(date);
    }

    /**
     * Saves the data model to Realm database
     */
    private void saveFoodData() {
        Food food = new Food();
        food.setId(foodId);
        food.setName(nameEditText.getText().toString());
        if (typeEditText.getText().toString().matches("")) {
            food.setFoodType(Constants.NO_TYPE);
        } else {
            food.setFoodType(typeEditText.getText().toString());
        }
        food.setDescription(descriptionEditText.getText().toString());
        food.setDate(dateTextView.getText().toString());
        food.setRating(ratingBar.getRating());
        food.setPhotoPath(currentPhotoPath);
        food.setPlaceModel(placeModel);
        food.setFavorite(isFavorite);

        helper.insertFood(food);
    }

    /**
     * Create the photoFile and start the Camera Intent for result
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Receives and uses the data using Intent from another Activities
     *
     * @param requestCode REQUEST_TAKE_PHOTO - from Camera Activity
     *                    REQUEST_MAP - from MapsActivity, select from Map
     *                    REQUEST_PRIVATE_PLACE - from PrivatePlaceActivity, which gets the PlaceModel
     * @param resultCode  RESULT_OK - From Camera Activity, when photo is saved
     *                    RESULT_CANCELED - From Camera Activity, when back pressed
     *                    RESULT_MAP & RESULT_PRIVATE_PLACE - PlaceModel related.
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (hasPhoto && previousPhotoPath != null) {
                deletePhotoFile(previousPhotoPath);
            }
            loadPhoto();
            previousPhotoPath = currentPhotoPath;
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_CANCELED) {
            currentPhotoPath = previousPhotoPath;
            loadPhoto();
        } else if (requestCode == Constants.REQUEST_MAP && resultCode == Constants.RESULT_MAP) {
            placeModel = realm.where(PlaceModel.class).equalTo("id", data.getIntExtra(Constants.KEY_PLACE_ID, 0)).findFirst();
            showPlaceDetails();
        } else if (requestCode == REQUEST_PRIVATE_PLACE && resultCode == PrivatePlaceAdapter.RESULT_PRIVATE_PLACE) {
            placeModel = realm.where(PlaceModel.class).equalTo("id", data.getIntExtra(PrivatePlaceAdapter.PUT_PLACE_ID, 0)).findFirst();
            showPlaceDetails();
        }
    }

    /**
     * if placeModel is not null, show the place details
     * else, hide them
     */
    private void showPlaceDetails() {
        if (placeModel != null) {
            placeConstraintLayout.setVisibility(View.VISIBLE);
            placeNameTextView.setText(placeModel.getPlaceName());
            placeAddressTextView.setText(placeModel.getAddress());
        } else {
            placeConstraintLayout.setVisibility(View.GONE);
        }
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
                hasPhoto = false;
            } else {
                Glide.with(context)
                        .load(currentPhotoPath)
                        .override(1000)
                        .into(photoImageView);
                hasPhoto = true;
            }
        }
    }

    /**
     * Create temporal image file here
     *
     * @return temporal photoFile is returned to save the Photo using Camera Activity
     * @throws IOException createTempFile throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = photoFile.getAbsolutePath();
        return photoFile;
    }

    /**
     * setup the dialog for place details
     * opens PrivatePlaceActivity or MapsActivity accordingly, for results
     */
    private void setupPlaceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location");
        String[] placeArray = {getResources().getString((R.string.location_private_place)), getResources().getString((R.string.location_select_from_map))};
        builder.setItems(placeArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        Intent intent = new Intent(EditorActivity.this, PrivatePlaceActivity.class);
                        startActivityForResult(intent, REQUEST_PRIVATE_PLACE);
                        break;
                    }
                    case 1: {
                        Intent intent = new Intent(EditorActivity.this, MapsActivity.class);
                        intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_MAP);
                        intent.putExtra(Constants.KEY_EDITOR_FOOD_ID, foodId);
                        startActivityForResult(intent, Constants.REQUEST_MAP);
                        break;
                    }
                }
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            hideKeyboard();
            finish();
            return true;
        }
        if (id == R.id.edit_menu_save) {
            hideKeyboard();

            /*
            If the food name is not entered, shows the toast
            and doesn't save the data
             */
            if (nameEditText.getText().toString().equals("")) {
                Toast.makeText(this, "Please enter the Food Name", Toast.LENGTH_SHORT).show();
            } else {
                saveFoodData();
                if (realm != null && realmChangeListener != null) {
                    realm.removeChangeListener(realmChangeListener);
                }
                finish();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Prevent the losing of entered data when screen is rotated
     *
     * @param outState
     * Data is stored in bundle to pass it when restored
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (currentPhotoPath != null) {
            outState.putString(KEY_INSTANCE_PHOTO, currentPhotoPath);
        }
        if (previousPhotoPath != null) {
            outState.putString(KEY_INSTANCE_PREVIOUS_PHOTO, previousPhotoPath);
        }
        if (nameEditText.getText() != null) {
            outState.putString(KEY_INSTANCE_NAME, nameEditText.getText().toString());
        }
        if (typeEditText.getText() != null) {
            outState.putString(KEY_INSTANCE_TYPE, typeEditText.getText().toString());
        }
        outState.putFloat(KEY_INSTANCE_RATING, ratingBar.getRating());
        if (dateTextView.getText() != null) {
            outState.putString(KEY_INSTANCE_DATE, dateTextView.getText().toString());
        }
        if (descriptionEditText.getText() != null) {
            outState.putString(KEY_INSTANCE_DESCRIPTION, descriptionEditText.getText().toString());
        }
        if (placeModel != null) {
            outState.putParcelable(KEY_INSTANCE_PLACE, placeModel);
        }

        if (dialog != null) {
            if (dialog.isShowing()) {
                outState.putInt(Constants.KEY_INSTANCE_DIALOG, 1);
                dialog.hide();
            }
        }
        if (datePickerDialog != null) {
            if (datePickerDialog.isVisible()) {
                outState.putInt(Constants.KEY_INSTANCE_DIALOG, 2);
                datePickerDialog.dismiss();
            }
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * After the screen is rotated, load the data saved in SaveInstanceState
     * @param savedInstanceState
     * Bring back the data from bundle
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentPhotoPath = savedInstanceState.getString(KEY_INSTANCE_PHOTO);
        previousPhotoPath = savedInstanceState.getString(KEY_INSTANCE_PREVIOUS_PHOTO);
        String name = savedInstanceState.getString(KEY_INSTANCE_NAME);
        String type = savedInstanceState.getString(KEY_INSTANCE_TYPE);
        float rating = savedInstanceState.getFloat(KEY_INSTANCE_RATING);
        String date = savedInstanceState.getString(KEY_INSTANCE_DATE);
        placeModel = savedInstanceState.getParcelable(KEY_INSTANCE_PLACE);

        if (name != null) {
            nameEditText.setText(name);
            nameEditText.setSelection(nameEditText.getText().length());
        }
        if (type != null) {
            typeEditText.setText(type);
        }
        ratingBar.setRating(rating);
        if (date != null) {
            dateTextView.setText(date);
        }
        if (placeModel != null) {
            placeConstraintLayout.setVisibility(View.VISIBLE);
            placeNameTextView.setText(placeModel.getPlaceName());
            placeAddressTextView.setText(placeModel.getAddress());
        }

        if (savedInstanceState.getInt(Constants.KEY_INSTANCE_DIALOG) == 1) {
            setupPlaceDialog();
        }
        if (savedInstanceState.getInt(Constants.KEY_INSTANCE_DIALOG) == 2) {
            setupDatePickerDialog();
        }
        loadPhoto();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    /**
     * Add a realm change listener for automatic UI updates
     */
    private void refresh() {
        realmChangeListener = new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                    }
                });
            }
        };
        realm.addChangeListener(realmChangeListener);
    }

    /**
     * hide the keyboard fragment
     */
    private void hideKeyboard() {
        Activity activity = EditorActivity.this;
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null && inputManager != null) {
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            inputManager.hideSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * load the data from Food model and enter the values in EditTexts accordingly
     */
    private void loadData() {
        placeConstraintLayout.setVisibility(View.GONE);
        if (foodId != 0) {
            nameEditText.setText(food.getName());
            nameEditText.setSelection(nameEditText.getText().length());
            typeEditText.setText(food.getFoodType());
            typeEditText.setSelection(typeEditText.getText().length());
            dateTextView.setText(food.getDate());
            descriptionEditText.setText(food.getDescription());
            ratingBar.setRating(food.getRating());
            currentPhotoPath = food.getPhotoPath();
            previousPhotoPath = currentPhotoPath;
            isFavorite = food.getFavorite();
            placeModel = food.getPlaceModel();
            showPlaceDetails();
        }
        loadPhoto();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (hasPhoto) {
            deletePhotoFile(currentPhotoPath);
        }
        if (realm != null && realmChangeListener != null) {
            realm.removeChangeListener(realmChangeListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && realmChangeListener != null) {
            realm.removeChangeListener(realmChangeListener);
            realm.close();
        }
    }

    /**
     * @param photoPath
     * Delete the file located in photoPath
     */
    private void deletePhotoFile(String photoPath) {
        boolean deleteSuccessful = new File(photoPath).delete();
        if (!deleteSuccessful) {
            Toast.makeText(context, "Error occuled. Delete failure", Toast.LENGTH_SHORT).show();
            Log.d(Constants.TAG_DELETE_LOG, "Delete failed");
        }
    }
}