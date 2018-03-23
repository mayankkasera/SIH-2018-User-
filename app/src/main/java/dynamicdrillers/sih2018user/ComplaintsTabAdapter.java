package dynamicdrillers.sih2018user;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Mayank on 14-03-2018.
 */

public class ComplaintsTabAdapter extends FragmentPagerAdapter {


    public ComplaintsTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position)
        {
            case 0:
                return fragment = new YourComplaintsFragment();
            case 1:
                return fragment = new NearbyComplaintsFragment();
            default:
                return null;
        }


    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int Position)
    {
        switch (Position)
        {

            case 0:
                return "Your's Complaints";
            case 1:
                return "Near By Complaints";


            default:
                return null;
        }
    }

}
