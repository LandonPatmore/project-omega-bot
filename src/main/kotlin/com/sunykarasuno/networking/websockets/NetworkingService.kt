package com.sunykarasuno.networking.websockets

import com.jakewharton.rxrelay3.PublishRelay

interface NetworkingService {
    val networkingService: PublishRelay<Any>
}
