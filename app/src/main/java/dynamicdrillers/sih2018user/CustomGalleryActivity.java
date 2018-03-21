package dynamicdrillers.sih2018user;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import com.gun0912.tedpicker.ImagePickerActivity;

import java.io.Serializable;
import java.security.Permission;
import java.util.ArrayList;

public class CustomGalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_gallery);

        checkPermition();
    }

    void checkPermition(){
        String s[]={Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE};
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getImages();
            }
            else{
                ActivityCompat.requestPermissions(this,s,123);
            }
        }
        else{
            ActivityCompat.requestPermissions(this,s,123);
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

        Intent intent  = new Intent(this, ImagePickerActivity.class);
        startActivityForResult(intent,INTENT_REQUEST_GET_IMAGES);

    }

    @Override
    protected void onActivityResult(int requestCode, int resuleCode, Intent intent) {
        super.onActivityResult(requestCode, resuleCode, intent);

        if (requestCode == INTENT_REQUEST_GET_IMAGES && resuleCode == Activity.RESULT_OK ) {

            ArrayList<Uri> image_uris = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            Toast.makeText(this, image_uris.toString(), Toast.LENGTH_SHORT).show();

            Intent intent1 = new Intent(CustomGalleryActivity.this,ComplaintCatagoryLocation.class);


            Bundle args = new Bundle();
            args.putSerializable("ARRAYLIST",(Serializable)image_uris);
            intent.putExtra("BUNDLE",args);


            startActivity(intent1);
            //do something//
        }
    }
}
