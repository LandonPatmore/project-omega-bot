package com.sunykarasuno.utils.state

class StateArbiter {
    private var state = State()

    fun modifyState(state: State) {
        // TODO: Think on this a bit more
        this.state = state
    }

    fun getState() : State {
        return state
    }
}
