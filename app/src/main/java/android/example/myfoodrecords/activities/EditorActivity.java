package android.example.myfoodrecords.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.example.myfoodrecords.BuildConfig;
import android.example.myfoodrecords.PhotoAsyncResponse;
import android.example.myfoodrecords.PhotoUtil;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.RealmHelper;
import android.example.myfoodrecords.model.Food;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

public class EditorActivity extends AppCompatActivity implements PhotoAsyncResponse {

    private Realm realm;
    private RealmHelper helper;
    private Food food;

    private EditText mNameEditText;
    private EditText mTypeEditText;
    private EditText mDateEditText;
    private EditText mRatingEditText;
    private EditText mLocationEditText;
    private ImageView mPhotoImageView;

    static final int REQUEST_TAKE_PHOTO = 1;
    private String currentPhotoPath;

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
        mPhotoImageView = findViewById(R.id.editor_food_iv);

        mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        if (id != 0) {
            mNameEditText.setText(food.getName());
            mTypeEditText.setText(food.getFoodType());
            mDateEditText.setText(food.getDate());
            mRatingEditText.setText(food.getRating());
            mLocationEditText.setText(food.getLocation());
            currentPhotoPath = food.getPhotoPath();
            loadPhoto();
        }

        Button saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nullCheck();
                if(!mNameEditText.getText().toString().equals("")) {
                    saveData();
                    finish();
                }
            }
        });
    }

    private void nullCheck() {
        if (mNameEditText.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter the Food Name", Toast.LENGTH_SHORT).show();
        }
    }

    // Saves the data model to Realm database
    private void saveData() {
        Food food = new Food();
        food.setId(id);
        food.setName(mNameEditText.getText().toString());
        food.setFoodType(mTypeEditText.getText().toString());
        food.setDate(mDateEditText.getText().toString());
        food.setRating(mRatingEditText.getText().toString());
        food.setLocation(mLocationEditText.getText().toString());
        food.setPhotoPath(currentPhotoPath);

        RealmHelper realmHelper = new RealmHelper(realm);
        realmHelper.insert(food);
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

/*
    public class getPhotoAsync extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {
            PhotoUtil photoUtil = new PhotoUtil(currentPhotoPath, false);
            return photoUtil.setPic();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mPhotoImageView.setImageBitmap(bitmap);
        }
    }

 */

}