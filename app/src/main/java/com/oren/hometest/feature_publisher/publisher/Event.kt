package com.oren.hometest.feature_publisher.publisher

data class Event(
    val subject: String,
    val payload: String
)