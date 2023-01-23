package com.sergivonavi.materialbanner.app.activities.adapter

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sergivonavi.materialbanner.app.R

internal class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    var image: ImageView

    init {
        image = itemView.findViewById(R.id.item_image)
    }
}