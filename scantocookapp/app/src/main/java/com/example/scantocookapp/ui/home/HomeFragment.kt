package com.example.scantocookapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scantocookapp.adapter.CatAdapter
import com.example.scantocookapp.adapter.HomeAdapter
import com.example.scantocookapp.databinding.FragmentHomeBinding
import com.example.scantocookapp.model.CatViewModel
import com.example.scantocookapp.model.RecipeViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val recipeViewModel by viewModels<RecipeViewModel>()
    private val catViewModel by viewModels<CatViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeViewModel.getMeals("b")
        catViewModel.getCat()


//        binding.rvRecommend.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        val layoutManager = GridLayoutManager(requireActivity(), 2, LinearLayoutManager.VERTICAL, false)
        binding.rvRecommend.layoutManager = layoutManager
        binding.rvCategory.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        recipeViewModel.item.observe(viewLifecycleOwner) {
            Log.e("COB", "Observer triggered with items: $it")
            val adapter = HomeAdapter()
            binding.rvRecommend.adapter = adapter
            adapter.submitList(it)

        }

        catViewModel.itemCat.observe(viewLifecycleOwner) {
            Log.e("COB", "Observer triggered with items: $it")
            val adapter = CatAdapter()
            binding.rvCategory.adapter = adapter
            adapter.submitList(it)
        }

        recipeViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}