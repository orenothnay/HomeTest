package com.oren.hometest.feature_publisher.local_data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TimestampedEvent::class], version = 1)
abstract class LocalEventDatabase : RoomDatabase() {
    abstract fun timestampedEventDao(): TimestampedEventDao
}