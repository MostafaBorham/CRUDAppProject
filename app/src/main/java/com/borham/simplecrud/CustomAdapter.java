package com.borham.simplecrud;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    public static int row_index = -1;
    private final ArrayList<DatabaseFormat> localDataSet;

    public CustomAdapter(ArrayList<DatabaseFormat> dataSet) {
        localDataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.idTxt.setText("ID: " + localDataSet.get(viewHolder.getAdapterPosition()).getId() + "");
        viewHolder.nameTxt.setText("Name: " + localDataSet.get(viewHolder.getAdapterPosition()).getName());
        viewHolder.ageTxt.setText("Age: " + localDataSet.get(viewHolder.getAdapterPosition()).getAge());
        viewHolder.cityTxt.setText("City: " + localDataSet.get(viewHolder.getAdapterPosition()).getCity());
        viewHolder.itemView.setOnClickListener(v -> {
            row_index = viewHolder.getAdapterPosition();
            notifyDataSetChanged();
        });

        if (row_index == viewHolder.getAdapterPosition()) {
            viewHolder.itemView.setBackgroundColor(Color.parseColor("#83EE00E2"));
        } else {
            viewHolder.itemView.setBackgroundColor(Color.parseColor("#8F6200EE"));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView idTxt, nameTxt, ageTxt, cityTxt;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            view.setClickable(true);
            idTxt = view.findViewById(R.id.idTxt);
            nameTxt = view.findViewById(R.id.nameTxt);
            ageTxt = view.findViewById(R.id.ageTxt);
            cityTxt = view.findViewById(R.id.cityTxt);
        }

    }
}

