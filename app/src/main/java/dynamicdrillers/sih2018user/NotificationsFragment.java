package dynamicdrillers.sih2018user;


import android.app.Notification;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {


    RecyclerView recyclerView;
    ImageView imageView ;


    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.nt_recyclerview);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);



        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance().getReference().child("notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).orderByChild("status").equalTo("true");

        FirebaseRecyclerOptions<NotificationModal> options = new FirebaseRecyclerOptions.Builder<NotificationModal>()
                .setQuery(query, NotificationModal.class)
                .build();


        FirebaseRecyclerAdapter<NotificationModal,NotificationViewhOLDER> recyclerAdapter = new FirebaseRecyclerAdapter<NotificationModal, NotificationViewhOLDER>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotificationViewhOLDER holder, final int position, @NonNull NotificationModal model) {

                if(position>=0) {


                    holder.view.findViewById(R.id.nt_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String nt_id = getRef(position).getKey();
                            FirebaseDatabase.getInstance().getReference().child("notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(nt_id).child("status").setValue("false");

                        }
                    });
                    holder.setTitle(model.getTitle());
                    holder.setDescription(model.getDescription());
                    holder.setTime(model.getTime());

                }




            }

            @Override
            public NotificationViewhOLDER onCreateViewHolder(ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.singlenotificationlayout, parent, false);

                return new NotificationViewhOLDER(mView);
            }
        };
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.startListening();




    }






    public  class  NotificationViewhOLDER extends  RecyclerView.ViewHolder
    {

        View view ;
        public NotificationViewhOLDER(View itemView) {
            super(itemView);
            this.view = itemView;
        }


        public void setTitle(String title) {
            TextView textView = view.findViewById(R.id.nt_title);
            textView.setText(title);

        }

        public void setDescription(String description) {

            TextView textView = view.findViewById(R.id.nt_desc);
            textView.setText(description);

        }

        public void setTime(String time) {
            String s = Time.getTimeAgo(Long.parseLong(time),getContext());
            TextView textView = view.findViewById(R.id.nt_time);
            textView.setText(s);

        }







    }

}
