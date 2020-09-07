package android.example.myfoodrecords.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.example.myfoodrecords.adapter.MyPagerAdapter;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.model.Food;
import android.example.myfoodrecords.utils.RealmHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    private MyPagerAdapter mFragmentAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    public Context context;
    private Realm realm;
    private RealmHelper helper;
    private AlertDialog dialog;

    private static final String KEY_INSTANCE_DIALOG = "keyDialog";
    private static final int KEY_DIALOG = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        setupRealm();
        setupUi();
    }

    private void setupRealm() {
        realm = Realm.getDefaultInstance();
        helper = new RealmHelper(realm);
    }

    private void setupUi() {

        getSupportActionBar().setTitle("Foods");
        // Get the ViewPager and apply the PagerAdapter
        mFragmentAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(mFragmentAdapter);

        // link the tabLayout and the viewpager together
        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        //checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_item_menu) {
            Intent intent = new Intent(this, EditorActivity.class);
            startActivity(intent);
        }

        if (id == R.id.delete_all) {
            setupDialog();
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dialog != null) {
            if (dialog.isShowing()) {
                outState.putInt(KEY_INSTANCE_DIALOG, KEY_DIALOG);
                dialog.hide();
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getInt(KEY_INSTANCE_DIALOG) == KEY_DIALOG) {
            setupDialog();
        }
    }

    private void setupDialog() {
        dialog = new AlertDialog.Builder(context)
                .setTitle("Delete All Food Items")
                .setMessage("Are you sure you want to delete all food items?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        deleteAllPhotoFiles();
                        helper.deleteAllFood();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteAllPhotoFiles() {
        helper.selectAllFoodsFromDb();
        List<Food> foodList = helper.retrieveAllFoodFromSelectedDb();
        for (int i = 0; i < foodList.size(); i++) {
            String photoPath = foodList.get(i).getPhotoPath();
            if (photoPath != null) {
                boolean deleteSuccessful = new File(photoPath).delete();
                if (!deleteSuccessful) {
                    Toast.makeText(context, "Error occuled. Deleted failure", Toast.LENGTH_SHORT).show();
                    Log.d(EditorActivity.TAG_DELETE_LOG, "Delete failed");
                }
            }
        }
    }
}