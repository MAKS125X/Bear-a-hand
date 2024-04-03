package com.example.simbirsoftmobile.presentation.screens.help

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simbirsoftmobile.databinding.ItemCategoryBinding
import com.example.simbirsoftmobile.presentation.models.category.Category

class CategoryAdapter(
    val context: Context,
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private var categoryArray: List<Category> = listOf()

    fun submitList(list: List<Category>) {
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
            context,
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
        val context: Context,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: Category) {
            with(binding) {
                setupUI(model, context)
            }
        }

        private fun ItemCategoryBinding.setupUI(
            model: Category,
            context: Context,
        ) {
            categoryNameIV.setImageResource(model.imageResId)
            categoryNameTV.text = context.resources.getString(model.stringResId)
        }
    }
}
