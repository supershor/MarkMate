package com.apify.markmate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

public class sub_organization extends AppCompatActivity implements RecyclerViewInterface{
    FirebaseDatabase firebaseDatabase;
    AppCompatButton appCompatButton;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ArrayList<list> arr;
    RecyclerView recyclerView;
    String org;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sub_organization);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //checking if the user is signed in or not and if not directing him to either login or signup
        firebaseAuth=FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()==null){
            Log.e("ans jump","1");
            startActivity(new Intent(sub_organization.this,loading_page.class));
            finishAffinity();
        }

        //receiving intent passed from organization and initializing elements
        Intent intent=getIntent();
        org=intent.getStringExtra("org");
        arr=new ArrayList<>();
        appCompatButton=findViewById(R.id.add_new_sub_organization);
        firebaseDatabase=FirebaseDatabase.getInstance("https://markmate-5452c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        recyclerView=findViewById(R.id.recycler_sub_organization_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //setting values of recycler view using firebase
        databaseReference = firebaseDatabase.getReference("USER DATA").child(firebaseAuth.getCurrentUser().getUid()).child("organization").child(intent.getStringExtra("org"));
        databaseReference.child("list_of_sub_organization").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arr.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    arr.add(new list(ds.getKey(), ds.child("sub_org_desc").getValue().toString()));
                    Log.e("ans values--<><><><>",ds.toString());
                    Log.e("ans values--<><><><>",ds.child("sub_org_desc").getValue().toString());
                }
                Recycler r=new Recycler(sub_organization.this,arr,sub_organization.this::onItemclick);
                recyclerView.setAdapter(r);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ans error", error.getMessage());
                Toast.makeText(sub_organization.this,error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //on add new org button click
        appCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View v1= LayoutInflater.from(sub_organization.this).inflate(R.layout.sub_org_name_desc_alert_dialog_view,null);

                EditText sub_org_name =v1.findViewById(R.id.sub_org_name_alert_dialog);
                EditText sub_org_desc =v1.findViewById(R.id.sub_org_desc_alert_dialog);
                EditText starting_sr_no =v1.findViewById(R.id.starting_sr_no);
                EditText ending_sr_no =v1.findViewById(R.id.ending_sr_no);
                CheckBox checkBox=v1.findViewById(R.id.has_unique_id_checkbox);

                final AlertDialog.Builder sub_org_name_desc_input_taker =new AlertDialog.Builder(sub_organization.this,R.style.Alert_Dialog_BAckground);
                sub_org_name_desc_input_taker.setView(v1);
                sub_org_name_desc_input_taker.setTitle("Sub-Organization")
                        .setMessage("You can't change this later")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(sub_org_name.getText()!=null&& !sub_org_name.getText().toString().isEmpty() &&sub_org_desc.getText()!=null&& !sub_org_desc.getText().toString().isEmpty()&&starting_sr_no.getText()!=null&& !starting_sr_no.getText().toString().isEmpty() &&ending_sr_no.getText()!=null&& !ending_sr_no.getText().toString().isEmpty()){
                                    HashMap<String,String> hashMap=new HashMap<>();
                                    hashMap.put("sub_org_name",sub_org_name.getText().toString());
                                    hashMap.put("sub_org_desc",sub_org_desc.getText().toString());
                                    hashMap.put("starting_sr_no",starting_sr_no.getText().toString());
                                    hashMap.put("ending_sr_no",ending_sr_no.getText().toString());
                                    if (checkBox.isChecked()){
                                        hashMap.put("checkBox","true");
                                    }else {
                                        hashMap.put("checkBox","false");
                                    }
                                    databaseReference.child("list_of_sub_organization")
                                            .child(sub_org_name.getText().toString())
                                            .setValue(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(sub_organization.this, "New sub-organization added successfully.", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("ans error", Objects.requireNonNull(e.getMessage()));
                                                    Toast.makeText(sub_organization.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }else{
                                    Toast.makeText(sub_organization.this,"Empty field", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                sub_org_name_desc_input_taker.show();
            }
        });
    }

    @Override
    public void onItemclick(int postion) {
        Log.e("ans clicked::::::", String.valueOf(postion));
        Intent intent=new Intent(sub_organization.this, date_sheet.class);
        intent.putExtra("org",org);
        intent.putExtra("sub_org",arr.get(postion).name);
        startActivity(intent);
    }
}