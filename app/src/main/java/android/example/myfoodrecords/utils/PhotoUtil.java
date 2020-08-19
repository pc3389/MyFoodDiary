package android.example.myfoodrecords.utils;

import android.example.myfoodrecords.PhotoAsyncResponse;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.activities.MainActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;

public class PhotoUtil {

    private static String currentPhotoPath;
    private static boolean isListView;

    public PhotoUtil(String currentPhotoPath, boolean isListView) {
        PhotoUtil.currentPhotoPath = currentPhotoPath;
        PhotoUtil.isListView = isListView;
    }

    public static class PhotoAsync extends AsyncTask<Void, Void, Bitmap> {
        public PhotoAsyncResponse delegate = null;

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return setPic();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            delegate.processFinish(bitmap);
        }
    }


    public static Bitmap setPic() {
        int targetW;
        int targetH;
        if(!isListView) {

            targetW = (int) MainActivity.context.getResources().getDimension(R.dimen.image_width);
            targetH = (int) MainActivity.context.getResources().getDimension(R.dimen.image_height);
        } else {
            targetW = (int) MainActivity.context.getResources().getDimension(R.dimen.image_width_list);
            targetH = (int) MainActivity.context.getResources().getDimension(R.dimen.image_height_list);
        }

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        Bitmap rotated = null;
        try {
            rotated =  rotateImageIfRequired(bitmap, currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotated;
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, String filePath) throws IOException {

        ExifInterface ei = new ExifInterface(Uri.parse(filePath).getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

}
