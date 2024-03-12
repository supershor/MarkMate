package com.apify.markmate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.apify.markmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

public class code_confirmation_page extends AppCompatActivity {
    static int waiting_seconds=29;
    EditText otp1;
    EditText otp2;
    EditText otp3;
    EditText otp4;
    EditText otp5;
    EditText otp6;
    TextView waiting_time_text_view;
    AppCompatButton resend_button;
    AppCompatButton confirm_button;
    AppCompatButton login;
    String phonenumber;
    String otp_entered;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_code_confirmation_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        otp1=findViewById(R.id.otp_1);
        otp2=findViewById(R.id.otp_2);
        otp3=findViewById(R.id.otp_3);
        otp4=findViewById(R.id.otp_4);
        otp5=findViewById(R.id.otp_5);
        otp6=findViewById(R.id.otp_6);

        waiting_time_text_view=findViewById(R.id.waiting_time_to_otp_to_phone_number_for_verification);

        resend_button=findViewById(R.id.resend_otp_to_phone_number_for_verification);
        confirm_button=findViewById(R.id.confirm_send_otp);
        login=findViewById(R.id.already_have_an_account_code_confirmation);

        phonenumber= getIntent().getExtras().get("mobilenumber").toString();

        sendfocus();


        waitings(waiting_seconds);
        mAuth=FirebaseAuth.getInstance();

        Log.e("ans current user", Objects.requireNonNull(mAuth.getCurrentUser()).toString());

        initiate_otp();
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty()){
                    Toast.makeText(code_confirmation_page.this, "Please enter full otp", Toast.LENGTH_SHORT).show();
                }else{
                    PhoneAuthCredential credential=PhoneAuthCredential.zza(otp_entered,(otp1.getText().toString()+otp2.getText().toString()+otp3.getText().toString()+otp4.getText().toString()+otp5.getText().toString()+otp6.getText().toString()));
                    signInWithCredential(credential);
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(code_confirmation_page.this,login_page.class);
                startActivity(intent);
                finish();
            }
        });


    }
    public void waitings(int i){
        if (i!=0){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    waiting_time_text_view.setText(i+"");
                    waitings(i-1);
                }
            },1000);
        }else{
            waiting_time_text_view.setVisibility(View.GONE);
            resend_button.setVisibility(View.VISIBLE);
            return;
        }
        return;
    }
    public void sendfocus(){
        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otp1.getText().toString().length()>=1){
                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otp2.getText().toString().length()>=1){
                    otp3.requestFocus();
                }else {
                    otp1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otp3.getText().toString().length()>=1){
                    otp4.requestFocus();
                }else {
                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otp4.getText().toString().length()>=1){
                    otp5.requestFocus();
                }else {
                    otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otp5.getText().toString().length()>=1){
                    otp6.requestFocus();
                }else {
                    otp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otp6.getText().toString().length()>=1){
                }else {
                    otp5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public boolean isEmpty(){
        if(otp1.getText()==null||otp2.getText()==null||otp3.getText()==null||otp4.getText()==null||otp5.getText()==null||otp6.getText()==null|| otp1.getText().toString().isEmpty()||otp2.getText().toString().isEmpty()||otp3.getText().toString().isEmpty()||otp4.getText().toString().isEmpty()||otp5.getText().toString().isEmpty()||otp6.getText().toString().isEmpty()|| (otp1.getText().toString()+otp2.getText().toString()+otp3.getText().toString()+otp4.getText().toString()+otp5.getText().toString()+otp6.getText().toString()).length()!=6){
            return true;
        }
        return false;
    }
    public void initiate_otp(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenumber, 60, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(code_confirmation_page.this, "2", Toast.LENGTH_SHORT).show();
                Toast.makeText(code_confirmation_page.this,e.toString(), Toast.LENGTH_SHORT).show();
                Log.e("ans error",e.toString());
                clear();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                otp_entered=s;
            }
        });
    }
    private void signInWithCredential(PhoneAuthCredential credential) {

        // inside this method we are checking if

        // the code entered is correct or not.

        mAuth.signInWithCredential(credential)

                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // if the code is correct and the task is successful

                            // we are sending our user to new activity.

                            Intent intent=new Intent(getApplicationContext(), create_new_account_page.class);
                            intent.putExtra("mobilenumber",phonenumber);

                            Log.e("ans current user",mAuth.getCurrentUser().getUid());
                            startActivity(intent);
                            finish();

                        } else {

                            // if the code is not correct then we are

                            // displaying an error message to the user.
                            Toast.makeText(code_confirmation_page.this, "Incorrect otp", Toast.LENGTH_SHORT).show();
                            Log.e("ans error",task.getException().getMessage());

                        }

                    }

                });

    }
    public void clear(){
        otp1.getText().clear();
        otp2.getText().clear();
        otp3.getText().clear();
        otp4.getText().clear();
        otp5.getText().clear();
        otp6.getText().clear();
    }
}