package com.apify.markmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hbb20.CountryCodePicker;

public class sign_up_page extends AppCompatActivity {
    AppCompatButton send_otp;
    AppCompatButton login;
    CountryCodePicker countryCodePicker;
    EditText personal_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        send_otp=findViewById(R.id.send_otp_to_phone_number_for_verification);
        login=findViewById(R.id.already_have_an_account);
        countryCodePicker=findViewById(R.id.ccp);
        personal_number=findViewById(R.id.PERSONAL_NUMBER);
        countryCodePicker.registerCarrierNumberEditText(personal_number);

        send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (personal_number.getText()!=null && personal_number.getText().length()>=10){
                    Intent intent=new Intent(sign_up_page.this, code_confirmation_page.class);
                    intent.putExtra("mobilenumber", countryCodePicker.getFullNumberWithPlus().replace(" ",""));

                    //toast
                    Toast.makeText(sign_up_page.this, countryCodePicker.getFullNumberWithPlus().replace(" ",""), Toast.LENGTH_SHORT).show();


                    startActivity(intent);
                }else{
                    Toast.makeText(sign_up_page.this, "Enter correct number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(sign_up_page.this, login_page.class);
                startActivity(intent);
                finish();
            }
        });
    }
}