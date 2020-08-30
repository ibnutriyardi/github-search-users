package com.github.search.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.github.search.R
import com.github.search.data.model.SearchItem

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    var items = mutableListOf<SearchItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun addItems(searchItems: List<SearchItem>) {
        items.addAll(searchItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_search, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchItem = items[position]
        holder.bindData(searchItem)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgAvatar: AppCompatImageView = itemView.findViewById(R.id.img_avatar)
        private val txtName: AppCompatTextView = itemView.findViewById(R.id.txt_name)

        fun bindData(searchItem: SearchItem) {
            Glide.with(itemView)
                .load(searchItem.avatarUrl)
                .transform(CircleCrop())
                .into(imgAvatar)

            txtName.text = searchItem.login
        }
    }
}