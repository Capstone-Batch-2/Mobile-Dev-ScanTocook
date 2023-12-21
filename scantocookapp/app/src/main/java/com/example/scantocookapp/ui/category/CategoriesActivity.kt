package com.example.scantocookapp.ui.category

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.scantocookapp.MainActivity
import com.example.scantocookapp.adapter.HomeAdapter
import com.example.scantocookapp.data.response.MealsItem
import com.example.scantocookapp.databinding.ActivityCategoriesBinding
import com.example.scantocookapp.model.RecipeViewModel

class CategoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoriesBinding
    private val recipeViewModel by viewModels<RecipeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val category = intent.getStringExtra("CATEGORY")


        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        binding.rvListCategory.layoutManager = layoutManager

        supportActionBar?.hide()

        if (category != null) {
            recipeViewModel.getListCat(category)
        }

        recipeViewModel.listCat.observe(this) {
            binding.tvDetailPage.text = "Category : $category"
            binding.ivBack.setOnClickListener {
                val detailMeal = Intent(binding.ivBack.context, MainActivity::class.java)
                binding.root.context.startActivity(detailMeal)
            }
            if (it  != null) {
                setListCatData(it)
            }
        }
    }

    private fun setListCatData(dataitem: List<MealsItem>) {
        val adapter = HomeAdapter()
        binding.rvListCategory.adapter = adapter
        adapter.submitList(dataitem)
    }
}