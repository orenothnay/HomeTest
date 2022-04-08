package com.oren.hometest.feature_publisher.local_data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "timestamped_events")
data class TimestampedEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val subject: String,
    val payload: String
)


