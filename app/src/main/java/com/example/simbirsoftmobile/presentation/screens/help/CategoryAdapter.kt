package com.example.simbirsoftmobile.presentation.screens.help

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.ItemCategoryBinding
import com.example.simbirsoftmobile.presentation.models.category.CategoryLongUi

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
            categoryNameIV.setImageResource(R.drawable.category_unknown)
            categoryNameIV.load(model.imageUrl) {
                placeholder(R.drawable.loading_animation)
                this.crossfade(100)
                error(R.drawable.category_unknown)
            }
            categoryNameTV.text = model.name
        }
    }
}
