package com.apify.markmate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.security.auth.login.LoginException;

public class date_sheet extends AppCompatActivity implements RecyclerViewInterface{
    RecyclerView recyclerView;

    int start;
    String date_from_date_picker_input_date;
    int end;
    boolean check;
    ArrayList<String>dates;
    FirebaseAuth firebaseAuth;
    AppCompatButton appCompatButton;
    AppCompatButton settings;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference sub_org_details;
    String org;
    String sub_org;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_date_sheet);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //checking if the user is signed in or not and if not directing him to either login or signup
        firebaseAuth= FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()==null){
            Log.e("ans jump","1");
            startActivity(new Intent(date_sheet.this,loading_page.class));
            finishAffinity();
        }


        Intent intent=getIntent();
        org=intent.getStringExtra("org");
        sub_org=intent.getStringExtra("sub_org");

        appCompatButton=findViewById(R.id.tap);
        settings=findViewById(R.id.settings_dates);
        dates=new ArrayList<>();
        firebaseDatabase=FirebaseDatabase.getInstance("https://markmate-5452c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        recyclerView=findViewById(R.id.dates_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("sub_organization").child(intent.getStringExtra("sub_org"));
        sub_org_details=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("list_of_sub_organization").child(intent.getStringExtra("sub_org"));
        databaseReference.child("attendance_dates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dates.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    dates.add(ds.getKey());
                }
                Log.e("ans fetched",dates.toString());
                dates_recycler_view r=new dates_recycler_view(date_sheet.this,dates,date_sheet.this::onItemclick);
                recyclerView.setAdapter(r);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ans error on 2",error.toString());
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(date_sheet.this,com.apify.markmate.settings.class));
            }
        });
        appCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View v1= LayoutInflater.from(date_sheet.this).inflate(R.layout.date_picker,null);
                DatePicker datePicker=v1.findViewById(R.id.datepicker);
                AlertDialog.Builder alert=new AlertDialog.Builder(date_sheet.this);
                alert.setView(v1);
                alert.setCancelable(false);
                alert.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        date_from_date_picker_input_date=datePicker.getDayOfMonth()+"_"+datePicker.getMonth()+"_"+datePicker.getYear();

                        if (!dates.contains(date_from_date_picker_input_date)){
                            Log.e("o---------ch","1");
                            databaseReference.child("attendance_dates").child(date_from_date_picker_input_date).setValue("-1")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(date_sheet.this,"date "+date_from_date_picker_input_date.replace("_","/") + " added", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("ans-------------",e.toString());
                                            Toast.makeText(date_sheet.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            Log.e("o---------ch","2");
                            sub_org_details.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Log.e("ansp-----------------",snapshot.toString());
                                    start= Integer.parseInt(Objects.requireNonNull(snapshot.child("starting_sr_no").getValue()).toString());
                                    end= Integer.parseInt(Objects.requireNonNull(snapshot.child("ending_sr_no").getValue()).toString());
                                    check= Boolean.parseBoolean(Objects.requireNonNull(snapshot.child("checkBox").getValue()).toString());


                                    Log.e("o---------ch",3+""+start+"");
                                    Log.e("o---------ch",3+""+end+"");
                                    Log.e("o---------ch",3+""+check+"");
                                    if (check){
                                        Log.e("onClick:--------------","1");
                                        HashMap<String,HashMap<String,String>>hashMap=new HashMap<>();
                                        Log.e("adjf--------------",hashMap.toString());
                                        for (int i=start;i<=end;i++){
                                            HashMap<String,String>hs=new HashMap<>();
                                            hs.put("uid","uid");
                                            hs.put("checkbox","false");
                                            hashMap.put(String.valueOf(i),hs);
                                        }
                                        Log.e("onClick:--------------",hashMap.toString());
                                        databaseReference.child("attendance_sheet").child(date_from_date_picker_input_date).setValue(hashMap)
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(date_sheet.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        Log.e("onClick:--------------","final");
                                    }
                                    else {
                                        HashMap<String,Boolean>hashMap=new HashMap<>();
                                        for (int i=start;i<=end;i++){
                                            hashMap.put(String.valueOf(i),false);
                                        }
                                        databaseReference.child("attendance_sheet").child(date_from_date_picker_input_date).setValue(hashMap)
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(date_sheet.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(date_sheet.this,error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            Toast.makeText(date_sheet.this, "date already exists", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("CANCE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
    }
    @Override
    public void onItemclick(int postion) {
        sub_org_details.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("oooooo---------------------------",snapshot.toString());
                Log.e("oooooo---------------------------",snapshot.child("checkBox").getValue().toString());
                Log.e("at------------->>>>>>>>>","1");
                if (Boolean.parseBoolean(Objects.requireNonNull(snapshot.child("checkBox").getValue()).toString())){
                    Intent intent=new Intent(date_sheet.this, attendance_sheet_with_uid.class);
                    intent.putExtra("org",org);
                    intent.putExtra("sub_org",sub_org);
                    intent.putExtra("date",dates.get(postion));
                    startActivity(intent);
                }else{
                    Intent intent=new Intent(date_sheet.this, attendance_sheet_without_uid.class);
                    intent.putExtra("org",org);
                    intent.putExtra("sub_org",sub_org);
                    intent.putExtra("date",dates.get(postion));
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error--------------------",error.toString());
                Toast.makeText(date_sheet.this,error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}