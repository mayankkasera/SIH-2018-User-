package dynamicdrillers.sih2018user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ComplaintSuccessFullyActivity extends AppCompatActivity {

    Button backtohome;
    TextView ComplaintId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_success_fully);
        backtohome = findViewById(R.id.back_to_home);
        ComplaintId = findViewById(R.id.complaintid_success_textview);

        String  c_id = getIntent().getStringExtra("complaintid");
        ComplaintId.setText( "ID : "+c_id.toString());

        sendPushNotification(getIntent().getStringExtra("token"),getIntent().getStringExtra("catagory"),getIntent().getStringExtra("address"));

        backtohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getBaseContext(),MainActivity.class));
                finish();
            }
        });
    }


    public void sendPushNotification(final String TokenUID , final String ComplaintCatagory, final String Address) {

        //Toast.makeText(this, TokenUID +"\n\n" +ComplaintCatagory + Address, Toast.LENGTH_SHORT).show();




        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, "http://happystore.16mb.com/sihapi/SendNotificationByOneSignal.php"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ComplaintSuccessFullyActivity.this, "Notification send" + response.toString(), Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ComplaintSuccessFullyActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                final HashMap<String,String> param = new HashMap<>();
                param.put("receivertoken",TokenUID);
                param.put("description","at " +Address);
                param.put("title","New "+ComplaintCatagory+" Complaint..");
                return  param;
            }
        };


        MySingleton.getInstance(ComplaintSuccessFullyActivity.this).addToRequestQueue(stringRequest);





    }

}
