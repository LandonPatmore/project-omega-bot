package com.sunykarasuno.networking.websockets

import io.reactivex.rxjava3.functions.Consumer

interface NetworkingController {
    val networkController: Consumer<Any>
}
