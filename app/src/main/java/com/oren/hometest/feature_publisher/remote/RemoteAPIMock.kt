package com.oren.hometest.feature_publisher.remote

import com.oren.hometest.feature_publisher.publisher.Event
import kotlinx.coroutines.delay

class RemoteAPIMock : RemoteAPI {

    // flags used to mock failure conditions
    var simulateServerError = false
    var simulateNetworkDown = false

    override suspend fun send(event: Event): Int {
        delay(1000) // simulate some network delay
        if (simulateNetworkDown) {
            throw Exception("the network is down!") // just for mock, a more distinct exception type should be used in real project.
        }
        if (simulateServerError) {
            return 503
        }
        return 200 // just a mock so please excuse the magic numbers here :)
    }


}