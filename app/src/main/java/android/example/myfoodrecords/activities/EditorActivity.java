package android.example.myfoodrecords.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.example.myfoodrecords.BuildConfig;
import android.example.myfoodrecords.adapter.PrivatePlaceAdapter;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.utils.RealmHelper;
import android.example.myfoodrecords.model.Food;
import android.example.myfoodrecords.model.PlaceModel;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private RealmChangeListener realmChangeListener;

    private Food food;
    private PlaceModel placeModel;

    private final Context context = EditorActivity.this;

    private EditText mNameEditText;
    private EditText mTypeEditText;
    private EditText mDescriptionEditText;
    private TextView mDateTextView;
    private RatingBar mRatingBar;
    private TextView mLocationTextView;
    private ImageView mPhotoImageView;
    private TextView mPlaceNameTextView;
    private TextView mPlaceAddressTextView;
    private LinearLayout linearLayoutPlace;

    static final int REQUEST_TAKE_PHOTO = 1;
    public static final String KEY_REQUEST_CODE = "requestCode";
    public static final String KEY_EDITOR_FOOD_ID = "foodId2";
    private static final String KEY_INSTANCE_PHOTO = "savein";
    private static final String KEY_INSTANCE_ADDRESS = "keyAddress";
    private static final String KEY_INSTANCE_PLACE_NAME = "keyPlaceName";
    private static final String KEY_INSTANCE_DATE = "keyDate";
    private static final String KEY_INSTANCE_NAME = "keyName";
    private static final String KEY_INSTANCE_TYPE = "keyType";
    private static final String KEY_INSTANCE_RATING = "keyRating";
    private static final String KEY_INSTANCE_DESCRIPTION = "keyDescription";
    public static final String KEY_INSTANCE_DIALOG = "keyDialog";
    public static final int REQUEST_MAP = 2;
    public static final int RESULT_MAP = 3;
    public static final int REQUEST_PRIVATE_PLACE = 4;
    public static final String NO_TYPE = "No Type";

    private DatePickerDialog datePickerDialog;
    private int Year, Month, Day;
    private Calendar calendar;
    private boolean isFavorite;
    private String date;

    private String currentPhotoPath = null;
    private String previousPhotoPath;
    private AlertDialog dialog;

    private int foodId = 0;
    private File photoFile;


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

        mNameEditText = findViewById(R.id.editor_food_name_edit);
        mTypeEditText = findViewById(R.id.editor_food_type_edit);
        mDescriptionEditText = findViewById(R.id.editor_description_edit);
        mDateTextView = findViewById(R.id.editor_date_edit);
        mDateTextView.setText(date);
        mRatingBar = findViewById(R.id.editor_rating_edit);
        mLocationTextView = findViewById(R.id.push_to_edit_image);
        mLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDialog();
            }
        });
        mPhotoImageView = findViewById(R.id.editor_food_iv);
        mPlaceNameTextView = findViewById(R.id.editor_place_name_tv);
        mPlaceAddressTextView = findViewById(R.id.editor_place_address_tv);
        linearLayoutPlace = findViewById(R.id.linear_layout_place);

        mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        loadData();
        refresh();
    }

    private void datePickerSetUp() {
        mDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDatePickerDialog();
            }
        });
    }

    private void setupDatePickerDialog() {
        todayDate();
        datePickerDialog = DatePickerDialog.newInstance(EditorActivity.this, Year, Month, Day);
        datePickerDialog.setThemeDark(false);
        datePickerDialog.showYearPickerFirst(false);
        datePickerDialog.setTitle("Date Picker");

        datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
    }

    private void todayDate() {
        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        date = Day + "/" + (Month + 1) + "/" + Year;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        Toast.makeText(EditorActivity.this, date, Toast.LENGTH_LONG).show();
        mDateTextView.setText(date);
    }

    // Saves the data model to Realm database
    private void saveFoodData() {
        Food food = new Food();
        food.setId(foodId);
        food.setName(mNameEditText.getText().toString());
        food.setFoodType(mTypeEditText.getText().toString());
        if (mTypeEditText.getText().toString().matches("")) {
            food.setFoodType(NO_TYPE);
        }
        food.setDescription(mDescriptionEditText.getText().toString());
        food.setDate(mDateTextView.getText().toString());
        food.setRating(mRatingBar.getRating());
        food.setPhotoPath(currentPhotoPath);
        food.setPlaceModel(placeModel);
        food.setFavorite(isFavorite);

        helper.insertFood(food);
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            loadPhoto();
            previousPhotoPath = currentPhotoPath;
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_CANCELED) {
            currentPhotoPath = previousPhotoPath;
            loadPhoto();
        } else if (requestCode == REQUEST_MAP && resultCode == RESULT_MAP) {
            placeModel = realm.where(PlaceModel.class).equalTo("id", data.getIntExtra(MapsActivity.PLACE_ID_KEY, 0)).findFirst();
            if (placeModel != null) {
                linearLayoutPlace.setVisibility(View.VISIBLE);
                mPlaceNameTextView.setText(placeModel.getPlaceName());
                mPlaceAddressTextView.setText(placeModel.getAddress());
            }
        } else if (requestCode == REQUEST_PRIVATE_PLACE && resultCode == PrivatePlaceAdapter.RESULT_PRIVATE_PLACE) {
            placeModel = realm.where(PlaceModel.class).equalTo("id", data.getIntExtra(PrivatePlaceAdapter.PUT_PLACE_ID, 0)).findFirst();
            if (placeModel != null) {
                linearLayoutPlace.setVisibility(View.VISIBLE);
                mPlaceNameTextView.setText(placeModel.getPlaceName());
                mPlaceAddressTextView.setText(placeModel.getAddress());
            }
        }
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        photoFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = photoFile.getAbsolutePath();
        return photoFile;
    }

    private void setupDialog() {
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
                        intent.putExtra(KEY_REQUEST_CODE, REQUEST_MAP);
                        intent.putExtra(KEY_EDITOR_FOOD_ID, foodId);
                        startActivityForResult(intent, REQUEST_MAP);
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

            if (mNameEditText.getText().toString().equals("")) {
                Toast.makeText(this, "Please enter the Food Name", Toast.LENGTH_SHORT).show();
            } else {
                saveFoodData();
                realm.removeChangeListener(realmChangeListener);
                finish();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (currentPhotoPath != null) {
            outState.putString(KEY_INSTANCE_PHOTO, currentPhotoPath);
        }
        if (mNameEditText.getText() != null) {
            outState.putString(KEY_INSTANCE_NAME, mNameEditText.getText().toString());
        }
        if (mTypeEditText.getText() != null) {
            outState.putString(KEY_INSTANCE_TYPE, mTypeEditText.getText().toString());
        }
        outState.putFloat(KEY_INSTANCE_RATING, mRatingBar.getRating());
        if (mDateTextView.getText() != null) {
            outState.putString(KEY_INSTANCE_DATE, mDateTextView.getText().toString());
        }
        if (mDescriptionEditText.getText() != null) {
            outState.putString(KEY_INSTANCE_DESCRIPTION, mDescriptionEditText.getText().toString());
        }
        if (mPlaceAddressTextView.getText() != null) {
            outState.putString(KEY_INSTANCE_ADDRESS, mPlaceAddressTextView.getText().toString());
        }
        if (mPlaceNameTextView.getText() != null) {
            outState.putString(KEY_INSTANCE_PLACE_NAME, mPlaceNameTextView.getText().toString());
        }
        if (dialog != null) {
            if (dialog.isShowing()) {
                outState.putInt(KEY_INSTANCE_DIALOG, 1);
                dialog.hide();
            }
        }
        if (datePickerDialog != null) {
            if (datePickerDialog.isVisible()) {
                outState.putInt(KEY_INSTANCE_DIALOG, 2);
                datePickerDialog.dismiss();
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentPhotoPath = savedInstanceState.getString(KEY_INSTANCE_PHOTO);
        String name = savedInstanceState.getString(KEY_INSTANCE_NAME);
        String type = savedInstanceState.getString(KEY_INSTANCE_TYPE);
        float rating = savedInstanceState.getFloat(KEY_INSTANCE_RATING);
        String date = savedInstanceState.getString(KEY_INSTANCE_DATE);
        String placeName = savedInstanceState.getString(KEY_INSTANCE_PLACE_NAME);
        String address = savedInstanceState.getString(KEY_INSTANCE_ADDRESS);

        if (name != null) {
            mNameEditText.setText(name);
        }
        if (type != null) {
            mTypeEditText.setText(type);
        }
        mRatingBar.setRating(rating);
        if (date != null) {
            mDateTextView.setText(date);
        }
        if (placeName != null) {
            mPlaceNameTextView.setText(placeName);
        }
        if (address != null) {
            mPlaceAddressTextView.setText(address);
        }
        if (savedInstanceState.getInt(KEY_INSTANCE_DIALOG) == 1) {
            setupDialog();
        }
        if (savedInstanceState.getInt(KEY_INSTANCE_DIALOG) == 2) {
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

    private void refresh() {
        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                loadData();
            }
        };
        realm.addChangeListener(realmChangeListener);
    }

    private void hideKeyboard() {
        Activity activity = EditorActivity.this;
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null && inputManager != null) {
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            inputManager.hideSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void loadData() {
        if (foodId != 0) {
            mNameEditText.setText(food.getName());
            mNameEditText.setSelection(mNameEditText.getText().length());
            mTypeEditText.setText(food.getFoodType());
            mTypeEditText.setSelection(mTypeEditText.getText().length());
            mDateTextView.setText(food.getDate());
            mDescriptionEditText.setText(food.getDescription());
            mRatingBar.setRating(food.getRating());
            currentPhotoPath = food.getPhotoPath();
            previousPhotoPath = currentPhotoPath;
            isFavorite = food.getFavorite();
            placeModel = food.getPlaceModel();
            if (placeModel != null) {
                linearLayoutPlace.setVisibility(View.VISIBLE);
                mPlaceNameTextView.setText(placeModel.getPlaceName());
                mPlaceAddressTextView.setText(placeModel.getAddress());
            } else {
                linearLayoutPlace.setVisibility(View.GONE);
            }
        }
        loadPhoto();
    }
}