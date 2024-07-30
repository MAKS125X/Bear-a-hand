package com.example.simbirsoftmobile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.auth.screen.AuthFragment
import com.example.event_details.screen.EventDetailsFragment
import com.example.event_details.screen.MoneyHelpNotificationIntentKey
import com.example.event_details.screen.MoneyHelpNotificationIntentValue
import com.example.event_details.screen.NotificationWorker.Companion.MONEY_HELP_ID_KEY
import com.example.simbirsoftmobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val notificationIntent = intent.getStringExtra(MoneyHelpNotificationIntentKey)

        when {
            notificationIntent == MoneyHelpNotificationIntentValue -> {
                val eventId = intent.getStringExtra(MONEY_HELP_ID_KEY)
                eventId?.let {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.fragmentContainerView,
                            EventDetailsFragment.newInstance(it),
                            EventDetailsFragment.TAG,
                        )
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
                    )
                    .commit()
            }
        }
    }
}
