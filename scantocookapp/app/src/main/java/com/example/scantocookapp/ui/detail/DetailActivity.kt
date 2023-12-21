package com.example.scantocookapp.ui.detail

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.scantocookapp.MainActivity
import com.example.scantocookapp.databinding.ActivityDetailBinding
import com.example.scantocookapp.model.DetailMealViewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailMealViewModel by viewModels<DetailMealViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val idmeal = intent.getStringExtra("MEAL_ID")


        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        if (idmeal != null) {
            detailMealViewModel.getDetailMeal(idmeal)
        }

        detailMealViewModel.itemDetail.observe(this) {
            val meal = it[0]

            binding.tvNameRecipe.text = meal.strMeal
            Glide.with(binding.root).load(meal.strMealThumb).into(binding.ivDetailRecipe)
            binding.tvCategoryRecipe.text = meal.strCategory
            binding.tvInstRecipe.text = meal.strInstructions
            binding.ivBack.setOnClickListener {
                val detailMeal = Intent(binding.ivBack.context, MainActivity::class.java)
                binding.root.context.startActivity(detailMeal)
            }
        }
    }
}