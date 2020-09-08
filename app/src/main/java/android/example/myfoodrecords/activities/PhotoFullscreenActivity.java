package android.example.myfoodrecords.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.example.myfoodrecords.PhotoAsyncResponse;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.utils.Constants;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class PhotoFullscreenActivity extends AppCompatActivity implements PhotoAsyncResponse {
    private String photoPath;
    private PhotoView fullScreenImageView;
    private final Context context = PhotoFullscreenActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_fullscreen);
        photoPath = getIntent().getStringExtra(Constants.KEY_FULL_SCREEN);
        fullScreenImageView = findViewById(R.id.photo_full_screen);

        hideActionAndStatusBar();

        loadPhoto();
    }

    private void loadPhoto() {
        if (!isFinishing() && !isDestroyed()) {
            Glide.with(context)
                    .load(photoPath)
                    .into(fullScreenImageView);
        }
    }

    /**
     * Hide the action bar and status bar
     */
    private void hideActionAndStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    @Override
    public void processFinish(Bitmap bitmap) {
        fullScreenImageView.setImageBitmap(bitmap);
    }
}
