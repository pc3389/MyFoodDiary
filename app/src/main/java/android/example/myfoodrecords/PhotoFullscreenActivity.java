package android.example.myfoodrecords;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.example.myfoodrecords.activities.DetailActivity;
import android.example.myfoodrecords.utils.PhotoUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class PhotoFullscreenActivity extends AppCompatActivity implements PhotoAsyncResponse {
    private String photoPath;
    private ImageView fullScreenImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_fullscreen);
        photoPath = getIntent().getStringExtra(DetailActivity.KEY_FULL_SCREEN);
        fullScreenImageView = findViewById(R.id.photo_full_screen);

        hideActionAndStatusBar();

        loadPhoto();
    }

    private void loadPhoto() {
        new PhotoUtil(photoPath, false);
        PhotoUtil.PhotoAsync photoAsync = new PhotoUtil.PhotoAsync();
        photoAsync.delegate = this;
        photoAsync.execute();
    }

    private void hideActionAndStatusBar() {
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    @Override
    public void processFinish(Bitmap bitmap) {
        fullScreenImageView.setImageBitmap(bitmap);
    }
}
