package com.example.simbirsoftmobile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.auth.screen.AuthFragment
import com.example.event_details.screen.EventDetailsFragment
import com.example.event_details.screen.EventDetailsFragment.Companion.EVENT_ID_KEY
import com.example.event_details.screen.MoneyHelpNotificationIntentKey
import com.example.event_details.screen.MoneyHelpNotificationIntentValue
import com.example.simbirsoftmobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when {
            intent.getStringExtra(MoneyHelpNotificationIntentKey) == MoneyHelpNotificationIntentValue -> {
                val eventId = intent.getStringExtra(EVENT_ID_KEY)
                eventId?.let {
                    val fragment = EventDetailsFragment.newInstance(it)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, fragment)
                        .commit()
                }
            }

            savedInstanceState == null -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragmentContainerView,
                        AuthFragment.newInstance(),
                        AuthFragment.TAG,
                    ).commit()
            }
        }
    }
}
