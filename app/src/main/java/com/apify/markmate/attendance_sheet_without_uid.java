package com.apify.markmate;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
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

public class attendance_sheet_without_uid extends AppCompatActivity implements RecyclerViewInterface_date_attendance{
    RecyclerView recyclerView_attendance;
    RecyclerView recyclerView_dates;
    boolean check;
    ArrayList<attendance_data_without_uid> attendance_arr;
    AppCompatButton settings_at_attendance_sheet_without_uid;
    AppCompatButton add_date_at_attendance_sheet_without_uid;
    ArrayList<String>dates_arr;
    int start;
    int end;


    String org;
    String sub_org;
    DatabaseReference sub_org_details;
    String date_from_date_picker_input_date;
    AppCompatButton save_attendance_without_uid;
    FirebaseAuth firebaseAuth;
    LinearLayout layout;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference date_refreance;
    Intent intent;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendance_sheet_without_uid);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //checking if the user is signed in or not and if not directing him to either login or signup
        firebaseAuth=FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()==null){
            Log.e("ans jump","1");
            startActivity(new Intent(attendance_sheet_without_uid.this,loading_page.class));
            finishAffinity();
        }


        intent=getIntent();
        attendance_arr=new ArrayList<>();
        org=intent.getStringExtra("org");
        sub_org=intent.getStringExtra("sub_org");
        dates_arr=new ArrayList<>();
        layout=findViewById(R.id.attendance_layout_without_uid);
        settings_at_attendance_sheet_without_uid=findViewById(R.id.settings_at_attendance_sheet_without_uid);
        add_date_at_attendance_sheet_without_uid=findViewById(R.id.add_date_at_attendance_sheet_without_uid);
        save_attendance_without_uid=findViewById(R.id.save_attendance_without_uid);
        firebaseDatabase=FirebaseDatabase.getInstance("https://markmate-5452c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        sub_org_details=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("list_of_sub_organization").child(intent.getStringExtra("sub_org"));
        date_refreance=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("sub_organization").child(intent.getStringExtra("sub_org"));
        recyclerView_attendance=findViewById(R.id.attendance_recycler);
        recyclerView_attendance.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_dates=findViewById(R.id.dates_recycler_at_attendance_sheet_without_uid);
        recyclerView_dates.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        save_attendance_without_uid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<attendance_arr.size();i++){
                    databaseReference.child(attendance_arr.get(i).sr_no).setValue(attendance_arr.get(i).present).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("ans failed",e.toString());
                            Toast.makeText(attendance_sheet_without_uid.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        date_refreance.child("attendance_dates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dates_arr.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    dates_arr.add(ds.getKey());
                }
                Log.e("ans fetched",dates_arr.toString());
                dates_recycler_view r=new dates_recycler_view(attendance_sheet_without_uid.this,dates_arr,attendance_sheet_without_uid.this::onItemclick);
                recyclerView_dates.setAdapter(r);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ans error on 2",error.toString());
            }
        });
        settings_at_attendance_sheet_without_uid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(attendance_sheet_without_uid.this,com.apify.markmate.settings.class));
            }
        });
        add_date_at_attendance_sheet_without_uid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View v1= LayoutInflater.from(attendance_sheet_without_uid.this).inflate(R.layout.date_picker,null);
                DatePicker datePicker=v1.findViewById(R.id.datepicker);
                AlertDialog.Builder alert=new AlertDialog.Builder(attendance_sheet_without_uid.this);
                alert.setView(v1);
                alert.setCancelable(false);
                alert.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        date_from_date_picker_input_date=datePicker.getDayOfMonth()+"_"+datePicker.getMonth()+"_"+datePicker.getYear();
                        if (!dates_arr.contains(date_from_date_picker_input_date)){
                            Log.e("o---------ch","1");
                            date_refreance.child("attendance_dates").child(date_from_date_picker_input_date).setValue("-1")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(attendance_sheet_without_uid.this,"date "+date_from_date_picker_input_date.replace("_","/") + " added", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("ans-------------",e.toString());
                                            Toast.makeText(attendance_sheet_without_uid.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
                                        HashMap<String, HashMap<String,String>>hashMap=new HashMap<>();
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
                                                        Toast.makeText(attendance_sheet_without_uid.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
                                                        Toast.makeText(attendance_sheet_without_uid.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(attendance_sheet_without_uid.this,error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            Toast.makeText(attendance_sheet_without_uid.this, "date already exists", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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
    public void onItemclick(int postion,int i) {
        if(i==1){
            databaseReference=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("sub_organization").child(intent.getStringExtra("sub_org")).child("attendance_sheet").child(dates_arr.get(postion));
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    attendance_arr.clear();
                    for (DataSnapshot ds:snapshot.getChildren()){
                        attendance_arr.add(new attendance_data_without_uid(ds.getKey(),Boolean.valueOf(ds.getValue().toString())));
                    }
                    attendance_recycler_view_without_uid r=new attendance_recycler_view_without_uid(attendance_sheet_without_uid.this,attendance_arr,attendance_sheet_without_uid.this::onItemclick);
                    recyclerView_attendance.setAdapter(r);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ans error on 2",error.toString());
                }
            });
            layout.setVisibility(View.VISIBLE);
        }
        else if (i==2){
            attendance_arr.get(postion).present=!attendance_arr.get(postion).present;
            Log.e("ans clicked",postion+"");
        }
        /*
        databaseReference.child(attendance.get(postion).sr_no).setValue(!attendance.get(postion).present).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("ans failed",e.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.e("ans success","");
            }
        });
         */
    }
}