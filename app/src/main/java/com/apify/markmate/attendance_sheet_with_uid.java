package com.apify.markmate;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.ScrollingTabContainerView;
import androidx.core.content.ContextCompat;
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
import com.hbb20.R.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class attendance_sheet_with_uid extends AppCompatActivity implements RecyclerViewInterface_date_attendance{
    RecyclerView recyclerView_attendance;
    RecyclerView recyclerView_dates;
    boolean check;
    AppCompatButton attendance_settings;
    AppCompatButton change_uid;
    ArrayList<String>sr_no_list;
    AppCompatButton count_total_attendance;
    AppCompatButton present_all_attendance;
    AppCompatButton reset_all_attendance;
    AppCompatButton absent_all_attendance;
    Intent intent;
    int current_date_index;
    ArrayList<attendance_data_with_uid> attendance_arr;

    AppCompatButton settings_at_attendance_sheet_with_uid;
    AppCompatButton add_date_at_attendance_sheet_with_uid;
    ArrayList<String>dates_arr;
    String selected;
    int start;
    int end;
    String org;
    String sub_org;
    FirebaseAuth firebaseAuth;
    DatabaseReference sub_org_details;
    String date_from_date_picker_input_date;
    LinearLayout layout;
    HashMap<String,String>uid_hashmap=new HashMap<>();

    DatabaseReference date_reference;
    DatabaseReference uid_changing;

    AppCompatButton save_attendance_with_uid;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendance_sheet_with_uid);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //setting status bar color to dark green for better look
        getWindow().setStatusBarColor(ContextCompat.getColor(attendance_sheet_with_uid.this,R.color.dark_green));

        //checking if the user is signed in or not and if not directing him to either login or signup
        firebaseAuth=FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()==null){
            Log.e("ans jump","1");
            startActivity(new Intent(attendance_sheet_with_uid.this,loading_page.class));
            finishAffinity();
        }


        intent=getIntent();
        attendance_arr=new ArrayList<>();
        org=intent.getStringExtra("org");
        uid_hashmap= (HashMap<String, String>) intent.getSerializableExtra("uid_hashmap");
        sub_org=intent.getStringExtra("sub_org");
        dates_arr=new ArrayList<>();
        layout=findViewById(R.id.attendance_layout_with_uid);
        sr_no_list=new ArrayList<>();
        settings_at_attendance_sheet_with_uid=findViewById(R.id.settings_at_attendance_sheet_with_uid);
        add_date_at_attendance_sheet_with_uid=findViewById(R.id.add_date_at_attendance_sheet_with_uid);
        save_attendance_with_uid=findViewById(R.id.save_attendance_with_uid);
        firebaseDatabase=FirebaseDatabase.getInstance("https://markmate-5452c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        sub_org_details=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("list_of_sub_organization").child(intent.getStringExtra("sub_org"));
        uid_changing=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("list_of_sub_organization").child(intent.getStringExtra("sub_org")).child("uid_hashmap");
        date_reference=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("sub_organization").child(intent.getStringExtra("sub_org"));
        recyclerView_attendance=findViewById(R.id.attendance_recycler);
        recyclerView_attendance.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_dates=findViewById(R.id.dates_recycler_at_attendance_sheet_with_uid);
        recyclerView_dates.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        save_attendance_with_uid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<attendance_arr.size();i++){
                    databaseReference.child(attendance_arr.get(i).sr_no).child("checkbox").setValue(attendance_arr.get(i).present).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("ans failed",e.toString());
                            Toast.makeText(attendance_sheet_with_uid.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
                }
                Toast.makeText(attendance_sheet_with_uid.this, "attendance saved", Toast.LENGTH_SHORT).show();
            }
        });
        date_reference.child("attendance_dates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dates_arr.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    dates_arr.add(ds.getKey());
                }
                Log.e("ans fetched",dates_arr.toString());
                dates_recycler_view r=new dates_recycler_view(attendance_sheet_with_uid.this,dates_arr,attendance_sheet_with_uid.this::onItemclick);
                recyclerView_dates.setAdapter(r);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ans error on 2",error.toString());
            }
        });
        settings_at_attendance_sheet_with_uid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(attendance_sheet_with_uid.this,com.apify.markmate.settings.class));
            }
        });
        add_date_at_attendance_sheet_with_uid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("sub_organization").child(intent.getStringExtra("sub_org"));
                View v1= LayoutInflater.from(attendance_sheet_with_uid.this).inflate(R.layout.date_picker,null);
                DatePicker datePicker=v1.findViewById(R.id.datepicker);
                AlertDialog.Builder alert=new AlertDialog.Builder(attendance_sheet_with_uid.this);
                alert.setView(v1);
                alert.setCancelable(false);
                alert.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        date_from_date_picker_input_date=datePicker.getDayOfMonth()+"_"+datePicker.getMonth()+"_"+datePicker.getYear();
                        if (!dates_arr.contains(date_from_date_picker_input_date)){
                            Log.e("o---------ch","1");
                            date_reference.child("attendance_dates").child(date_from_date_picker_input_date).setValue("-1")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(attendance_sheet_with_uid.this,"date "+date_from_date_picker_input_date.replace("_","/") + " added", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("ans-------------",e.toString());
                                            Toast.makeText(attendance_sheet_with_uid.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                    Log.e("o---------ch", 3+""+start);
                                    Log.e("o---------ch", 3+""+end);
                                    Log.e("o---------ch", 3+""+check);
                                    HashMap<String,Boolean>hashMap=new HashMap<>();
                                    for (int i=start;i<=end;i++){
                                        hashMap.put(String.valueOf(i),false);
                                    }
                                    date_reference=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("sub_organization").child(intent.getStringExtra("sub_org"));
                                    databaseReference.child("attendance_sheet").child(date_from_date_picker_input_date).setValue(hashMap)
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(attendance_sheet_with_uid.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(attendance_sheet_with_uid.this,error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            Toast.makeText(attendance_sheet_with_uid.this, "date already exists", Toast.LENGTH_SHORT).show();
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

        attendance_settings=findViewById(R.id.attendance_settings_at_attendance_sheet_with_uid);
        attendance_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View v1=LayoutInflater.from(attendance_sheet_with_uid.this).inflate(R.layout.attendance_settings_layout,null);
                change_uid=v1.findViewById(R.id.change_uid);
                count_total_attendance=v1.findViewById(R.id.count_total_attendance);
                reset_all_attendance=v1.findViewById(R.id.reset_all_attendance);
                present_all_attendance=v1.findViewById(R.id.present_all_attendance);
                absent_all_attendance=v1.findViewById(R.id.absent_all_attendance);
                AlertDialog.Builder alert=new AlertDialog.Builder(attendance_sheet_with_uid.this);
                alert.setView(v1);
                alert.setCancelable(true);
                alert.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                change_uid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View v1=LayoutInflater.from(attendance_sheet_with_uid.this).inflate(R.layout.change_uid_at_attendance_sheet,null);
                        Spinner autoCompleteTextView=v1.findViewById(R.id.autoComplete);
                        EditText editText=v1.findViewById(R.id.enter_uid);
                        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(attendance_sheet_with_uid.this, com.hbb20.R.layout.support_simple_spinner_dropdown_item,sr_no_list);
                        autoCompleteTextView.setAdapter(arrayAdapter);
                        AlertDialog.Builder alert=new AlertDialog.Builder(attendance_sheet_with_uid.this);
                        selected=null;
                        autoCompleteTextView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selected=sr_no_list.get(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                selected=null;
                            }
                        });
                        alert.setView(v1)
                                .setCancelable(false)
                                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(selected!=null&&editText.getText()!=null&&editText.getText().length()>0){
                                            uid_changing.child(selected).setValue(editText.getText().toString())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.e("sucess------------------","sucess");
                                                            Toast.makeText(attendance_sheet_with_uid.this, "UID changed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e( "onFailure:-------------------",e.toString());
                                                            Toast.makeText(attendance_sheet_with_uid.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                })
                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.e("onClick:------------------",sr_no_list.toString());
                                        dialog.dismiss();
                                    }
                                });
                        alert.show();
                        Toast.makeText(attendance_sheet_with_uid.this, "1", Toast.LENGTH_SHORT).show();
                    }
                });
                count_total_attendance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(attendance_sheet_with_uid.this, "2", Toast.LENGTH_SHORT).show();
                    }
                });
                reset_all_attendance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseReference=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("sub_organization").child(intent.getStringExtra("sub_org")).child("attendance_sheet").child(dates_arr.get(current_date_index));
                        Log.e("hit ---------------","1");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Log.e("ppppppppppp>>>>>>>>>>>>>>>>",snapshot.toString());
                                attendance_arr.clear();
                                Log.e("on error>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",snapshot.toString());
                                for (DataSnapshot ds:snapshot.getChildren()){
                                    Log.e("on error--------------------------",ds.toString());
                                    attendance_arr.add(new attendance_data_with_uid(ds.getKey(),Boolean.valueOf(ds.child("checkbox").getValue().toString()),ds.child("uid").getValue().toString()));
                                }
                                attendance_recycler_view_with_uid r=new attendance_recycler_view_with_uid(attendance_sheet_with_uid.this,attendance_arr,attendance_sheet_with_uid.this::onItemclick);
                                recyclerView_attendance.setAdapter(r);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("ans error on 2",error.toString());
                                Toast.makeText(attendance_sheet_with_uid.this,error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(attendance_sheet_with_uid.this, "Reset all done", Toast.LENGTH_SHORT).show();
                    }
                });
                present_all_attendance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i=0;i<attendance_arr.size();i++){
                            attendance_arr.get(i).present=true;
                        }
                        attendance_recycler_view_with_uid r=new attendance_recycler_view_with_uid(attendance_sheet_with_uid.this,attendance_arr,attendance_sheet_with_uid.this::onItemclick);
                        recyclerView_attendance.setAdapter(r);
                        Toast.makeText(attendance_sheet_with_uid.this, "Present all done", Toast.LENGTH_SHORT).show();
                    }
                });
                absent_all_attendance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i=0;i<attendance_arr.size();i++){
                            attendance_arr.get(i).present=false;
                        }
                        attendance_recycler_view_with_uid r=new attendance_recycler_view_with_uid(attendance_sheet_with_uid.this,attendance_arr,attendance_sheet_with_uid.this::onItemclick);
                        recyclerView_attendance.setAdapter(r);
                        Toast.makeText(attendance_sheet_with_uid.this, "Absent all done", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
        });
        
    }
    @Override
    public void onItemclick(int postion,int i) {
        if (i==1){
            current_date_index=postion;
            databaseReference=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("sub_organization").child(intent.getStringExtra("sub_org")).child("attendance_sheet").child(dates_arr.get(postion));
            Log.e("hit ---------------","1");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.e("ppppppppppp>>>>>>>>>>>>>>>>",snapshot.toString());
                    if (snapshot.getValue()==null){
                        databaseReference=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("sub_organization").child(intent.getStringExtra("sub_org"));
                        sub_org_details.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Log.e("ansp-----------------",snapshot.toString());
                                start= Integer.parseInt(Objects.requireNonNull(snapshot.child("starting_sr_no").getValue()).toString());
                                end= Integer.parseInt(Objects.requireNonNull(snapshot.child("ending_sr_no").getValue()).toString());
                                check= Boolean.parseBoolean(Objects.requireNonNull(snapshot.child("checkBox").getValue()).toString());
                                Log.e("o---------ch", 3+""+start);
                                Log.e("o---------ch", 3+""+end);
                                Log.e("o---------ch", 3+""+check);
                                HashMap<String,Boolean>hashMap=new HashMap<>();
                                for (int i=start;i<=end;i++){
                                    hashMap.put(String.valueOf(i),false);
                                }
                                date_reference=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("sub_organization").child(intent.getStringExtra("sub_org"));
                                databaseReference.child("attendance_sheet").child(date_from_date_picker_input_date).setValue(hashMap)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(attendance_sheet_with_uid.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(attendance_sheet_with_uid.this,error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    attendance_arr.clear();
                    sr_no_list.clear();
                    Log.e("on error>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",snapshot.toString());
                    for (DataSnapshot ds:snapshot.getChildren()){
                        sr_no_list.add(ds.getKey());
                        Log.e("on error--------------------------",ds.toString());
                        Log.e("on error--------------------------",uid_hashmap.toString());
                        attendance_arr.add(new attendance_data_with_uid(ds.getKey(),Boolean.valueOf(ds.getValue().toString()),uid_hashmap.get(ds.getKey())));
                    }
                    attendance_recycler_view_with_uid r=new attendance_recycler_view_with_uid(attendance_sheet_with_uid.this,attendance_arr,attendance_sheet_with_uid.this::onItemclick);
                    recyclerView_attendance.setAdapter(r);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ans error on 2",error.toString());
                }
            });
            layout.setVisibility(View.VISIBLE);
        }else if(i==2){
            attendance_arr.get(postion).present=!attendance_arr.get(postion).present;
            Log.e("ans changed",attendance_arr.toString());
        }
    }
}