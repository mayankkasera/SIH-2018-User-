package dynamicdrillers.sih2018user;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class ProfileActivity extends AppCompatActivity {

    Button name,phone,edit_cancel,edit_change;
    TextView resolved,pending;
    ImageView edit_name,editname;
    CircleImageView profile;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference();
    String Image;
    SpotsDialog spotsDialog;
    TextInputEditText nameToChangeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        spotsDialog = new SpotsDialog(this);
    name = findViewById(R.id.profile_name);
    phone = findViewById(R.id.phn_profile_button);
    resolved = findViewById(R.id.resolved_profile_textview);
    pending  = findViewById(R.id.pending_profile_textview);
    profile = findViewById(R.id.profile_id);
    edit_name = findViewById(R.id.editName_profile_imageview);


      //  Query query = FirebaseDatabase.getInstance().getReference().child("complaints").orderByChild()

    
    
    edit_name.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            final Dialog dialog = new Dialog(ProfileActivity.this);
            dialog.setContentView(R.layout.name_change_dialog);
            dialog.setTitle("Do you want to delete ?");
            dialog.show();

            edit_cancel = dialog.findViewById(R.id.name_dialog_btn_cencel);
            edit_change = dialog.findViewById(R.id.name_dialog_btn_chnge);
            nameToChangeText = dialog.findViewById(R.id.newnametext);

            edit_change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if(!nameToChangeText.getText().equals(""))
                    {


                        mRoot.child("Users").child(mAuth.getCurrentUser().getUid().toString()).child("name").setValue(nameToChangeText.getText().toString());
                        Toast.makeText(ProfileActivity.this, "Name Changed Successfuly", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                    else
                    {
                        Toast.makeText(ProfileActivity.this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            edit_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();
                }
            });





        }
    });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);


            }
        });


    mRoot.child("Users").child(mAuth.getCurrentUser().getUid().toString()).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            name.setText(dataSnapshot.child("name").getValue().toString());
            phone.setText(dataSnapshot.child("mobile").getValue().toString());
            resolved.setText(dataSnapshot.child("resolvedcomplaints").getValue().toString());
            pending.setText(dataSnapshot.child("pendingcomplaints").getValue().toString());
            Image = dataSnapshot.child("image").getValue().toString();
            Picasso.with(getApplicationContext()).load(Image).into(profile);
            spotsDialog.dismiss();

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });





    }

    @Override
    protected void onStart() {
        super.onStart();
//        spotsDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        spotsDialog.show();
        switch (requestCode)
        {
            case  0 : if(RESULT_OK==resultCode)
            {
                Uri selectedImage = data.getData();
                
                StorageReference mStorage = FirebaseStorage.getInstance().getReference();
                mStorage.child("profilepics").child(mAuth.getCurrentUser().getUid().toString()+".jpg").putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            mRoot.child("Users").child(mAuth.getCurrentUser().getUid().toString()).child("image").setValue(task.getResult().getDownloadUrl().toString());
                        }

                        spotsDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Profile Picture Updated..", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                spotsDialog.dismiss();
            }
            break;
        }
    }
}



