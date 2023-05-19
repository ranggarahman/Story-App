package com.example.storyapp.ui.storylist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.api.response.ListStoryItem
import com.example.storyapp.databinding.ItemStoryBinding

class StoryListAdapter
    : PagingDataAdapter<ListStoryItem, StoryListAdapter.StoryListViewHolder>(DIFF_CALLBACK){

    private lateinit var onItemClickCallback: OnItemClickCallback

    class StoryListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val binding = ItemStoryBinding.bind(itemView)

        val tvName = binding.tvUsername
        val imgPhoto = binding.ivPhoto
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryListViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StoryListViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.tvName.text = story.name
            Glide.with(holder.itemView.context)
                .load(story.photoUrl)
                .into(holder.imgPhoto)

            holder.itemView.setOnClickListener {
                story.id?.let { storyId ->
                    onItemClickCallback.onItemClicked(storyId, holder)
                }
            }
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback{
        fun onItemClicked(id: String, holder: StoryListViewHolder)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}