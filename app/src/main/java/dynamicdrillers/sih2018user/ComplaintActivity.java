package dynamicdrillers.sih2018user;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ComplaintActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String key;
    private String Name,Dis,Add,Vote,Share,Status,time;
    private ProgressDialog progressBar;
    private TextView NameTxt,TimeTxt,DisTxt,AddTxt,VoteTxt,ShareTxt,complaint_status,StatusTxt;
    private String UserId="";
    private  String ComplainerUserId ;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ImageView voteimg;
    private String TAG="bc";
    private CircleImageView profileImage;
    private ImageView deleteImg;
    private int flag = 0;
    private  DatabaseReference referenceVote  = null;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private StorageReference mStorage;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        UserId = mAuth.getCurrentUser().getUid().toString();

        recyclerView = findViewById(R.id.complaint_des_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mStorage = FirebaseStorage.getInstance().getReference();

        key = getIntent().getStringExtra("key");
        Name = getIntent().getStringExtra("name");
        time = getIntent().getStringExtra("time");
        Dis = getIntent().getStringExtra("description");
        Add = getIntent().getStringExtra("add");
        ComplainerUserId = getIntent().getStringExtra("userid");
        Status = getIntent().getStringExtra("status");


        Toast.makeText(this, ""+key, Toast.LENGTH_SHORT).show();

        NameTxt = findViewById(R.id.name_com_txt);
        TimeTxt = findViewById(R.id.time_com_txt);
        DisTxt = findViewById(R.id.des_com_txt);
        AddTxt = findViewById(R.id.add_com_txt);
        VoteTxt = findViewById(R.id.vote_com_txt);
        ShareTxt = findViewById(R.id.share_com_txt);
        voteimg  =findViewById(R.id.vote_img);
        profileImage = findViewById(R.id.profile_image);
        complaint_status = findViewById(R.id.status_com_txt);
        StatusTxt = findViewById(R.id.status_com_txt);
        deleteImg = findViewById(R.id.delete_complaint);



        NameTxt.setText(Name);
        Long l = Long.parseLong(time);
        TimeTxt.setText(Time.getTimeAgo(l,this));
        DisTxt.setText(Dis);
        AddTxt.setText(Add);
        StatusTxt.setText(Status);


        referenceVote = FirebaseDatabase.getInstance().getReference()
                .child("complaints").child(key);
        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                    Vote = dataSnapshot.child("complaint_votes").getValue().toString();
                    Share = dataSnapshot.child("complaint_share").getValue().toString();
                    VoteTxt.setText(dataSnapshot.child("complaint_votes").getValue()+ " Votes");
                    ShareTxt.setText(Share+" Share");
                    complaint_status.setText(dataSnapshot.child("complaint_status").getValue().toString());
                    setTime(dataSnapshot.child("complaint_request_time").getValue().toString());



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        referenceVote.addValueEventListener(valueEventListener);



        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        Intent intent = new Intent(getApplicationContext(),DeleteActivity.class);
                        intent.putExtra("key",key);
                        startActivity(intent);
                        finish();



                    }

        });

        StatusTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

               if(StatusTxt.getText().equals("Reject")){
                   final Dialog dialog = new Dialog(ComplaintActivity.this);
                   dialog.setContentView(R.layout.reject_complaint_dialog_layout);
                   dialog.setTitle("Reject Reasion ");

                   final TextView Reject  = (TextView) dialog.findViewById(R.id.reject_txt);
                   final Button ok = (Button) dialog.findViewById(R.id.ok_btn);

                   FirebaseDatabase.getInstance().getReference().child("complaints").child(key)
                           .addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   Reject.setText(dataSnapshot.child("Reject Reason").getValue().toString());
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {

                               }
                           });

                   ok.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           dialog.dismiss();
                       }
                   });

                   dialog.show();

               }

               if(!StatusTxt.getText().equals("Reject")){
                   if(!StatusTxt.getText().equals("Resolved"))
                        checkPermissions();
               }





            }
        });





        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(ComplainerUserId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               Picasso.with(getApplicationContext()).load(dataSnapshot.child("image").getValue().toString()).into(profileImage);
                NameTxt.setText(dataSnapshot.child("name").getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("vote").child(key);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserId)){
                    if(dataSnapshot.child(UserId).getValue().equals("true")){
                        voteimg.setImageResource(R.drawable.like);
                        Toast.makeText(ComplaintActivity.this,"true", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        voteimg.setImageResource(R.drawable.unlike);
                        Toast.makeText(ComplaintActivity.this, "false", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





              VoteTxt.setText(Vote+ " Votes");

        final ImageView VoteImg = findViewById(R.id.vote_img);

        findViewById(R.id.share_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child("complaints").child(key);
                int i = Integer.parseInt(Share);
                reference.child("complaint_share").setValue(i+1+"");

            }
        });

        findViewById(R.id.vote_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DatabaseReference referenceVote = FirebaseDatabase.getInstance().getReference()
                        .child("vote").child(key);
                referenceVote.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(UserId)){
                            referenceVote.child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue().equals("true")){
                                        referenceVote.child(UserId).setValue("false");
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                                .child("complaints").child(key);


                                        int i = Integer.parseInt(String.valueOf(Vote));
                                        i=i-1;
                                        reference.child("complaint_votes").setValue(i+"");
                                        Toast.makeText(ComplaintActivity.this, "vote -", Toast.LENGTH_SHORT).show();
                                        //VoteImg.setImageResource(R.drawable.unlike);
                                    }
                                    else{
                                        referenceVote.child(UserId).setValue("true");
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                                .child("complaints").child(key);
                                        int i = Integer.parseInt(String.valueOf(Vote));
                                        i = i+1;
                                        reference.child("complaint_votes").setValue(i+"");
                                        Toast.makeText(ComplaintActivity.this, "vote +", Toast.LENGTH_SHORT).show();

                                        //VoteImg.setImageResource(R.drawable.like);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else {

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                    .child("complaints").child(key);
                            int i = Integer.parseInt(String.valueOf(Vote));
                            reference.child("complaint_votes").setValue(i+1+"");

                           // VoteImg.setImageResource(R.drawable.unlike);
                            referenceVote.child(UserId).setValue("true");
                            Toast.makeText(ComplaintActivity.this, "vote"+key.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });

    }





    public void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("complaints").child(key).child("images");


        FirebaseRecyclerOptions<ComplaintDesModal> options =
                new FirebaseRecyclerOptions.Builder<ComplaintDesModal>()
                        .setQuery(query, ComplaintDesModal.class)
                        .build();

        FirebaseRecyclerAdapter<ComplaintDesModal,ComplaintDisViewHolder> adapter
                = new FirebaseRecyclerAdapter<ComplaintDesModal,ComplaintDisViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ComplaintDisViewHolder holder, final int position, @NonNull final ComplaintDesModal user) {
                final int pos = position;
                holder.setName(user.getType());
                holder.setCategiryImage(user.getImage());



            }

            @Override
            public ComplaintDisViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View mView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.complaindeslayout, parent, false);

                return new ComplaintDisViewHolder(mView);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    public void setTime(String title){

        String s = Time.getTimeAgo(Long.parseLong(title),getBaseContext());
        TextView textView = findViewById(R.id.time_com_txt);
        textView.setText(s);
    }


    public class ComplaintDisViewHolder extends RecyclerView.ViewHolder {
        View mView;



        public ComplaintDisViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String title){
            Button button = mView.findViewById(R.id.before_com_dis);
            button.setText(title);
        }


        public void setCategiryImage(String image) {
            ImageView img =  mView.findViewById(R.id.img_com_dis);;
            Picasso.with(ComplaintActivity.this).load(image).into(img);
        }



        public View getView(){
            return this.mView;
        }

    }

    void checkPermissions(){
        String s[]={android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE};
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getImages();
            }
            else{
                ActivityCompat.requestPermissions((Activity) this,s,123);
            }
        }
        else{
            ActivityCompat.requestPermissions((Activity) this,s,123);
        }
    }

    private void getImages() {
        Config config = new Config();
        config.setSelectionMin(1);
        config.setSelectionLimit(4);
        ImagePickerActivity.setConfig(config);
        Intent intent  = new Intent(this, ImagePickerActivity.class);
        startActivityForResult(intent,INTENT_REQUEST_GET_IMAGES);

    }

    @Override
    public void onActivityResult(int requestCode, int resuleCode, final Intent intent) {
        super.onActivityResult(requestCode, resuleCode, intent);

        if (requestCode == INTENT_REQUEST_GET_IMAGES && resuleCode == Activity.RESULT_OK ) {
            progressBar = new ProgressDialog(this);
            progressBar.setMessage("INITIALIZING ...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.show();


            ArrayList<Uri> image_uris = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            Toast.makeText(this, image_uris.size()+" "+image_uris.toString(), Toast.LENGTH_SHORT).show();

            final DatabaseReference  reference = FirebaseDatabase.getInstance().getReference();
            for (int i = 0;i<image_uris.size();i++ )
            {
                final int finalI = i;
                Toast.makeText(this, ""+key, Toast.LENGTH_SHORT).show();
                Uri img_uri = Uri.fromFile(new File(String.valueOf(image_uris.get(i))));
                mStorage.child("complaints").child(key+"_request_response_"+(i+1)+".jpg").putFile(img_uri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                Toast.makeText(getApplicationContext(), "image " + finalI + "uploaded", Toast.LENGTH_SHORT).show();
                                HashMap<String,String> data = new HashMap<>();
                                data.put("image",task.getResult().getDownloadUrl().toString());
                                data.put("type","request response");
                                reference.child("complaints").child(key).child("images").push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.dismiss();
                                        Intent intent1 = new Intent(ComplaintActivity.this,MainActivity.class);
                                        startActivity(intent1);
                                        finish();
                                        FirebaseDatabase.getInstance().getReference().child("complaints").child(key)
                                                .child("complaint_resolved_time").setValue(ServerValue.TIMESTAMP);

                                        FirebaseDatabase.getInstance().getReference()
                                                .child("complaints").child(key).child("complaint_status").setValue("Resolved");
                                    }
                                });
                            }
                        });
            }
            //do something//
            progressBar.dismiss();
        }

    }


}


