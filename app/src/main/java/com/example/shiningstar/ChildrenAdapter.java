package com.example.shiningstar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChildrenAdapter extends RecyclerView.Adapter<ChildrenAdapter.ChildrenViewHolder> {
    private Context mCtx;
    private List<ChildrenData> childrenList= new ArrayList<>();

    public ChildrenAdapter(Context mCtx, List<ChildrenData> childrenList){
        this.mCtx = mCtx;
        this.childrenList = childrenList;
    }


    @NonNull
    @Override
    public ChildrenAdapter.ChildrenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChildrenViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.children_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChildrenAdapter.ChildrenViewHolder holder, int position) {
        ChildrenData child = childrenList.get(position);

        holder.nam.setText(child.getName());

    }

    @Override
    public int getItemCount() {
        return childrenList.size();
    }

    public class ChildrenViewHolder extends RecyclerView.ViewHolder {
        TextView nam;

        public ChildrenViewHolder(View itemView){
            super(itemView);

            nam = itemView.findViewById(R.id.childname);
        }
    }
}

