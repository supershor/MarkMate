package com.supershor.markmate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class attendance_recycler_view_with_uid extends RecyclerView.Adapter<attendance_recycler_view_with_uid.ViewHolder> {
    Context context;
    ArrayList<attendance_data_with_uid> arr;
    private final RecyclerViewInterface_date_attendance recyclerViewInterface;
    public attendance_recycler_view_with_uid(Context context, ArrayList<attendance_data_with_uid>arr, RecyclerViewInterface_date_attendance recyclerViewInterface){
        this.context=context;
        this.arr=arr;
        this.recyclerViewInterface= recyclerViewInterface;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.attendance_sheet_element_layout_with_uid,parent,false);
        ViewHolder viewHolder=new ViewHolder(view,recyclerViewInterface);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.sr_no.setText(arr.get(position).sr_no);
        holder.checkBox.setChecked(arr.get(position).present);
        holder.uid.setText(arr.get(position).uid);
    }



    @Override
    public int getItemCount() {
        return arr.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView sr_no;
        TextView uid;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView,RecyclerViewInterface_date_attendance recyclerViewInterface) {
            super(itemView);
            sr_no=itemView.findViewById(R.id.sr_no_attendance_sheet_with_uid);
            uid=itemView.findViewById(R.id.uid_attendance_sheet_with_uid);
            checkBox=itemView.findViewById(R.id.checkbox_attendance_sheet_with_uid);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            checkBox.setChecked(!checkBox.isChecked());
                            recyclerViewInterface.onItemclick(position,2);
                        }
                    }
                }
            });
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemclick(position,2);
                        }
                    }
                }
            });
        }
    }
}

