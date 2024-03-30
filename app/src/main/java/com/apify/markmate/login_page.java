package com.apify.markmate;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.apify.markmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login_page extends AppCompatActivity {
    AppCompatButton login;
    AppCompatButton forgot_password;
    AppCompatButton create_account;
    EditText account;
    EditText password;
    FirebaseAuth firebaseAuth;
    String issue;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth=FirebaseAuth.getInstance();

        login=findViewById(R.id.login_at_login_page);
        forgot_password=findViewById(R.id.forgot_password_login_page);
        create_account=findViewById(R.id.sign_up_at_login_page);
        account=findViewById(R.id.account_login_page);
        password=findViewById(R.id.password_login_page);
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login_page.this,create_new_account_page.class));
                finish();
            }
        });
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText=new EditText(login_page.this);
                final AlertDialog.Builder reset_alert_dialog=new AlertDialog.Builder(login_page.this);
                reset_alert_dialog.setTitle("Reset Password").setMessage("Enter your email to get reset link").setView(editText);
                reset_alert_dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText()!=null&&editText.getText().toString().length()>0){
                            firebaseAuth.sendPasswordResetEmail(editText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(login_page.this, "Reset link sent.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("ans errors 2",e.toString());
                                    Log.e("ans errors 3",e.getMessage());
                                    Toast.makeText(login_page.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Toast.makeText(login_page.this, "Please enter email address correctly", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                reset_alert_dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                reset_alert_dialog.show();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty()){
                    Toast.makeText(login_page.this, issue, Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.signInWithEmailAndPassword(account.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                                startActivity(new Intent(login_page.this, MainActivity.class));
                                finishAffinity();
                            }else{
                                Log.e("ans jump", "4 ");
                                Log.e("ans error",task.toString());
                                Log.e("ans error",task.getException().toString());
                                if(task.getException().toString().replace("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: ", "").equals("The supplied auth credential is incorrect, malformed or has expired.")){
                                    Toast.makeText(login_page.this,"Information incorrect or session expired.", Toast.LENGTH_SHORT).show();
                                }
                                else if (task.getException().toString().contains("com.google.firebase.auth.FirebaseAuthUserCollisionException: ")){
                                    Toast.makeText(login_page.this,task.getException().toString().replace("com.google.firebase.auth.FirebaseAuthUserCollisionException: ",""), Toast.LENGTH_SHORT).show();
                                }else if(task.getException().toString().contains("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: ")){
                                    Toast.makeText(login_page.this,task.getException().toString().replace("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: ",""), Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(login_page.this,task.getException().toString().replace("com.google.firebase.auth.FirebaseAuth",""), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }
    public boolean isEmpty(){
        if(account.getText()==null){
            issue="Please enter all account";
            return true;
        } else if (password.getText()==null) {
            issue="Please enter all password";
            return true;
        }else if (account.getText().length()<=0) {
            issue="Please enter full account";
            return true;
        }else if (password.getText().length()<=8) {
            issue="Please enter full password";
            return true;
        }
        return false;
    }
}