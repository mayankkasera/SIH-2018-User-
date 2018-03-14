package dynamicdrillers.sih2018user;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ComplaintsFragment extends Fragment {


    private ViewPager viewPager;
    private TabLayout tabLayout;

    public ComplaintsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_complaints, container, false);

        viewPager = view.findViewById(R.id.complaint_viewpager);
        viewPager.setAdapter(new ComplaintsTabAdapter(getFragmentManager()));

        tabLayout = view.findViewById(R.id.complaint_tab);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

}
