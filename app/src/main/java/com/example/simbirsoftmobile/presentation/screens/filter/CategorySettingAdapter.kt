package com.example.simbirsoftmobile.presentation.screens.filter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simbirsoftmobile.databinding.ItemCategorySettingBinding
import com.example.simbirsoftmobile.presentation.models.category.CategoryLongUi

class CategorySettingAdapter(
    private var settings: List<CategoryLongUi> = listOf(),
    val context: Context,
) : RecyclerView.Adapter<CategorySettingAdapter.ViewHolder>() {
    fun submitList(list: List<CategoryLongUi>) {
        settings = list
    }

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
            context,
        )

    override fun getItemCount(): Int = settings.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(settings[position])
    }

    class ViewHolder(private val binding: ItemCategorySettingBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: CategoryLongUi) {
            with(binding) {
                setupUI(model)
            }
        }

        private fun ItemCategorySettingBinding.setupUI(model: CategoryLongUi) {
            nameTV.text = model.name
            switchView.isChecked = model.isSelected
            switchView.setOnCheckedChangeListener { _, isChecked ->
                model.isSelected = isChecked
            }
        }
    }
}
