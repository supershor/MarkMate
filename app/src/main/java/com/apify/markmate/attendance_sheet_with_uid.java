package com.apify.markmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

public class attendance_sheet_with_uid extends AppCompatActivity implements RecyclerViewInterface{
    RecyclerView recyclerView;
    ArrayList<attendance_data_with_uid> attendance;
    FirebaseAuth firebaseAuth;
    AppCompatButton save_attendance_with_uid;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
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
        //checking if the user is signed in or not and if not directing him to either login or signup
        firebaseAuth=FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()==null){
            Log.e("ans jump","1");
            startActivity(new Intent(attendance_sheet_with_uid.this,loading_page.class));
            finishAffinity();
        }


        Intent intent=getIntent();
        attendance=new ArrayList<>();
        save_attendance_with_uid=findViewById(R.id.save_attendance_with_uid);
        firebaseDatabase=FirebaseDatabase.getInstance("https://markmate-5452c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        recyclerView=findViewById(R.id.attendance_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference=firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org")).child("sub_organization").child(intent.getStringExtra("sub_org")).child("attendance_sheet").child(intent.getStringExtra("date"));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("ppppppppppp>>>>>>>>>>>>>>>>",snapshot.toString());
                attendance.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    attendance.add(new attendance_data_with_uid(ds.getKey(),Boolean.valueOf(ds.child("checkbox").getValue().toString()),ds.child("uid").getValue().toString()));
                }
                attendance_recycler_view_with_uid r=new attendance_recycler_view_with_uid(attendance_sheet_with_uid.this,attendance,attendance_sheet_with_uid.this::onItemclick);
                recyclerView.setAdapter(r);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ans error on 2",error.toString());
            }
        });

        save_attendance_with_uid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<attendance.size();i++){
                    databaseReference.child(attendance.get(i).sr_no).child("checkbox").setValue(attendance.get(i).present).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("ans failed",e.toString());
                            Toast.makeText(attendance_sheet_with_uid.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
    @Override
    public void onItemclick(int postion) {
        attendance.get(postion).present=!attendance.get(postion).present;
        Log.e("ans changed",attendance.toString());
        /*
        Log.e("tap1---------","1");
        Log.e("ans clicked",postion+"");
        databaseReference.child(attendance.get(postion).sr_no).child("checkbox").setValue(!attendance.get(postion).present).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("ans failed",e.toString());
                Toast.makeText(attendance_sheet_with_uid.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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