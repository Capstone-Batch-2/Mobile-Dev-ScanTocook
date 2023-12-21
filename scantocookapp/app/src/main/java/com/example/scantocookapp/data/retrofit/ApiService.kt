package com.example.scantocookapp.data.retrofit

import com.example.scantocookapp.data.response.CategoryResponse
import com.example.scantocookapp.data.response.MealsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search.php")
    fun getRandomMeals(@Query("f") f: String): Call<MealsResponse>

    @GET("categories.php")
    fun getCategory(): Call<CategoryResponse>

    @GET("lookup.php")
    fun getDetailMeal(@Query("i") i: String): Call<MealsResponse>

    @GET("filter.php")
    fun getRecipeByCat(@Query("c") c: String): Call<MealsResponse>
}