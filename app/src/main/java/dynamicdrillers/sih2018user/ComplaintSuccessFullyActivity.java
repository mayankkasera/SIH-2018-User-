package dynamicdrillers.sih2018user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        backtohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getBaseContext(),MainActivity.class));
                finish();
            }
        });
    }
}
