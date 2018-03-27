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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    int Got_Authority_Flag =0;
    String Region,USerToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_catagory_location);

        // Intializing Objects
        init();
        spotsDialog = new SpotsDialog(this);
        spotsDialog.setTitle("Searching nearest authority..");

        imagesUri = getIntent().getParcelableArrayListExtra("imagesuri");


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


                if(!gotLocationChoosed.getText().equals("") && Got_Authority_Flag==1)
                {


                    spotsDialog.show();
                    sendComplaintToDatabase();

                }
                else if( Got_Authority_Flag==2)
                {
                    Toast.makeText(ComplaintCatagoryLocation.this, "Service is Not Avaiable in your region", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ComplaintCatagoryLocation.this, "Please Choose Your Location...", Toast.LENGTH_SHORT).show();
                }


            }
        });







    }

    private void sendComplaintToDatabase() {



        final HashMap<String,String> complaint = new HashMap<>();

        complaint.put("complainer_id",mAuth.getCurrentUser().getUid().toString());
        complaint.put("complaint_type",ComplaintCatagoryS);
        complaint.put("complaint_description",ComplaintDescription.getText().toString());



        complaint.put("complainer_state",state);
        complaint.put("complaint_goesto",localAuthorityid);
        complaint.put("complaint_forwardto","default");
        complaint.put("complaint_votes","0");
        complaint.put("complaint_share","0");
        complaint.put("complaint_resolved_time","null");
        complaint.put("complaint_full_address",Myplace.getAddress().toString());
        complaint.put("complaint_status","pending");
        complaint.put("complaint_level","1");
        complaint.put("complaint_request_No_of_images", String.valueOf(imagesUri.size()));
        complaint.put("complaint_response_No_of_images","0");
        complaint.put("complaint_location_latitude", String.valueOf(Myplace.getLatLng().latitude));
        complaint.put("complaint_location_longitude", String.valueOf(Myplace.getLatLng().longitude));
        complaint.put("complaint_request_time",String.valueOf(123456));
        complaint.put("complaint_district",district);



        final String Complaintid = mRoot.child("complaints").push().getKey().toString();

        mRoot.child("complaints").child(Complaintid).setValue(complaint).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    mRoot.child("complaints").child(Complaintid).child("complaint_request_time").setValue(ServerValue.TIMESTAMP);


                    DatabaseReference reference  = FirebaseDatabase.getInstance().getReference().child("region_admin").child(localAuthorityid);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Region  =  dataSnapshot.child("region").getValue().toString();
                            mRoot.child("complaints").child(Complaintid).child("complainer_region").setValue(Region.toLowerCase());
                            Toast.makeText(ComplaintCatagoryLocation.this, Region, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mRoot.child("time").child("t").setValue(ServerValue.TIMESTAMP);
                    mRoot.child("time").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Long time =  Long.parseLong(dataSnapshot.child("t").getValue().toString());
                            mRoot.child("complaints").child(Complaintid).child("complaint_request_time").setValue(time.toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                     for (int i = 0;i<imagesUri.size();i++ )
                    {
                        final int finalI = i;
                        Uri img_uri = Uri.fromFile(new File(String.valueOf(imagesUri.get(i))));
                        mStorage.child("complaints").child(Complaintid+"_request_"+(i+1)+".jpg").putFile(img_uri)
                                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                        HashMap<String,String> data = new HashMap<>();
                                        data.put("image",task.getResult().getDownloadUrl().toString());
                                        data.put("type","request");

                                        mRoot.child("complaints").child(Complaintid).child("images").push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {


                                            }
                                        });


                                    }
                                });
                    }
                    spotsDialog.dismiss();


                    FirebaseDatabase.getInstance().getReference().child("region_admin").child(localAuthorityid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                           USerToken =  dataSnapshot.child("token").getValue().toString();
                            Intent i = new Intent(ComplaintCatagoryLocation.this,ComplaintSuccessFullyActivity.class);
                            i.putExtra("complaintid",Complaintid);
                            i.putExtra("token",USerToken);
                            i.putExtra("catagory",ComplaintCatagoryS);
                            i.putExtra("address",Myplace.getAddress().toString());
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


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
                    Got_Authority_Flag = 1;
                    SubmitComplaint.setEnabled(true);
                    spotsDialog.dismiss();

                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {



            }

            @Override
            public void onGeoQueryReady() {
                if(!found&&r<100){
                    r  = r+5;
                    fun();
                    spotsDialog.show();
                }
                if(r>100)
                {
                    Got_Authority_Flag = 2;
                    Toast.makeText(ComplaintCatagoryLocation.this, "Services Are Not available at your place", Toast.LENGTH_SHORT).show();
                    spotsDialog.dismiss();
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

        complainerState= "";
        complainerDistrict = "";

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                spotsDialog.show();
                Myplace = PlacePicker.getPlace(data, this);
               Toast.makeText(this, "", Toast.LENGTH_SHORT).show();


               gotLocationChoosed.setText(Myplace.getAddress());
                if(Myplace.getAddress()!=null){
                    String address[] = Myplace.getAddress().toString().split(",");

                    if (address.length <= 3) {
                        Toast.makeText(ComplaintCatagoryLocation.this, "select exact location", Toast.LENGTH_SHORT).show();

                    } else {
                        String state  = address[address.length - 2].replaceAll("\\d","");

                        String dis_s[] = address[address.length-3].split(" ");
                        for (int j = 0; j < dis_s.length; j++) {

                            complainerDistrict = complainerDistrict + dis_s[j];


                        }

                        String st_s[] = state.split(" ");

                        for (int j = 0; j < st_s.length; j++) {

                            complainerState = complainerState + st_s[j];


                        }

                        complainerDistrict =  complainerDistrict.trim().toLowerCase();
                        complainerState = complainerState.trim().toLowerCase();

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