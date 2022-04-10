package com.oren.hometest.feature_publisher.publisher

interface EventPublisher {
    fun publish(event: Event)
}