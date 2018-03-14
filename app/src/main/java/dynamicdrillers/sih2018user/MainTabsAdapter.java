package dynamicdrillers.sih2018user;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Mayank on 13-03-2018.
 */

public class MainTabsAdapter extends FragmentPagerAdapter {
    public MainTabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position)
        {
            case 2:
                return fragment = new NotificationsFragment();
            case 0:
                return fragment = new HomeFragment();
            case 1:
                return fragment = new ComplaintsFragment();
            default:
                return null;
        }


    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int Position)
    {
        switch (Position)
        {
            case 1:
                return "Complaints";
            case 0:
                return "Home";
            case 2:
                return "Notifications";

            default:
                return null;
        }
    }

}
