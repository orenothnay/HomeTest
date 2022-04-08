package com.oren.hometest.feature_publisher.publisher

import com.oren.hometest.feature_publisher.publisher.Event

interface EventPublisher {
    fun publish(event: Event)
}