package com.example.scantocookapp.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.scantocookapp.data.response.MealsItem
import com.example.scantocookapp.databinding.ItemListRecipeBinding
import com.example.scantocookapp.ui.detail.DetailActivity

class ListCatAdapter: ListAdapter<MealsItem, ListCatAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private var listItem:List<MealsItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    class MyViewHolder(private val binding: ItemListRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MealsItem){
            Log.i("YTC","$item")
            binding.tvItemName .text = item.strMeal
            Glide.with(binding.root.context).load(item.strMealThumb).into(binding.ivItemRecipe)
            binding.btnItemRecipe.setOnClickListener {
                val detailMeal = Intent(binding.btnItemRecipe.context, DetailActivity::class.java)
                detailMeal.putExtra("MEAL_ID", item.idMeal)
                binding.root.context.startActivity(detailMeal)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MealsItem>() {
            override fun areItemsTheSame(oldItem: MealsItem, newItem: MealsItem): Boolean {
                return oldItem.idMeal == newItem.idMeal
            }

            override fun areContentsTheSame(oldItem: MealsItem, newItem: MealsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}