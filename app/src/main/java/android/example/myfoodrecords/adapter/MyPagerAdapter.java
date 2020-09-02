package android.example.myfoodrecords.adapter;

import android.example.myfoodrecords.activities.MainActivity;
import android.example.myfoodrecords.fragments.SummaryFragment;
import android.example.myfoodrecords.fragments.FavoriteFragment;
import android.example.myfoodrecords.fragments.ItemViewFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyPagerAdapter extends FragmentPagerAdapter {

    public MyPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new ItemViewFragment();

            case 1:
                return new FavoriteFragment();

            case 2:
                return new SummaryFragment();

            default:
                return null;
        }
    }

    // Will be displayed as the tab's label
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return "Foods";

            case 1:
                return "Favorite";

            case 2:
                return "Summary";

            default:
                return null;
        }
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return 3;
    }
}
