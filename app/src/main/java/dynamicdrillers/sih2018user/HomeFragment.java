package dynamicdrillers.sih2018user;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;

import java.io.Serializable;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment {

    Button Registor_Button;
    int PLACE_PICKER_REQUEST = 1;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment

        Registor_Button = view.findViewById(R.id.register_home_btn);
        Registor_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkPermissions();
            }
        });
        return view;
    }



    void checkPermissions(){
        String s[]={android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE};
        if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getImages();
            }
            else{
                ActivityCompat.requestPermissions((Activity) getContext(),s,123);
            }
        }
        else{
            ActivityCompat.requestPermissions((Activity) getContext(),s,123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 123:
            {
                if(grantResults.length>0){

                    getImages();
                }
            }
        }
    }

    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    private void getImages() {

        Config config = new Config();
        config.setSelectionMin(1);
        config.setSelectionLimit(4);
        ImagePickerActivity.setConfig(config);
        Intent intent  = new Intent(getContext(), ImagePickerActivity.class);
        startActivityForResult(intent,INTENT_REQUEST_GET_IMAGES);

    }

    @Override
    public void onActivityResult(int requestCode, int resuleCode, Intent intent) {
        super.onActivityResult(requestCode, resuleCode, intent);

        if (requestCode == INTENT_REQUEST_GET_IMAGES && resuleCode == Activity.RESULT_OK ) {


            ArrayList<Uri> image_uris = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
           // Toast.makeText(getContext(), image_uris.size()+" ", Toast.LENGTH_SHORT).show();

            Intent intent1 = new Intent(getContext(),ComplaintCatagoryLocation.class);
            intent1.putExtra("imagesuri",image_uris);

            startActivity(intent1);
            //do something//
        }
    }





   
}
