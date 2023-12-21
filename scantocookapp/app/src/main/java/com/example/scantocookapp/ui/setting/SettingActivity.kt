package com.example.scantocookapp.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.scantocookapp.MainActivity
import com.example.scantocookapp.R
import com.example.scantocookapp.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        // Your setting activity initialization code here
        supportActionBar?.hide()

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            val detailSet = Intent(binding.ivBack.context, MainActivity::class.java)
            binding.root.context.startActivity(detailSet)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }
}
