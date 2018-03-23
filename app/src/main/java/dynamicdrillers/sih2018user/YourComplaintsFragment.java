package dynamicdrillers.sih2018user;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class YourComplaintsFragment extends Fragment {

    private RecyclerView recyclerView;

    public YourComplaintsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_your_complaints, container, false);

        recyclerView = view.findViewById(R.id.your_complaints_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }



    public void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("complaints");


        FirebaseRecyclerOptions<ComplaintModal> options =
                new FirebaseRecyclerOptions.Builder<ComplaintModal>()
                        .setQuery(query, ComplaintModal.class)
                        .build();

        FirebaseRecyclerAdapter<ComplaintModal,ComplaintViewHolder> adapter
                = new FirebaseRecyclerAdapter<ComplaintModal,ComplaintViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ComplaintViewHolder holder, final int position, @NonNull final ComplaintModal user) {
                final int pos = position;
                holder.setName("Name");
                holder.setStatus(user.getComplaint_status());
                holder.setTime(user.getComplaint_request_time());
                holder.setDes(user.getComplaint_dis());
                holder.setAdd(user.getComplaint_full_address());
                holder.setShare(user.getComplaint_share());
                holder.setVote(user.getComplaint_votes());
                final LinearLayout VoteLayout = holder.getView().findViewById(R.id.vote_layout);
                final LinearLayout ShareLayout = holder.getView().findViewById(R.id.vote_layout);



                holder.getView().findViewById(R.id.com_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(),ComplaintActivity.class);
                        intent.putExtra("key",getRef(position).getKey());
                        startActivity(intent);
                    }
                });


                holder.getView().findViewById(R.id.vote_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child("complaints").child(getRef(position).getKey());
                        int i = Integer.parseInt(user.getComplaint_votes());
                                reference.child("complaint_votes").setValue(i+1+"");
                                VoteLayout.setAlpha(1);

                    }
                });

                holder.getView().findViewById(R.id.share_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child("complaints").child(getRef(position).getKey());
                        int i = Integer.parseInt(user.getComplaint_share());
                        reference.child("complaint_share").setValue(i+1+"");
                        ShareLayout.setAlpha(1);
                    }
                });

            }

            @Override
            public ComplaintViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View mView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_complaint_layout, parent, false);

                return new ComplaintViewHolder(mView);
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
            ImageView img =  mView.findViewById(R.id.categiry_com_img);;
            Picasso.with(getContext()).load(image).into(img);
        }

        public void setTime(String title){
            long l = Long.parseLong(title);
            String s = Time.getTimeAgo(l,getContext());
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



        public View getView(){
            return this.mView;
        }

    }

}
