package dynamicdrillers.sih2018user;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    Button button;
    private android.support.v7.widget.Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    ImageView profile_Icon;
    private ImageView drawer_open_icon;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    CircleImageView ProfileImage;
    TextView name,mobile,state;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile_Icon = findViewById(R.id.toolbar_profile_icon);
        drawerLayout = findViewById(R.id.drawer_main);
        drawer_open_icon = findViewById(R.id.navigation_icon);



        drawer_open_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerLayout.openDrawer(Gravity.START);
            }
        });

        navigationView = (NavigationView)findViewById(R.id.main_activity_nav);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case  R.id.navigation_profile:
                        startActivity(new Intent(MainActivity.this,ProfileActivity.class));
                        drawerLayout.closeDrawer(Gravity.START);
                        break;

                    case  R.id.navigation_logout:
                        Toast.makeText(getBaseContext(), "logout successfuly", Toast.LENGTH_SHORT).show();
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("token").setValue("");


                        mAuth.signOut();

                        goToLoginPage();
                        drawerLayout.closeDrawer(Gravity.START);
                        break;



                    case  R.id.navigation_aboutus:
                        Toast.makeText(getBaseContext(), "aboutus  item Clicked", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(Gravity.START);
                        break;



                }

                return true;

            }
        });


        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Your Shopping Host");





        viewPager = findViewById(R.id.main_viewpager);
        viewPager.setAdapter(new MainTabsAdapter(getSupportFragmentManager()));

        tabLayout = findViewById(R.id.main_tablayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        profile_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        //Checking User Is Logged In Or not;
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser==null)
        {
            goToLoginPage();
        }
        else
        {
            setNavigationItem();
        }


    }

    // Method For going on login page
    private void goToLoginPage() {

        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

    public  void setNavigationItem()

    {
        navigationView = (NavigationView)findViewById(R.id.main_activity_nav);
        View v = navigationView.getHeaderView(0);
        name = v.findViewById(R.id.navigation_header_name);
        mobile = v.findViewById(R.id.navigation_header_mobileno);
        state = v.findViewById(R.id.navigation_header_state);
        ProfileImage = v.findViewById(R.id.navigation_header_image);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference();

        mRoot.child("Users").child(mAuth.getCurrentUser().getUid().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Picasso.with(MainActivity.this).load(dataSnapshot.child("image").getValue().toString()).into(ProfileImage);
                name.setText(dataSnapshot.child("name").getValue().toString());
                mobile.setText("Mob :"+ dataSnapshot.child("mobile").getValue().toString());
                // state.setText(dataSnapshot.child(""));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
