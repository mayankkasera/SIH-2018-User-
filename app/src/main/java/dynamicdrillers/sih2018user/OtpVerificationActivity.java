package dynamicdrillers.sih2018user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

public class OtpVerificationActivity extends AppCompatActivity {

    LinearLayout done;
    Pinview pinview;
    String fullotp;
    TextView resendCode;
    String verificationId,mobileNo;
    SpotsDialog spotsDialog ;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        spotsDialog = new SpotsDialog(this);
        verificationId = getIntent().getStringExtra("verificationId");
        mobileNo = getIntent().getStringExtra("mobileno");
        done = (LinearLayout)findViewById(R.id.doneVerification_otpverification_linearlayout);
        pinview = (Pinview)findViewById(R.id.pinview);
        resendCode = (TextView)findViewById(R.id.resendCode_otpVerification_text);



        // Setting Timer
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                resendCode.setText(""+millisUntilFinished / 1000);
                resendCode.setEnabled(false);
            }

            public void onFinish() {
                resendCode.setText("RESEND CODE");
                resendCode.setEnabled(true);
            }
        }.start();


        //setting OtpCOde
        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean b) {
                fullotp = pinview.getValue();
            }
        });

        // Submit Button
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spotsDialog.show();
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId,fullotp);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }
        });

        //resending Code
        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PhoneAuthProvider.getInstance().verifyPhoneNumber(mobileNo
                        ,60
                        , TimeUnit.SECONDS
                        ,OtpVerificationActivity.this
                        ,mCallback);

                Toast.makeText(getBaseContext(),"We Have Sent To You Another Otp ",Toast.LENGTH_SHORT).show();
            }
        });


        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getApplicationContext(),"Invalid Mobile Number",Toast.LENGTH_SHORT).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {

                    Toast.makeText(getApplicationContext(),"Quate Exceed",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCodeSent(String verificationIdresend,
                                   PhoneAuthProvider.ForceResendingToken token) {

                verificationId = verificationIdresend;
                new CountDownTimer(60000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        resendCode.setText(""+millisUntilFinished / 1000);
                        resendCode.setEnabled(false);
                    }

                    public void onFinish() {
                        resendCode.setText("RESEND CODE");
                        resendCode.setEnabled(true);
                    }
                }.start();

            }
        };


    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    //Checking User is New Or Old
                    FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child(task.getResult().getUser().getUid().toString()).exists())
                            {



                                // Sending to mainActivity
                                SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("name",dataSnapshot.child(task.getResult().getUser().getUid().toLowerCase()).child("name").getValue().toString());
                                editor.putString("mobile",task.getResult().getUser().getUid());
                                editor.putString("image",dataSnapshot.child(task.getResult().getUser().getUid().toLowerCase()).child("image").getValue().toString()) ;

                                editor.commit();
                                editor.apply();

                                startActivity(new Intent(OtpVerificationActivity.this,MainActivity.class));
                                finish();
                            }
                            else
                            {
                                HashMap<String,String> user = new HashMap<>();
                                user.put("mobile",task.getResult().getUser().getPhoneNumber());
                                user.put("name","Citizen");
                                user.put("image","https://www.alectro.com.au/libraries/images/icons/Male_Profile_Picture_Silhouette_Profile_Grey.png");
                                user.put("resolvedcomplaints","0");
                                user.put("pendingcomplaints","0");
                                FirebaseDatabase.getInstance().getReference().child("Users").child(task.getResult().getUser().getUid()).setValue(user);

                                // Sending to mainActivity
                                startActivity(new Intent(OtpVerificationActivity.this,MainActivity.class));
                                finish();
                            }
                            spotsDialog.dismiss();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            spotsDialog.dismiss();

                        }
                    });

                }
                else{

                    Toast.makeText(getApplicationContext(),"Enter Correct Otp Code",Toast.LENGTH_SHORT).show();
                    spotsDialog.dismiss();

                }
            }
        });

    }
}
