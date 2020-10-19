package bo.young.myfoodrecords.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.example.myfoodrecords.R;

import bo.young.myfoodrecords.activities.MainActivity;
import bo.young.myfoodrecords.fragments.FavoriteFragment;
import bo.young.myfoodrecords.fragments.ItemViewFragment;
import bo.young.myfoodrecords.fragments.MovieFragment;
import bo.young.myfoodrecords.fragments.SummaryFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyPagerAdapter extends FragmentPagerAdapter {

    Context context;

    public MyPagerAdapter(FragmentManager supportFragmentManager, Context context) {
        super(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
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

            case 3:
                return new MovieFragment();

            default:
                return null;
        }
    }

    // Tab's label
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return context.getString(R.string.food_title);

            case 1:
                return context.getString(R.string.favorite_title);

            case 2:
                return  context.getString(R.string.summary_title);

            case 3:
                return  context.getString(R.string.movie_title);

            default:
                return null;
        }
    }

    // Total number of pages
    @Override
    public int getCount() {
        return 4;
    }
}
