package com.apify.markmate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class loading_page extends AppCompatActivity {
    Intent login;
    Intent sign_up;

    AppCompatButton loading_page_login;
    AppCompatButton loading_page_sign_up;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(loading_page.this, MainActivity.class));
            finishAffinity();
        }
        loading_page_login =findViewById(R.id.loading_page_login);
        loading_page_sign_up =findViewById(R.id.loading_page_sign_up);
        loading_page_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login=new Intent(loading_page.this,login_page.class);
                startActivity(login);
            }
        });
        loading_page_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_up=new Intent(loading_page.this,create_new_account_page.class);
                startActivity(sign_up);
            }
        });
    }
}