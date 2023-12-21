package com.example.scantocookapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.scantocookapp.data.response.CategoriesItem
import com.example.scantocookapp.databinding.ItemListBtnCatBinding
import com.example.scantocookapp.ui.category.CategoriesActivity

class CatAdapter: ListAdapter<CategoriesItem, CatAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private var listItem:List<CategoriesItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListBtnCatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }


    class MyViewHolder(private val binding: ItemListBtnCatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoriesItem){
            binding.btnCatItem .text = item.strCategory
            binding.btnCatItem.setOnClickListener {
                val detailMeal = Intent(binding.btnCatItem.context, CategoriesActivity::class.java)
                detailMeal.putExtra("CATEGORY", item.strCategory)
                binding.root.context.startActivity(detailMeal)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CategoriesItem>() {
            override fun areItemsTheSame(oldItem: CategoriesItem, newItem: CategoriesItem): Boolean {
                return oldItem.idCategory == newItem.idCategory

            }

            override fun areContentsTheSame(oldItem: CategoriesItem, newItem: CategoriesItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}