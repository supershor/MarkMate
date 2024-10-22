package com.supershor.markmate;

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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.supershor.markmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

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
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

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

        //setting status bar color to dark green for better look
        getWindow().setStatusBarColor(ContextCompat.getColor(create_new_account_page.this,R.color.dark_green));

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
                if(isEmpty()||contains_email(email.getText().toString()) || contains_word(password.getText().toString())){
                    Toast.makeText(create_new_account_page.this, issue1, Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.e( "onClick: ","-"+email.getText().toString()+"=");
                    Log.e( "onClick: ","-"+Boolean.valueOf(email.getText().length()<=0)+"+");
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.e("ans jump", "2");
                            if (task.isSuccessful()){
                                FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                                firebaseDatabase=FirebaseDatabase.getInstance("https://markmate-5452c-default-rtdb.asia-southeast1.firebasedatabase.app/");
                                databaseReference=firebaseDatabase.getReference("USER DATA");
                                databaseReference.child(firebaseUser.getUid()).child("Personal informations").child("Name").setValue(name.getText()+" "+surname.getText()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        startActivity(new Intent(create_new_account_page.this,MainActivity.class));
                                        finishAffinity();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("ans failure",e.toString());
                                        Toast.makeText(create_new_account_page.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                Log.e("ans jump", "4 ");
                                Log.e("ans error",task.toString());
                                Log.e("ans error", Objects.requireNonNull(task.getException()).toString());
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
        }else if(email.getText().length()<=0){
            issue1="Enter valid email";
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
    private boolean contains_word(String string) {
        if (string.length()<=0){
            issue1="Password Empty";
            return true;
        }
        if (string.contains(" ")){
            issue1="Password cant contains spaces";
            return true;
        }
        return false;
    }
    private boolean contains_email(String string) {
        if (string==null){
            issue1="Email field Empty";
            return true;
        }
        if (string.length()<=0){
            issue1="Email field Empty";
            return true;
        }
        if (string.contains(" ")){
            issue1="Email cant contains spaces";
            return true;
        }
        if (!string.contains("@")){
            issue1="Invalid email";
            return true;
        }
        return false;
    }
}