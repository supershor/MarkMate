package com.supershor.markmate;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.net.MailTo;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class settings extends AppCompatActivity {
    AppCompatButton change_password;
    String issue;
    AppCompatButton report_error;
    AppCompatButton contact_owner;
    AppCompatButton logout;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //setting status bar color to dark green for better look
        getWindow().setStatusBarColor(ContextCompat.getColor(settings.this,R.color.dark_green));

        //checking if the user is signed in or not if not the redirecting him to loading page
        firebaseAuth=FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()==null){
            startActivity(new Intent(settings.this,loading_page.class));
            finishAffinity();
        }

        //initializing
        change_password=findViewById(R.id.change_password_settings);
        report_error=findViewById(R.id.report_error_settings);
        contact_owner=findViewById(R.id.contact_owner_settings);
        logout=findViewById(R.id.logout_settings);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert=new AlertDialog.Builder(settings.this);
                alert.setTitle("Logout")
                        .setCancelable(false)
                        .setMessage("Do you want to logout ? ")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseAuth.signOut();
                                startActivity(new Intent(settings.this,loading_page.class));
                                finishAffinity();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alert.show();
            }
        });
        report_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse(MailTo.MAILTO_SCHEME));
                intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"supershor.cp@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT,"Report error in MARKMATE");
                startActivity(intent);
            }
        });
        contact_owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse(MailTo.MAILTO_SCHEME));
                intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"supershor.cp@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT,"Contact owner regarding MARKMATE");
                startActivity(intent);
            }
        });
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseAuth.sendPasswordResetEmail(firebaseAuth.getCurrentUser().getEmail())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(settings.this, "Reset link has been send to your email.", Toast.LENGTH_SHORT).show();
                                Log.e("ans reset","sucess");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(settings.this, e.toString(), Toast.LENGTH_SHORT).show();
                                Log.e("ans reset",e.toString());
                            }
                        });
                /*
                EditText editText=new EditText(settings.this);
                AlertDialog.Builder alert=new AlertDialog.Builder(settings.this);
                alert.setView(editText)
                    .setTitle("Change password")
                    .setMessage("Enter new password")
                            .setCancelable(false)
                                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(editText.getText()!=null&&contains_word(editText.getText().toString())){
                                                firebaseAuth.getCurrentUser().updatePassword(editText.getText().toString())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.e("ans change=-=--------","changed");
                                                        Toast.makeText(settings.this, "Password chenged", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                       @Override
                                                       public void onFailure(@NonNull Exception e) {
                                                          Log.e("ans chnge",e.toString());
                                                          Toast.makeText(settings.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                       }
                                                });
                                            }else{
                                                Toast.makeText(settings.this,issue, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                alert.show();

                 */
            }
        });

    }

    private boolean contains_word(String string) {
        if (string.length()<=0){
            issue="Empty field";
            return false;
        }
        if (string.contains(" ")){
            issue="Password cant contains spaces";
            return false;
        }
        return true;
    }
}