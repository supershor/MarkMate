package com.apify.markmate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface{
    AppCompatButton appCompatButton;
    AppCompatButton settings;
    FirebaseDatabase database;
    DatabaseReference firebase;
    EditText org_name;
    EditText org_desc;
    ArrayList<list> arr;
    String issues;
    FirebaseAuth firebaseAuth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //setting status bar color to dark green for better look
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.dark_green));

        //checking if the user is signed in or not and if not directing him to either login or signup
        firebaseAuth=FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()==null){
            Log.e("ans jump","1");
            startActivity(new Intent(MainActivity.this,loading_page.class));
            finishAffinity();
        }

        //setting access point for firebase and initializing elements
        database = FirebaseDatabase.getInstance("https://markmate-5452c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        firebase = database.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid());
        arr = new ArrayList<>();
        settings=findViewById(R.id.settings_org);
        RecyclerView recyclerView = findViewById(R.id.recycler_organization_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        appCompatButton=findViewById(R.id.add_new_organization);

        //setting values of recycler view using firebase
        firebase.child("list_of_organizations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arr.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Log.e("ans success", ds.getKey() + "<-->" + ds.getValue());
                    arr.add(new list(ds.getKey(), ds.getValue().toString()));
                }
                Recycler r = new Recycler(MainActivity.this, arr,MainActivity.this::onItemclick);
                recyclerView.setAdapter(r);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ans error", error.getMessage());
                Toast.makeText(MainActivity.this,error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //on add new org button click
        appCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View v1= LayoutInflater.from(MainActivity.this).inflate(R.layout.org_name_desc_alert_dialog_view,null);
                org_name =v1.findViewById(R.id.org_name_alert_dialog);
                org_desc =v1.findViewById(R.id.org_desc_alert_dialog);
                final AlertDialog.Builder org_name_input_alert_dialog =new AlertDialog.Builder(MainActivity.this,R.style.Alert_Dialog_BAckground);
                org_name_input_alert_dialog.setView(v1);
                org_name_input_alert_dialog.setTitle("Enter organization name")
                        .setMessage("You can't change this later")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(check_fields()){
                                    firebase.child("list_of_organizations")
                                            .child(org_name.getText().toString())
                                            .setValue(org_desc.getText().toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(MainActivity.this, "New organization added successfully.", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("ans error", Objects.requireNonNull(e.getMessage()));
                                                    Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }else{
                                    Toast.makeText(MainActivity.this,issues, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                org_name_input_alert_dialog.show();
            }
        });

        //settings
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,settings.class));
            }
        });
    }
    public boolean check_fields(){
        Boolean checked=false;
        if(org_name.getText()==null|| org_name.getText().toString().isEmpty() ||org_desc.getText()==null|| org_desc.getText().toString().isEmpty()){
            issues="Empty fields";
            return false;
        }
        String org=org_name.getText().toString();
        String desc=org_desc.getText().toString();
        for (int i = 0; i <org.length(); i++) {
            if (org.charAt(i)!=' '){
                checked=true;
            }
        }
        for (int i = 0; i <desc.length(); i++) {
            if (desc.charAt(i)!=' '){
                if (checked){
                    return true;
                }
            }
        }
        issues="Either one of the filed must contain a character";
        return false;
    }

    //on item click being override
    @Override
    public void onItemclick(int postion) {
        Log.e("ans clicks--<><>",postion+"");
        Intent intent=new Intent(MainActivity.this,sub_organization.class);
        intent.putExtra("org",arr.get(postion).name);
        startActivity(intent);
    }
}