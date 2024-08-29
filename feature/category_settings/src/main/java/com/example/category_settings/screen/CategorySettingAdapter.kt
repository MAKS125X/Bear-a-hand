package com.example.category_settings.screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.category_settings.databinding.ItemCategorySettingBinding
import com.example.category_settings.model.SettingUi
import com.example.ui.TypedListener

class CategorySettingAdapter(private val onCheckBoxClick: TypedListener<String>) :
    ListAdapter<SettingUi, CategorySettingAdapter.ViewHolder>(DiffCallback) {

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
        fun bind(model: SettingUi) {
            with(binding) {
                setupUI(model)
            }
        }

        private fun ItemCategorySettingBinding.setupUI(model: SettingUi) {
            nameTV.text = model.name
            switchView.isChecked = model.isSelected
            switchView.setOnCheckedChangeListener { _, _ ->
                onCheckBoxClick(model.id)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<SettingUi>() {
            override fun areItemsTheSame(
                oldItem: SettingUi,
                newItem: SettingUi,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: SettingUi,
                newItem: SettingUi,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
