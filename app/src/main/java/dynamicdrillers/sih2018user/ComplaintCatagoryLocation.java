package dynamicdrillers.sih2018user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class ComplaintCatagoryLocation extends AppCompatActivity {

    private static final String TAG = "ComplaintCatagoryLocati";
    Spinner ComplaintCatagory;
    Button ChooseLocation;
    EditText ComplaintDescription;
    TextView ChoosedCatagoryString,gotLocationChoosed;
    LinearLayout SubmitComplaint;
    String []catagory = {"catagory1","catagory2","catagory3","catagory4","catagory5"};
    int PLACE_PICKER_REQUEST = 1;
    Double lat,lon;
    Place Myplace;
    static String complainerState=" ";
    static String complainerDistrict = " ";
    int r = 2;
    boolean found = false;
    String localAuthorityid;
    Intent intent;
    Bundle bundle;
    ArrayList<Uri> imagesUri;
    String ComplaintCatagoryS;
    SpotsDialog spotsDialog ;
    DatabaseReference mRoot;
    FirebaseAuth mAuth;
    StorageReference mStorage;
    String state,district;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_catagory_location);

        // Intializing Objects
        init();
        spotsDialog = new SpotsDialog(this);

        //getting Images from INtent
        intent = getIntent();
        bundle = intent.getBundleExtra("BUNDLE");

        if(bundle!=null)
        {
          imagesUri = (ArrayList<Uri>) bundle.getSerializable("ARRAYLIST");
            Toast.makeText(this, imagesUri.size()+"", Toast.LENGTH_SHORT).show();
        }








        ArrayAdapter<String> ArrayAdaptorcatagory = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,catagory);
        ComplaintCatagory.setAdapter(ArrayAdaptorcatagory);

        ComplaintCatagory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ComplaintCatagoryS = catagory[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //getting User Location;

        ChooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLocation();
                found = false;

            }
        });

        // Submiting Complaint to database
        SubmitComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendComplaintToDatabase();
            }
        });







    }

    private void sendComplaintToDatabase() {

        HashMap<String,String> complaint = new HashMap<>();
        complaint.put("complainer_id",mAuth.getCurrentUser().getUid().toString());
        complaint.put("complaint_request_time", String.valueOf(ServerValue.TIMESTAMP));
        complaint.put("complaint_type",ComplaintCatagoryS);
        complaint.put("complaint_description",ComplaintDescription.getText().toString());
        complaint.put("complainer_region",district);
        complaint.put("complainer_state",state);
        complaint.put("complaint_goesto",localAuthorityid);
        complaint.put("complaint_forwardto","default");
        complaint.put("complaint_votes","0");
        complaint.put("complaint_share","0");
        complaint.put("complaint_resolved_time","null");
        complaint.put("complaint_full_address",Myplace.getAddress().toString());
        complaint.put("complaint_status","pending");
        complaint.put("complaint_level","1");

        final String Complaintid = mRoot.child("complaints").push().getKey();


        mRoot.child("complaints").child(Complaintid).setValue(complaint).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    int noOfImages = 0;
                    noOfImages = getIntent().getIntExtra("noofimages",0);
                    Toast.makeText(ComplaintCatagoryLocation.this,noOfImages+" " , Toast.LENGTH_SHORT).show();


                    for (int i = 0;i<noOfImages;i++ )
                    {
                        final int finalI = i;
                        mStorage.child("complaints").child(Complaintid+"_request_"+(i+1)+".jpg").putFile(imagesUri.get(i))
                                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                        Toast.makeText(ComplaintCatagoryLocation.this, "image " + finalI + "uploaded", Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                }
            }
        });

        mRoot.child("complaints").child(Complaintid).setValue(complaint).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    Toast.makeText(ComplaintCatagoryLocation.this, "Your Complaint Is Registred..", Toast.LENGTH_SHORT).show();
                }
            }
        });










    }


    public  void init()
    {

        ComplaintCatagory = findViewById(R.id.complaintCatagory);
        ChoosedCatagoryString = findViewById(R.id.gotLocation_complaint_textview);
        ChooseLocation = findViewById(R.id.chooseLocation_complaint_btn);
        ComplaintDescription = findViewById(R.id.description_compliant_edittext);
        SubmitComplaint = findViewById(R.id.submitComplaint_complaint_LinearLayout);
        gotLocationChoosed = findViewById(R.id.gotLocation_complaint_textview);
        SubmitComplaint.setEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mRoot = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();


    }

    void fun(){
        String SharedprefenceName = "USER_DATA";
        SharedPreferences sharedPreferences = getSharedPreferences(SharedprefenceName, Context.MODE_PRIVATE);
        sharedPreferences.getString("type",null);

        state = sharedPreferences.getString("state",null);
        district = sharedPreferences.getString("district",null);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("region_places").child(state.toLowerCase())
                .child(district.toLowerCase());

        GeoFire geoFire = new GeoFire(ref);
        //geoFire.setLocation("you",new GeoLocation(23.290864500235624,77.33559608459473));





        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(Myplace.getLatLng().latitude,Myplace.getLatLng().longitude),r);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if(!found){
                    found = true;
                    localAuthorityid = key;
                    Toast.makeText(getApplicationContext(),"Yes Services are available at your location",Toast.LENGTH_SHORT).show();
                    SubmitComplaint.setEnabled(true);
                    spotsDialog.dismiss();

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
                    r  = r+2;
                    fun();
                    spotsDialog.show();
                }
                else
                {
                    Toast.makeText(ComplaintCatagoryLocation.this, "Services Are Not available at your place", Toast.LENGTH_SHORT).show();
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
                spotsDialog.show();
                Myplace = PlacePicker.getPlace(data, this);
               Toast.makeText(this, "", Toast.LENGTH_SHORT).show();


               gotLocationChoosed.setText(Myplace.getAddress());
                if(Myplace.getAddress()!=null){
                    String address[] = Myplace.getAddress().toString().split(",");



                    if(address.length<=3){
                        Toast.makeText(ComplaintCatagoryLocation.this, "select exact location", Toast.LENGTH_SHORT).show();
                        spotsDialog.dismiss();
                    }
                    else{
                        String  s[] = address[address.length-2].split(" ");


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


                        fun();
                        complainerState= "";
                        complainerDistrict = "";


                    }



                }

            }
        }
    }


}