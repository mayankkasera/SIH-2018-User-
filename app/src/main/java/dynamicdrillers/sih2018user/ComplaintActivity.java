package dynamicdrillers.sih2018user;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.HashMap;

public class ComplaintActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String key;
    private String Name,Dis,Add,Vote,Share;
    private long time;
    private TextView NameTxt,TimeTxt,DisTxt,AddTxt,VoteTxt,ShareTxt;
    private String UserId="123";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        recyclerView = findViewById(R.id.complaint_des_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        key = getIntent().getStringExtra("key");
        Name = getIntent().getStringExtra("name");
        time = getIntent().getLongExtra("time",0);
        Dis = getIntent().getStringExtra("description");
        Add = getIntent().getStringExtra("add");

        Toast.makeText(this, ""+key, Toast.LENGTH_SHORT).show();

        NameTxt = findViewById(R.id.name_com_txt);
        TimeTxt = findViewById(R.id.time_com_txt);
        DisTxt = findViewById(R.id.des_com_txt);
        AddTxt = findViewById(R.id.add_com_txt);
        VoteTxt = findViewById(R.id.vote_com_txt);
        ShareTxt = findViewById(R.id.share_com_txt);

        NameTxt.setText(Name);
        TimeTxt.setText(Time.getTimeAgo(time,this));
        DisTxt.setText(Dis);
        AddTxt.setText(Add);


        DatabaseReference referenceVote = FirebaseDatabase.getInstance().getReference()
                .child("complaints").child(key);

        referenceVote.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vote = dataSnapshot.child("complaint_votes").getValue().toString();
                Share = dataSnapshot.child("complaint_share").getValue().toString();
                VoteTxt.setText(dataSnapshot.child("complaint_votes").getValue()+ " Votes");
                ShareTxt.setText(Share+" Share");
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
                                        int i = Integer.parseInt(Vote);
                                        i=i-1;
                                        reference.child("complaint_votes").setValue(i+"");
                                        Toast.makeText(ComplaintActivity.this, "vote -", Toast.LENGTH_SHORT).show();
                                        VoteImg.setImageResource(R.drawable.unlike);
                                    }
                                    else{
                                        referenceVote.child(UserId).setValue("true");
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                                .child("complaints").child(key);
                                        int i = Integer.parseInt(Vote);
                                        i = i+1;
                                        reference.child("complaint_votes").setValue(i+"");
                                        Toast.makeText(ComplaintActivity.this, "vote +", Toast.LENGTH_SHORT).show();

                                        VoteImg.setImageResource(R.drawable.like);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            HashMap<String,String> Vote = new HashMap<>();
                            Vote.put("complainer_id",key);

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                    .child("complaints").child(key);
                            int i = Integer.parseInt(String.valueOf(Vote));
                            reference.child("complaint_votes").setValue(i+1+"");

                            VoteImg.setImageResource(R.drawable.unlike);
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

}
