package com.example.help.screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.help.databinding.ItemCategoryBinding
import com.example.help.model.CategoryLongUi
import com.example.common.R as commonR
import com.example.help.R as helpR

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private var categoryArray: List<CategoryLongUi> = listOf()

    fun submitList(list: List<CategoryLongUi>) {
        categoryArray = list
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder =
        ViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )

    override fun getItemCount(): Int = categoryArray.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(categoryArray[position])
    }

    class ViewHolder(
        private val binding: ItemCategoryBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: CategoryLongUi) {
            with(binding) {
                setupUI(model)
            }
        }

        private fun ItemCategoryBinding.setupUI(model: CategoryLongUi) {
            categoryNameIV.load(model.imageUrl) {
                placeholder(commonR.drawable.loading_animation)
                this.crossfade(100)
                error(helpR.drawable.category_unknown)
            }
            categoryNameTV.text = model.name
        }
    }
}
