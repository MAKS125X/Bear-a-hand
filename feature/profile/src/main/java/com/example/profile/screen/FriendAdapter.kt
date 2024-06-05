package com.example.profile.screen

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.common.R
import com.example.profile.databinding.ItemFriendBinding
import com.example.profile.models.FriendUI

class FriendAdapter : ListAdapter<FriendUI, FriendAdapter.ViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder =
        ViewHolder(
            ItemFriendBinding.inflate(
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

    class ViewHolder(private val binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: FriendUI) {
            with(binding) {
                setupUI(model)
            }
        }

        private fun ItemFriendBinding.setupUI(model: FriendUI) {
            nameTextView.text = model.name
            profileIV.load(model.imageUrl) {
                placeholder(R.drawable.loading_animation)
                this.error(R.drawable.friend_error_square)
                transformations(CircleCropTransformation())
            }
        }
    }

    class CustomItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val position = parent.getChildAdapterPosition(view)
            val itemCount = parent.adapter?.itemCount ?: 0

            if (position == itemCount - 1) {
                outRect.bottom = 0
            }
        }
    }

    companion object {
        private val DiffCallback =
            object : DiffUtil.ItemCallback<FriendUI>() {
                override fun areItemsTheSame(
                    oldItem: FriendUI,
                    newItem: FriendUI,
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: FriendUI,
                    newItem: FriendUI,
                ): Boolean {
                    return oldItem.imageUrl == newItem.imageUrl
                }
            }
    }
}
