package com.example.scantocookapp.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scantocookapp.data.response.CategoriesItem
import com.example.scantocookapp.data.response.CategoryResponse
import com.example.scantocookapp.data.response.MealsItem
import com.example.scantocookapp.data.response.MealsResponse
import com.example.scantocookapp.data.retrofit.ApiConfig
import retrofit2.Call

class CatViewModel : ViewModel() {

    private val _itemCat = MutableLiveData<List<CategoriesItem>>()
    val itemCat: LiveData<List<CategoriesItem>> = _itemCat

    private val _listCat = MutableLiveData<List<MealsItem>>()
    val listCat: LiveData<List<MealsItem>> = _listCat

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object{
        private const val TAG = "CatViewModel"
    }

    fun getCat() {
        // In your activity or fragment
// Make a call to get random meals
        val call = ApiConfig.getApiService()
            .getCategory() // Change 5 to the desired number of random meals

        call.enqueue(object : retrofit2.Callback<CategoryResponse> {
            override fun onResponse(
                call: Call<CategoryResponse>,
                response: retrofit2.Response<CategoryResponse>
            ) {
                if (response.isSuccessful) {
                    val categories = response.body()?.categories
                    _itemCat.value = categories
                    // Process the list of random meals as needed
                    // Example: display the names of the meals
                    categories?.forEach { category ->
                        println(category.strCategory)

                    }
                    Log.e("COB", "Observer triggered with items: $categories")
                } else {
                    // Handle error
                    println("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                // Handle failure
                println("Failed to make API call: ${t.message}")
            }
        })

    }
    fun getListCat(c: String) {
        // In your activity or fragment
// Make a call to get random meals
        val call = ApiConfig.getApiService()
            .getRecipeByCat(c) // Change 5 to the desired number of random meals

        call.enqueue(object : retrofit2.Callback<MealsResponse> {
            override fun onResponse(
                call: Call<MealsResponse>,
                response: retrofit2.Response<MealsResponse>
            ) {
                if (response.isSuccessful) {
                    val meals = response.body()?.meals
                    _listCat.value = meals
                    // Process the list of random meals as needed
                    // Example: display the names of the meals
                    meals?.forEach { meal ->
                        println(meal.strMeal)

                    }
                    Log.e("COB", "Observer triggered with items: $meals")
                } else {
                    // Handle error
                    println("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MealsResponse>, t: Throwable) {
                // Handle failure
                println("Failed to make API call: ${t.message}")
            }
        })

    }
}