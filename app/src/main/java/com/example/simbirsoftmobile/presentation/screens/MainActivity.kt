package com.example.simbirsoftmobile.presentation.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.simbirsoftmobile.databinding.ActivityMainBinding
import com.example.simbirsoftmobile.presentation.screens.auth.AuthFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                binding.fragmentHolder.id,
                AuthFragment.newInstance(),
                AuthFragment.TAG,
            ).commit()
        }
    }
}
