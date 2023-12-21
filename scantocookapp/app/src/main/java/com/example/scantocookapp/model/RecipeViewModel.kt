package com.example.scantocookapp.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scantocookapp.data.response.MealsItem
import com.example.scantocookapp.data.response.MealsResponse
import com.example.scantocookapp.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeViewModel : ViewModel() {

    private val _item = MutableLiveData<List<MealsItem>>()
    val item: LiveData<List<MealsItem>> = _item

    private val _listCat = MutableLiveData<List<MealsItem>>()
    val listCat: LiveData<List<MealsItem>> = _listCat

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object{
        private const val TAG = "RecipeViewModel"
    }

    fun getMeals(f: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getRandomMeals(f)
        client.enqueue(object : Callback<MealsResponse> {
            override fun onResponse(
                call: Call<MealsResponse>,
                response: Response<MealsResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _item.value = response.body()?.meals

                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MealsResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
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