package com.sunykarasuno.models

sealed class Status {
    object Online : Status()
    object Idle : Status()
    object DoNotDisturb : Status()
    object Offline : Status()
    object Invisible : Status()
    object Unknown : Status()
}
