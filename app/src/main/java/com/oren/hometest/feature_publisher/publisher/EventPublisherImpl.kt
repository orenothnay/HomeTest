package com.oren.hometest.feature_publisher.publisher

import android.util.Log
import com.oren.hometest.feature_publisher.local_data.TimestampedEvent
import com.oren.hometest.feature_publisher.local_data.TimestampedEventDao
import com.oren.hometest.feature_publisher.remote.RemoteAPI
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
Event publisher implementation, uses a database backing as a task queue.
this approach allows us to persist events in case of system failure.
it also means our implementation is thread safe (at some cost to performance).
message order is maintained using timestamps.
NOTE: database exceptions are of course possible, handling them seemed out of scope for this assignment so I omitted the verbose try-catch blocks.
 */
class EventPublisherImpl(private val localEventDao : TimestampedEventDao, private val remote: RemoteAPI) : EventPublisher {

    private var sendMessagesJob : Job

    init {
        // in case we already have messages in our queue (e.g. crash recovery) start sending them immediately
        sendMessagesJob = MainScope().launch { sendMessagesSequentially() }
    }

    override fun publish(event: Event)
    {
        Log.i("EventPublisher","adding event to publish queue ${event.subject} ${event.payload}")
        MainScope().launch {
            // add the latest event to our queue, adding a timestamp.
            localEventDao.insertAll(listOf(TimestampedEvent(timestamp = System.currentTimeMillis(), subject = event.subject, payload = event.payload)))

            // if we're not already sending out messages, start doing so
            if(!sendMessagesJob.isActive)
            {
                sendMessagesJob = MainScope().launch { sendMessagesSequentially() }
            }
        }
    }

    // while there are events in our queue, take the earliest and send it.
    private suspend fun sendMessagesSequentially()
    {
        var timestampedEvent = localEventDao.getEventWithLowestTimestamp()
        while(timestampedEvent != null)
        {
            try {
                val httpResultCode = remote.send(Event(timestampedEvent.subject, timestampedEvent.payload))
                if(httpResultCode == 200)
                {
                    // log a successful send (in real project possibly avoid logging sensitive information here)
                    Log.i("EventPublisher","send was successful for ${timestampedEvent.subject} ${timestampedEvent.payload}")
                    // we have send confirmation, so we can safely remove this event from our queue.
                    localEventDao.delete(timestampedEvent)
                    // now get the next event and start over
                    timestampedEvent = localEventDao.getEventWithLowestTimestamp()
                }
                else
                {
                    Log.e("EventPublisher", "Server did not return 200 for event ${timestampedEvent.subject} ${timestampedEvent.payload}")
                    delay(1000)
                    // with the current implementation, we will naively wait a second and retry this request forever.
                    // more complex handling strategies (like exponential back off) can be used in a real project.
                }
            }
            catch (e: Exception) // in a real project we will have more granular exception handling
            {
                Log.e("EventPublisher", "Exception while sending event ${e.message}")
                delay(1000) // same naive strategy
            }
        }
    }
}