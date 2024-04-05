package com.supershor.markmate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class total_attendance extends AppCompatActivity {
    String org;
    String sub_org;
    Boolean has_uid;
    ArrayList<String>dates_arr;
    FirebaseAuth firebaseAuth;

    HashMap<String ,Integer>total_present;
    HashMap<String,String> uid_hashmap;
    ArrayList<String>sr_no_list;
    Integer total;

    Intent intent;
    FirebaseDatabase firebaseDatabase;
    RecyclerView recyclerView;
    TextView uid_setter_main;
    DatabaseReference date_Reference;
    DatabaseReference attendance_reference;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_total_attendance);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(ContextCompat.getColor(total_attendance.this,R.color.dark_green));

        //checking if the user is signed in or not and if not directing him to either login or signup
        firebaseAuth=FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()==null){
            Log.e("ans jump","1");
            startActivity(new Intent(total_attendance.this,loading_page.class));
            finishAffinity();
        }

        intent=getIntent();
        uid_setter_main=findViewById(R.id.uid_setter_main);
        recyclerView=findViewById(R.id.count_total_attendance);
        total_present=new HashMap<>();
        org=intent.getStringExtra("org");
        total=0;
        has_uid=intent.getBooleanExtra("has_uid",false);
        sub_org=intent.getStringExtra("sub_org");
        dates_arr= (ArrayList<String>) intent.getSerializableExtra("dates_arr");
        sr_no_list= (ArrayList<String>) intent.getSerializableExtra("sr_no_list");
        uid_hashmap= (HashMap<String, String>) intent.getSerializableExtra("uid_hashmap");
        firebaseDatabase=FirebaseDatabase.getInstance("https://markmate-5452c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        attendance_reference=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("sub_organization").child(intent.getStringExtra("sub_org")).child("attendance_sheet");
        date_Reference=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("sub_organization").child(intent.getStringExtra("sub_org")).child("attendance_dates");

        if (!has_uid){
            uid_setter_main.setVisibility(View.GONE);
        }
        date_Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dates:snapshot.getChildren()) {
                    attendance_reference.child(dates.getKey().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.e("ans---------------------",snapshot.toString());
                            for(DataSnapshot ds:snapshot.getChildren()){
                                Log.e("ds====================",ds.toString());
                                if (Boolean.parseBoolean(ds.getValue().toString())){
                                    total_present.put(ds.getKey(),total_present.getOrDefault(ds.getKey(),0)+1);
                                }else{
                                    total_present.put(ds.getKey(), total_present.getOrDefault(ds.getKey(), 0));
                                }
                                Log.e("hashmap111111111111111111111111",total_present.toString());

                            }
                            if (total==dates_arr.size()){
                                Log.e( "onDataChange: ", Boolean.valueOf(total==dates_arr.size())+"");
                                Log.e( "dates_arr onDataChange:------------",dates_arr.toString());
                                Log.e( "uid onDataChange:------------",uid_hashmap.toString());
                                Log.e( "sr_no_list onDataChange:------------",sr_no_list.toString());
                                Log.e( "total_present onDataChange:------------",total_present.toString());
                                Log.e( "has_uid onDataChange:------------",has_uid.toString());
                                Log.e( "total onDataChange:------------",total.toString());
                                total_attendance_recycler totalAttendanceRecycler=new total_attendance_recycler(total_attendance.this,uid_hashmap,has_uid,sr_no_list,total,total_present);
                                Log.e("i------------------------","@");
                                recyclerView.setLayoutManager(new LinearLayoutManager(total_attendance.this));
                                recyclerView.setAdapter(totalAttendanceRecycler);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });
                    total+=1;
                }
                Log.e("hashmap2222222222222222222222222222222222222222222",total_present.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void while_empty(){
        if (total!=dates_arr.size()){
            while_empty();
        }
        Log.e( "uid onDataChange:------------",uid_hashmap.toString());
        Log.e( "sr_no_list onDataChange:------------",sr_no_list.toString());
        Log.e( "total_present onDataChange:------------",total_present.toString());
        Log.e( "has_uid onDataChange:------------",has_uid.toString());
        Log.e( "total onDataChange:------------",total.toString());
        total_attendance_recycler totalAttendanceRecycler=new total_attendance_recycler(total_attendance.this,uid_hashmap,has_uid,sr_no_list,total,total_present);
        recyclerView.setAdapter(totalAttendanceRecycler);
        return;
    }
}