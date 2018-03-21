package dynamicdrillers.sih2018user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = "Main2Activity";
    int PLACE_PICKER_REQUEST = 1;
    Double lat,lon;
    Place Myplace;
    static String complainerState=" ";
    static String complainerDistrict = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button click = (Button) findViewById(R.id.click);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                found = false;
            }
        });


    }


    int r = 1;
    boolean found = false;
    String id;
    void fun(){
        String SharedprefenceName = "USER_DATA";
        SharedPreferences sharedPreferences = getSharedPreferences(SharedprefenceName,Context.MODE_PRIVATE);
        sharedPreferences.getString("type",null);

        String s2 = sharedPreferences.getString("state",null);
        String s3 = sharedPreferences.getString("district",null);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("region_places").child(s2.toLowerCase())
                .child(s3.toLowerCase());

        GeoFire geoFire = new GeoFire(ref);
        //geoFire.setLocation("you",new GeoLocation(23.290864500235624,77.33559608459473));





        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(Myplace.getLatLng().latitude,Myplace.getLatLng().longitude),r);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Toast.makeText(getApplicationContext(),"as,mnfc",Toast.LENGTH_LONG).show();

                if(!found){
                    found = true;

                    id = key;
                    Toast.makeText(getApplicationContext(),id+" found "+r,Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Main2Activity.this,MainActivity.class));
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

                Toast.makeText(getApplicationContext(),location.latitude+""+location.longitude,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onGeoQueryReady() {
                if(!found&&r<100){
                    r++;
                    fun();
                    Toast.makeText(getApplicationContext(),r+"",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Toast.makeText(getApplicationContext(),r+"error",Toast.LENGTH_LONG).show();
            }
        });

    }


    private void getLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Myplace = PlacePicker.getPlace(data, this);
                Log.d(TAG, "onActivityResult: "+Myplace.getAddress());
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();


                if(Myplace.getAddress()!=null){
                    String address[] = Myplace.getAddress().toString().split(",");

                    TextView textView = findViewById(R.id.textView);
                    for(int i = 0 ; i<address.length;i++){
                        textView.setText(textView.getText()+address[i]+"\n");
                    }

                    if(address.length<=3){
                        Toast.makeText(Main2Activity.this, "select exact location", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String  s[] = address[address.length-2].split(" ");
                        textView.setText(textView.getText()+""+s.length+"\n");

                        if(s.length>3){
                            for(int j=0;j<s.length-1;j++)
                            {

                                complainerState = complainerState+s[j];
                                 complainerState = complainerState.trim().toLowerCase();


                            }
                        }
                        else{

                            for(int j=0;j<s.length;j++)
                            {

                                complainerState = complainerState+s[j];
                                complainerState = complainerState.trim().toLowerCase();

                            }

                        }



                        complainerDistrict = complainerDistrict+address[address.length-3];
                        complainerDistrict = complainerDistrict.trim().toLowerCase();
                         String SharedprefenceName = "USER_DATA";

                        SharedPreferences sharedPreferences = getSharedPreferences(SharedprefenceName, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("state",complainerState);
                        editor.putString("district",complainerDistrict);
                        editor.commit();
                        editor.apply();

                        textView.setText("mb"+complainerDistrict+complainerState+"mb");
                        fun();
                        complainerState= "";
                        complainerDistrict = "";


                    }



                }

            }
        }
    }


}
