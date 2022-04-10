package com.oren.hometest.feature_publisher.local_data

import androidx.room.*

@Dao
interface TimestampedEventDao {

    @Query("SELECT * FROM timestamped_events ORDER BY timestamp ASC LIMIT 1")
    suspend fun getEventWithLowestTimestamp(): TimestampedEvent?

    @Insert
    suspend fun insertAll(events: List<TimestampedEvent>)

    @Delete
    suspend fun delete(event: TimestampedEvent)
}