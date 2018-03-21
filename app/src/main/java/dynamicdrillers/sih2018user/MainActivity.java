package dynamicdrillers.sih2018user;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button button;
    private android.support.v7.widget.Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Your Shopping Host");





        viewPager = findViewById(R.id.main_viewpager);
        viewPager.setAdapter(new MainTabsAdapter(getSupportFragmentManager()));

        tabLayout = findViewById(R.id.main_tablayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        //Checking User Is Logged In Or not;
//        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
//        if(mUser==null)
//        {
//            goToLoginPage();
//        }
//
//
//    }

    // Method For going on login page
    private void goToLoginPage() {

        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }
}
