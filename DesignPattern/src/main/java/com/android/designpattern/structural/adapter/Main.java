package com.android.designpattern.structural.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Main {

    public class MAdapter extends RecyclerView.Adapter<MViewHolder> {

        @NonNull
        @Override
        public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull MViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    public static class MViewHolder extends RecyclerView.ViewHolder {

        public MViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
