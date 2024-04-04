package com.apify.markmate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class total_attendance_recycler extends RecyclerView.Adapter<total_attendance_recycler.ViewHolder>{
    Context context;
    HashMap<String,String> uid_hashmap;
    Boolean has_uid;
    ArrayList<String>sr_no_list;
    Integer total;
    HashMap<String ,Integer>total_present;

    public total_attendance_recycler(Context context,HashMap<String,String> uid_hashmap,Boolean has_uid,ArrayList<String>sr_no_list,Integer total,HashMap<String ,Integer>total_present){
        this.context=context;
        this.uid_hashmap=uid_hashmap;
        this.has_uid=has_uid;
        this.sr_no_list=sr_no_list;
        this.total=total;
        this.total_present=total_present;
    }
    @NonNull
    @Override
    public total_attendance_recycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.total_attendance_element_layout,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull total_attendance_recycler.ViewHolder holder, int position) {
        holder.sr_no.setText(sr_no_list.get(position));
        if (has_uid){
            holder.uid.setText(uid_hashmap.get(sr_no_list.get(position)));
        }
        holder.present.setText(total_present.get(sr_no_list.get(position))+"");
        holder.absent.setText(total-total_present.get(sr_no_list.get(position))+"");
    }

    @Override
    public int getItemCount() {
        return sr_no_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sr_no;
        TextView uid;
        TextView present;
        TextView absent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sr_no=itemView.findViewById(R.id.sr_no_setter);
            uid=itemView.findViewById(R.id.uid_setter);
            present=itemView.findViewById(R.id.total_present_setter);
            absent=itemView.findViewById(R.id.total_absent_setter);
            if (!has_uid){
                uid.setVisibility(View.GONE);
            }
        }
    }
}
