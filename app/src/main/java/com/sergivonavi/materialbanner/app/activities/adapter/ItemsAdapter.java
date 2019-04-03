package com.sergivonavi.materialbanner.app.activities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sergivonavi.materialbanner.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemsAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private LayoutInflater mLayoutInflater;
    private final List<String> mList = new ArrayList<>();

    public ItemsAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);

        // Ensure we get a different ordering of images on each run.
        Collections.addAll(mList, SampleData.ASSETS);
        Collections.shuffle(mList);

        // Triple up the list.
        ArrayList<String> copy = new ArrayList<>(mList);
        mList.addAll(copy);
        mList.addAll(copy);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(mLayoutInflater.inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        holder.image.post(new Runnable() {
            @Override
            public void run() {
                Picasso.get()
                        .load(mList.get(position))
                        .resize(holder.image.getWidth(), holder.image.getHeight())
                        .centerCrop()
                        .into(holder.image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
