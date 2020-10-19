package bo.young.myfoodrecords.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import bo.young.myfoodrecords.model.Food;
import bo.young.myfoodrecords.adapter.MyPagerAdapter;
import android.example.myfoodrecords.R;

import bo.young.myfoodrecords.utils.Constants;
import bo.young.myfoodrecords.utils.RealmHelper;
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

    public Context context;
    private RealmHelper helper;
    private AlertDialog dialog;
    private Realm realm;

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

        getSupportActionBar().setTitle(getString(R.string.app_name));
        // Get the ViewPager and apply the PagerAdapter
        MyPagerAdapter mFragmentAdapter = new MyPagerAdapter(getSupportFragmentManager(), context);
        ViewPager mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(mFragmentAdapter);

        // link the tabLayout and the viewpager together
        TabLayout mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
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
            setupDeleteAllDialog();
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dialog != null) {
            if (dialog.isShowing()) {
                outState.putInt(Constants.KEY_INSTANCE_DIALOG, KEY_DIALOG);
                dialog.hide();
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getInt(Constants.KEY_INSTANCE_DIALOG) == KEY_DIALOG) {
            setupDeleteAllDialog();
        }
    }

    /**
     * Opens the dialog asking if the user still wants to delete the file
     * Implemented to prevent the mis-click
     */
    private void setupDeleteAllDialog() {
        dialog = new AlertDialog.Builder(context)
                .setTitle(getString(R.string.delete_all_items))
                .setMessage(getString(R.string.delete_all_description))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllPhotoFiles();
                        helper.deleteAllFood();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * When deleteing all the data from realm, delete all of the photo files
     */
    private void deleteAllPhotoFiles() {
        helper.selectAllFoodsFromDb();
        List<Food> foodList = helper.retrieveAllFoodFromSelectedDb();
        for (int i = 0; i < foodList.size(); i++) {
            String photoPath = foodList.get(i).getPhotoPath();
            if (photoPath != null) {
                boolean deleteSuccessful = new File(photoPath).delete();
                if (!deleteSuccessful) {
                    Toast.makeText(context, "Error occuled. Deleted failure", Toast.LENGTH_SHORT).show();
                    Log.d(Constants.TAG_DELETE_LOG, "Delete failed");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }
}