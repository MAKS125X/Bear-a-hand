package com.example.simbirsoftmobile.presentation.screens

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.example.auth.screen.AuthFragment
import com.example.content_holder.screen.ContentFragment
import com.example.event_details.screen.EventDetailsFragment
import com.example.help.screen.HelpFragment
import com.example.news.screen.NewsFragment
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.ActivityMainBinding
import kotlinx.parcelize.Parcelize

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
