package com.example.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.search.databinding.ItemSearchResultBinding
import com.example.search.models.SearchResultUI

class SearchResultAdapter :
    ListAdapter<SearchResultUI, SearchResultAdapter.ViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder =
        ViewHolder(
            ItemSearchResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    class ViewHolder(private val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: SearchResultUI) {
            with(binding) {
                setupUI(model)
            }
        }

        private fun ItemSearchResultBinding.setupUI(model: SearchResultUI) {
            nameTextView.text = model.name
        }
    }

    companion object {
        private val DiffCallback =
            object : DiffUtil.ItemCallback<SearchResultUI>() {
                override fun areItemsTheSame(
                    oldItem: SearchResultUI,
                    newItem: SearchResultUI,
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: SearchResultUI,
                    newItem: SearchResultUI,
                ): Boolean {
                    return oldItem.name == newItem.name
                }
            }
    }
}
