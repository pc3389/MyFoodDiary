package android.example.myfoodrecords.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.example.myfoodrecords.BuildConfig;
import android.example.myfoodrecords.PhotoAsyncResponse;
import android.example.myfoodrecords.PhotoUtil;
import android.example.myfoodrecords.PrivatePlaceAdapter;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.RealmHelper;
import android.example.myfoodrecords.model.Food;
import android.example.myfoodrecords.model.PlaceModel;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;

public class EditorActivity extends AppCompatActivity implements PhotoAsyncResponse, DatePickerDialog.OnDateSetListener {

    private Realm realm;
    private RealmHelper helper;

    private Food food;
    private PlaceModel placeModel;

    private EditText mNameEditText;
    private EditText mTypeEditText;
    private EditText mDateEditText;
    private RatingBar mRatingBar;
    private Spinner mLocationSpinner;
    private ImageView mPhotoImageView;
    private TextView mPlaceNameTextView;
    private TextView mPlaceAddressTextView;

    static final int REQUEST_TAKE_PHOTO = 1;
    public static final String KEY_REQUEST_CODE = "requestCode";
    public static final String KEY_EDITOR_FOOD_ID = "foodId2";
    public static final int REQUEST_MAP = 2;
    public static final int RESULT_MAP = 3;
    public static final int REQUEST_PRIVATE_PLACE = 4;

    private DatePickerDialog datePickerDialog;
    private int Year, Month, Day;
    private Calendar calendar;

    private String currentPhotoPath;

    int foodId = 0;


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

        mNameEditText = findViewById(R.id.editor_food_name_edit);
        mTypeEditText = findViewById(R.id.editor_food_type_edit);
        mDateEditText = findViewById(R.id.editor_date_edit);
        mRatingBar = findViewById(R.id.editor_rating_edit);
        mLocationSpinner = findViewById(R.id.editor_location_spinner);
        setupSpinner();
        mPhotoImageView = findViewById(R.id.editor_food_iv);
        mPlaceNameTextView = findViewById(R.id.editor_place_name_tv);
        mPlaceAddressTextView = findViewById(R.id.editor_place_address_tv);

        mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        if (foodId != 0) {
            mNameEditText.setText(food.getName());
            mTypeEditText.setText(food.getFoodType());
            mDateEditText.setText(food.getDate());
            mRatingBar.setRating(food.getRating());
            currentPhotoPath = food.getPhotoPath();
            if (currentPhotoPath != null) {
                loadPhoto();
            }
            placeModel = food.getPlaceModel();
            if (placeModel != null) {
                mPlaceNameTextView.setText(placeModel.getPlaceName());
                mPlaceAddressTextView.setText(placeModel.getAddress());
            }
        }

        Button saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nullCheck();
                if (!mNameEditText.getText().toString().equals("")) {
                    saveFoodData();
                    finish();
                }
            }
        });
    }

    private void datePickerSetUp() {
        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                Year = calendar.get(Calendar.YEAR);
                Month = calendar.get(Calendar.MONTH);
                Day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = DatePickerDialog.newInstance(EditorActivity.this, Year, Month, Day);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setTitle("Date Picker");

                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                        Toast.makeText(EditorActivity.this, "Datepicker Canceled", Toast.LENGTH_SHORT).show();
                    }
                });

                datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        Toast.makeText(EditorActivity.this, date, Toast.LENGTH_LONG).show();
        mDateEditText.setText(date);
    }

    private void nullCheck() {
        if (mNameEditText.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter the Food Name", Toast.LENGTH_SHORT).show();
        }
    }

    // Saves the data model to Realm database
    private void saveFoodData() {
        Food food = new Food();
        food.setId(foodId);
        food.setName(mNameEditText.getText().toString());
        food.setFoodType(mTypeEditText.getText().toString());
        food.setDate(mDateEditText.getText().toString());
        food.setRating(mRatingBar.getRating());
        food.setPhotoPath(currentPhotoPath);
        food.setPlaceModel(placeModel);

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
        } else if (requestCode == REQUEST_MAP && resultCode == RESULT_MAP) {
            placeModel = realm.where(PlaceModel.class).equalTo("id", data.getIntExtra(MapsActivity.PLACE_ID_KEY, 0)).findFirst();
            mPlaceNameTextView.setText(placeModel.getPlaceName());
            mPlaceAddressTextView.setText(placeModel.getAddress());
        } else if (requestCode == REQUEST_PRIVATE_PLACE && resultCode == PrivatePlaceAdapter.RESULT_PRIVATE_PLACE) {
            placeModel = realm.where(PlaceModel.class).equalTo("id", data.getIntExtra(PrivatePlaceAdapter.PUT_PLACE_ID, 0)).findFirst();
            mPlaceNameTextView.setText(placeModel.getPlaceName());
            mPlaceAddressTextView.setText(placeModel.getAddress());
        }
    }

    private void loadPhoto() {
        new PhotoUtil(currentPhotoPath, false);
        PhotoUtil.PhotoAsync photoAsync = new PhotoUtil.PhotoAsync();
        photoAsync.delegate = this;
        photoAsync.execute();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void processFinish(Bitmap bitmap) {
        mPhotoImageView.setImageBitmap(bitmap);
    }

    private void setupSpinner() {

        ArrayAdapter locationSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_location_options, android.R.layout.simple_spinner_item);
        locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mLocationSpinner.setAdapter(locationSpinnerAdapter);
        mLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.spinner_not_selected))) {

                    } else if (selection.equals(getString(R.string.location_private_place))) {
                        Intent intent = new Intent(EditorActivity.this, PrivatePlaceActivity.class);
                        startActivityForResult(intent, REQUEST_PRIVATE_PLACE);
                    } else if (selection.equals(getString(R.string.location_select_from_map))) {
                        Intent intent = new Intent(EditorActivity.this, MapsActivity.class);
                        intent.putExtra(KEY_REQUEST_CODE, REQUEST_MAP);
                        intent.putExtra(KEY_EDITOR_FOOD_ID, foodId);
                        startActivityForResult(intent, REQUEST_MAP);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}