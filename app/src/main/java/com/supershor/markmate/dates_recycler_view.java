package com.supershor.markmate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class dates_recycler_view extends RecyclerView.Adapter<dates_recycler_view.ViewHolder> {
    Context context;
    ArrayList<String>arr;
    private final RecyclerViewInterface_date_attendance recyclerViewInterface;
    public dates_recycler_view(Context context, ArrayList<String>arr, RecyclerViewInterface_date_attendance recyclerViewInterface){
        this.context=context;
        this.arr=arr;
        this.recyclerViewInterface= recyclerViewInterface;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.dates_attendance_sheet,parent,false);
        ViewHolder viewHolder=new ViewHolder(view,recyclerViewInterface);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.date.setText(arr.get(position).replace("_","/"));
    }



    @Override
    public int getItemCount() {
        return arr.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        public ViewHolder(@NonNull View itemView,RecyclerViewInterface_date_attendance recyclerViewInterface) {
            super(itemView);
            date=itemView.findViewById(R.id.date_info);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemclick(position,1);
                        }
                    }
                }
            });
        }
    }
}
