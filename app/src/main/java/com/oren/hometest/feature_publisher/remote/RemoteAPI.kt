package com.oren.hometest.feature_publisher.remote

import com.oren.hometest.feature_publisher.publisher.Event

interface RemoteAPI {

    suspend fun send(event: Event) : Int // mock remote server call, returns the http response code.

}