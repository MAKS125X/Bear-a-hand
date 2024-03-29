package com.example.simbirsoftmobile.presentation.screens

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.ActivityMainBinding
import com.example.simbirsoftmobile.presentation.screens.auth.AuthFragment
import com.example.simbirsoftmobile.presentation.screens.eventDetails.EventDetailsFragment
import com.example.simbirsoftmobile.presentation.screens.help.HelpFragment
import com.example.simbirsoftmobile.presentation.screens.news.NewsFragment
import com.example.simbirsoftmobile.presentation.screens.profile.ProfileFragment
import com.example.simbirsoftmobile.presentation.screens.search.SearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigation()

        if (savedInstanceState == null) {
//            binding.bottomNavigationView.selectedItemId = R.id.help

            supportFragmentManager.beginTransaction().replace(
                binding.fragmentHolder.id,
                AuthFragment.newInstance(),
                AuthFragment.TAG,
            ).commit()
        }

    }

    private fun initBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.profile -> {
                    supportFragmentManager.beginTransaction().replace(
                        binding.fragmentHolder.id,
                        ProfileFragment.newInstance(),
                        ProfileFragment.TAG,
                    ).commit()
                }

                R.id.help -> {
                    supportFragmentManager.beginTransaction().replace(
                        binding.fragmentHolder.id,
                        HelpFragment.newInstance(),
                        HelpFragment.TAG,
                    ).commit()
                }

                R.id.search -> {
                    supportFragmentManager.beginTransaction().replace(
                        binding.fragmentHolder.id,
                        SearchFragment.newInstance(),
                        SearchFragment.TAG,
                    ).commit()
                }

                R.id.news -> {
                    val currentFragment =
                        supportFragmentManager.findFragmentById(R.id.fragmentHolder)
                    if (currentFragment !is EventDetailsFragment) {
                        supportFragmentManager.beginTransaction().replace(
                            binding.fragmentHolder.id,
                            NewsFragment.newInstance(),
                            NewsFragment.TAG,
                        ).commit()
                    }
                }

                else -> {
                    val toastMassage = it.title
                    Toast.makeText(this, toastMassage, Toast.LENGTH_SHORT).show()
                }
            }

            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_toolbar_menu, menu)
        return true
    }
}
