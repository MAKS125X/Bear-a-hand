package com.example.simbirsoftmobile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.auth.screen.AuthFragment
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragmentContainerView,
                AuthFragment.newInstance(),
                AuthFragment.TAG,
            ).commit()
        }
    }
}
