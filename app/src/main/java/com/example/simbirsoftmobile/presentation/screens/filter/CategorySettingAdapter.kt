package com.example.simbirsoftmobile.presentation.screens.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simbirsoftmobile.databinding.ItemCategorySettingBinding
import com.example.simbirsoftmobile.presentation.models.category.CategoryLongUi
import com.example.simbirsoftmobile.presentation.screens.utils.TypedListener

class CategorySettingAdapter(private val onCheckBoxClick: TypedListener<String>) :
    ListAdapter<CategoryLongUi, CategorySettingAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder =
        ViewHolder(
            ItemCategorySettingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
            onCheckBoxClick,
        )

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    class ViewHolder(
        private val binding: ItemCategorySettingBinding,
        private val onCheckBoxClick: TypedListener<String>,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: CategoryLongUi) {
            with(binding) {
                setupUI(model)
            }
        }

        private fun ItemCategorySettingBinding.setupUI(model: CategoryLongUi) {
            nameTV.text = model.name
            switchView.isChecked = model.isSelected
            switchView.setOnCheckedChangeListener { _, _ ->
                onCheckBoxClick(model.id)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CategoryLongUi>() {
            override fun areItemsTheSame(
                oldItem: CategoryLongUi,
                newItem: CategoryLongUi,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CategoryLongUi,
                newItem: CategoryLongUi,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
