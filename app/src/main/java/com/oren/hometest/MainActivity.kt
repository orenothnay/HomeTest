package com.oren.hometest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.oren.hometest.databinding.ActivityMainBinding
import com.oren.hometest.feature_publisher.publisher.Event
import com.oren.hometest.feature_publisher.publisher.EventPublisherImpl
import com.oren.hometest.feature_publisher.remote.RemoteAPIMock
import com.oren.hometest.feature_publisher.local_data.LocalEventDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Room.databaseBuilder(
            applicationContext,
            LocalEventDatabase::class.java, "local-event-database"
        ).build()
        val remoteAPIImpl = RemoteAPIMock()
        val eventPublisher = EventPublisherImpl(
            database.timestampedEventDao(),
            remoteAPIImpl
        ) // manual injection to keep things simple, would use DI framework in real project.
        // also, in a real project this would go in a sticky service. Or at least a view model. omitted for simplicity.

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.send.setOnClickListener {
            eventPublisher.publish(
                Event(
                    binding.subject.text.toString(),
                    binding.payload.text.toString()
                )
            )
        }
        var counter = 0
        binding.generateEventsButton.setOnClickListener {
            for (i in 0..9) {
                counter++
                eventPublisher.publish(Event("subject${counter}", "payload${counter}"))
            }
        }
        binding.simulateNetworkErrorButton.setOnClickListener {
            remoteAPIImpl.simulateNetworkDown = !remoteAPIImpl.simulateNetworkDown
        }

        binding.simulateServerErrorButton.setOnClickListener {
            remoteAPIImpl.simulateServerError = !remoteAPIImpl.simulateServerError
        }
    }
}