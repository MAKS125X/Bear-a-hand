package com.example.simbirsoftmobile.presentation.screens.profile

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simbirsoftmobile.databinding.ItemFriendBinding
import com.example.simbirsoftmobile.presentation.models.FriendUI

class FriendAdapter(
    private val friendList: Array<FriendUI>,
) : RecyclerView.Adapter<FriendAdapter.ViewHolder>() {
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

    override fun getItemCount(): Int = friendList.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(friendList[position])
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
            profileIV.setImageResource(model.imageUrl)
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
}
