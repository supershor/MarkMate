package com.apify.markmate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.security.auth.login.LoginException;

public class create_new_account_page extends AppCompatActivity {
    EditText name;
    EditText surname;
    EditText email;
    EditText password;
    FirebaseAuth firebaseAuth;
    CheckBox checkBox;
    AppCompatButton save_button;
    AppCompatButton login_button;
    String issue1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_new_account_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        firebaseAuth=FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()!=null){
            Log.e("ans jump","1");
            startActivity(new Intent(create_new_account_page.this,MainActivity.class));
            finishAffinity();
        }
        name=findViewById(R.id.name_information_save_info_page);
        surname=findViewById(R.id.surname_information_save_info_page);
        email=findViewById(R.id.email_information_save_info_page);
        password=findViewById(R.id.password_information_save_info_page);
        checkBox=findViewById(R.id.checkbox_agree_for_terms_and_condition_at_save_info_page);
        save_button=findViewById(R.id.save_account_creation_informations);
        login_button=findViewById(R.id.already_have_an_account_save_info_page);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(create_new_account_page.this,login_page.class));
                finish();
            }
        });
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty()){
                    Toast.makeText(create_new_account_page.this, issue1, Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.e("ans jump", "2");
                            if (task.isSuccessful()){
                                Log.e("ans jump", "3");
                                Log.e("ans error",task.toString());
                                Log.e("ans error",task.getException().toString());
                                FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                                startActivity(new Intent(create_new_account_page.this,MainActivity.class));
                                finishAffinity();
                            }else{
                                Log.e("ans jump", "4 ");
                                Log.e("ans error",task.toString());
                                Log.e("ans error",task.getException().toString());
                                String issue=task.getException().toString();
                                if (task.getException().toString().contains("com.google.firebase.auth.FirebaseAuthUserCollisionException: ")){
                                    Log.e("ans --","1");
                                    Toast.makeText(create_new_account_page.this,task.getException().toString().replace("com.google.firebase.auth.FirebaseAuthUserCollisionException: ",""), Toast.LENGTH_SHORT).show();
                                }else if(task.getException().toString().contains("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: ")){
                                    Log.e("ans --","2");
                                    Toast.makeText(create_new_account_page.this,task.getException().toString().replace("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: ",""), Toast.LENGTH_SHORT).show();
                                }else{
                                    Log.e("ans --","3");
                                    Toast.makeText(create_new_account_page.this,task.getException().toString().replace("com.google.firebase.auth.FirebaseAuth",""), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean isEmpty() {
        if (name.getText()==null||surname.getText()==null||email.getText()==null||password.getText()==null){
            issue1="Enter all fields";
            return true;
        }else if(password.getText().length()<=8){
            issue1="Password must be greater than 8 charachters";
            return true;
        }else if(name.getText().length()<=1){
            issue1="Name must be greater than 1 charachters";
            return true;
        }else if(surname.getText().length()<=1){
            issue1="Surname must be greater than 1 charachters";
            return true;
        }else if(!checkBox.isChecked()){
            issue1="Please agree to terms and conditions";
            return true;
        }
        else{
            return false;
        }
    }
}