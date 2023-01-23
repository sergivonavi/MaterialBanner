package com.sergivonavi.materialbanner.app.activities.adapter;

import android.view.View;
import android.widget.ImageView;

import com.sergivonavi.materialbanner.app.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class ItemViewHolder extends RecyclerView.ViewHolder {

    ImageView image;

    ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.item_image);
    }

}
