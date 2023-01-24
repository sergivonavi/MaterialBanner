package com.sergivonavi.materialbanner.app.activities.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sergivonavi.materialbanner.app.R
import com.squareup.picasso.Picasso
import java.util.*

internal class ItemsAdapter(context: Context?) : RecyclerView.Adapter<ItemViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    private val mList: MutableList<String?> = ArrayList()

    init {
        mLayoutInflater = LayoutInflater.from(context)

        // Ensure we get a different ordering of images on each run.
        Collections.addAll(mList, *SampleData.ASSETS)
        mList.shuffle()

        // Triple up the list.
        val copy = ArrayList(mList)
        mList.addAll(copy)
        mList.addAll(copy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(mLayoutInflater.inflate(R.layout.item_list, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.image.post {
            Picasso.get()
                .load(mList[position])
                .resize(holder.image.width, holder.image.height)
                .centerCrop()
                .into(holder.image)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}