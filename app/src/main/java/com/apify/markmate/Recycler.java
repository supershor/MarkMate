package com.apify.markmate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Recycler extends RecyclerView.Adapter<Recycler.ViewHolder> {
    Context context;
    ArrayList<list>arr;
    private final RecyclerViewInterface recyclerViewInterface;
    public Recycler(Context context, ArrayList<list>arr, RecyclerViewInterface recyclerViewInterface){
        this.context=context;
        this.arr=arr;
        this.recyclerViewInterface= recyclerViewInterface;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.org_recycler_layout,parent,false);
        ViewHolder viewHolder=new ViewHolder(view,recyclerViewInterface);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(arr.get(position).name);
        holder.description.setText(arr.get(position).description);
    }



    @Override
    public int getItemCount() {
        return arr.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        public ViewHolder(@NonNull View itemView,RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            name=itemView.findViewById(R.id.org_name);
            description=itemView.findViewById(R.id.org_desc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemclick(position);
                        }
                    }
                }
            });
        }
    }
}
