package com.example.news.screen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.date.getRemainingDateInfo
import com.example.news.databinding.ItemNewBinding
import com.example.news.models.EventShortUi
import com.example.ui.TypedListener
import com.example.common.R as commonR
import com.example.news.R as newsR

class NewsAdapter(
    private val onItemClick: TypedListener<String>,
    private val context: Context,
) : ListAdapter<EventShortUi, NewsAdapter.ViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder =
        ViewHolder(
            ItemNewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
            onItemClick,
            context,
        )

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    class ViewHolder(
        private val binding: ItemNewBinding,
        private val onItemClick: TypedListener<String>,
        private val context: Context,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: EventShortUi) {
            with(binding) {
                setupUI(model, onItemClick)
            }
        }

        private fun ItemNewBinding.setupUI(
            model: EventShortUi,
            onItemClick: TypedListener<String>,
        ) {
            newLayout.setOnClickListener {
                onItemClick(model.id)
            }
            titleTV.text = model.name
            descriptionTV.text = model.description
            remainDateTV.text = getRemainingDateInfo(model.startDate, model.endDate, context)

            previewIV.load(model.photo) {
                placeholder(commonR.drawable.loading_animation)
                error(newsR.drawable.news_preview_not_found)
            }
        }
    }

    companion object {
        private val DiffCallback =
            object : DiffUtil.ItemCallback<EventShortUi>() {
                override fun areItemsTheSame(
                    oldItem: EventShortUi,
                    newItem: EventShortUi,
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: EventShortUi,
                    newItem: EventShortUi,
                ): Boolean {
                    return oldItem.id == newItem.id
                }
            }
    }
}
