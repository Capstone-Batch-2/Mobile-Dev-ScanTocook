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

class DetailMealViewModel : ViewModel() {

    private val _itemDetail = MutableLiveData<List<MealsItem>>()
    val itemDetail: LiveData<List<MealsItem>> = _itemDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object{
        internal const val TAG = "DetailMealViewModel"
    }

    fun getDetailMeal(i: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailMeal(i)
        client.enqueue(object : Callback<MealsResponse> {
            override fun onResponse(
                call: Call<MealsResponse>,
                response: Response<MealsResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _itemDetail.value = response.body()?.meals
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

    }