package dynamicdrillers.sih2018user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class NearbyComplaintsFragment extends Fragment {


    private RecyclerView recyclerView;
    private String UserId="";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nearby_complaints, container, false);

        UserId = mAuth.getCurrentUser().getUid().toString();

        recyclerView = view.findViewById(R.id.your_complaints_recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    public void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("complaints").orderByChild("complainer_state").equalTo("madhyapradesh");


        FirebaseRecyclerOptions<ComplaintModal> options =
                new FirebaseRecyclerOptions.Builder<ComplaintModal>()
                        .setQuery(query, ComplaintModal.class)
                        .build();

        FirebaseRecyclerAdapter<ComplaintModal,ComplaintViewHolder> adapter
                = new FirebaseRecyclerAdapter<ComplaintModal,ComplaintViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final ComplaintViewHolder holder, final int position, @NonNull final ComplaintModal user) {
                final int pos = position;

                String complainer_id = user.getComplainer_id();

                final String[] userName = new String[1];
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(complainer_id);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        holder.setName(dataSnapshot.child("name").getValue().toString());
                        holder.setProfileImage(dataSnapshot.child("image").getValue().toString());

                        userName[0] =dataSnapshot.child("name").getValue().toString();


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                holder.setStatus(user.getComplaint_status());
                holder.setTime(user.getComplaint_request_time());
                holder.setDes(user.getComplaint_description());
                holder.setAdd(user.getComplaint_full_address());
                holder.setShare(user.getComplaint_share());
                holder.setVote(user.getComplaint_votes());
                holder.setImage(getRef(position).getKey());
                final LinearLayout VoteLayout = holder.getView().findViewById(R.id.vote_layout);
                final LinearLayout ShareLayout = holder.getView().findViewById(R.id.vote_layout);
                final ImageView VoteImg = holder.getView().findViewById(R.id.vote_img);



                holder.getView().findViewById(R.id.com_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(),ComplaintActivity.class);
                        intent.putExtra("userid",user.getComplainer_id());
                        intent.putExtra("key",getRef(position).getKey());
                        intent.putExtra("name",userName[0]);
                        intent.putExtra("time",user.getComplaint_request_time());
                        intent.putExtra("description",user.getComplaint_description());
                        intent.putExtra("add",user.getComplaint_full_address());
                        intent.putExtra("vote",user.getComplaint_votes());
                        intent.putExtra("share",user.getComplaint_share());
                        startActivity(intent);
                    }
                });


                holder.getView().findViewById(R.id.vote_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final DatabaseReference referenceVote = FirebaseDatabase.getInstance().getReference()
                                .child("vote").child(getRef(position).getKey().toString());
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
                                                        .child("complaints").child(getRef(position).getKey());
                                                int i = Integer.parseInt(user.getComplaint_votes());
                                                i=i-1;
                                                reference.child("complaint_votes").setValue(i+"");
                                                Toast.makeText(getContext(), "vote -", Toast.LENGTH_SHORT).show();
                                                // VoteImg.setImageResource(R.drawable.like);
                                            }
                                            else{
                                                referenceVote.child(UserId).setValue("true");
                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                                        .child("complaints").child(getRef(position).getKey());
                                                int i = Integer.parseInt(user.getComplaint_votes());
                                                reference.child("complaint_votes").setValue(i+1+"");
                                                Toast.makeText(getContext(), "vote +", Toast.LENGTH_SHORT).show();

                                                //VoteImg.setImageResource(R.drawable.unlike);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                                else {
                                    HashMap<String,String> Vote = new HashMap<>();
                                    Vote.put("complainer_id",getRef(position).getKey().toString());

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                            .child("complaints").child(getRef(position).getKey());
                                    int i = Integer.parseInt(user.getComplaint_votes());
                                    reference.child("complaint_votes").setValue(i+1+"");

                                    // VoteImg.setImageResource(R.drawable.like);
                                    referenceVote.child(UserId).setValue("true");
                                    Toast.makeText(getContext(), "vote"+getRef(position).getKey().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }
                });

                holder.getView().findViewById(R.id.share_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child("complaints").child(getRef(position).getKey());
                        int i = Integer.parseInt(user.getComplaint_share());
                        reference.child("complaint_share").setValue(i+1+"");

                    }
                });

            }

            @Override
            public ComplaintViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View mView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_complaint_layout, parent, false);

                return new NearbyComplaintsFragment.ComplaintViewHolder(mView);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }



    public class ComplaintViewHolder extends RecyclerView.ViewHolder {
        View mView;


        public ComplaintViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String title){
            TextView textView = mView.findViewById(R.id.name_com_txt);
            textView.setText(title);
        }

        public void setStatus(String title){
            TextView textView = mView.findViewById(R.id.status_com_txt);
            textView.setText(title);
        }


        public void setCategiryImage(String image) {
            ImageView img =  mView.findViewById(R.id.user_complainer_img);;
            Picasso.with(getContext()).load(image).into(img);
        }

        public void setTime(String title){

            String s = Time.getTimeAgo(Long.parseLong(title),getContext());
            TextView textView = mView.findViewById(R.id.time_com_txt);
            textView.setText(s);
        }

        public void setDes(String title){
            TextView textView = mView.findViewById(R.id.des_com_txt);
            textView.setText(title);
        }

        public void setAdd(String title){
            TextView textView = mView.findViewById(R.id.add_com_txt);
            textView.setText(title);
        }

        public void setVote(String title){
            TextView textView = mView.findViewById(R.id.vote_com_txt);
            textView.setText(title+ " Votes");
        }

        public void setShare(String title){
            TextView textView = mView.findViewById(R.id.share_com_txt);
            textView.setText(title+" Share");
        }

        public void setImage(String id){
            final ImageView imageView = mView.findViewById(R.id.vote_img);
            final DatabaseReference referenceVote = FirebaseDatabase.getInstance().getReference()
                    .child("vote").child(id);

            referenceVote.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(UserId)){
                        if(dataSnapshot.child(UserId).getValue().equals("true")){
                            imageView.setImageResource(R.drawable.like);
                        }
                        else {
                            imageView.setImageResource(R.drawable.unlike);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }

        public void setProfileImage(String ImageLink)
        {
            CircleImageView userProfile = mView.findViewById(R.id.user_complainer_img);

            Picasso.with(getContext()).load(ImageLink).into(userProfile);

        }



        public View getView(){
            return this.mView;
        }

    }


}
