package io.github.mishkun.puerh.core

import io.github.mishkun.puerh.core.utils.addListenerAndMakeCancelable
import io.github.mishkun.puerh.core.utils.notifyAll

class SyncFeature<Msg : Any, Model : Any, Eff : Any>(
    initialState: Model,
    private val reducer: (Msg, Model) -> Pair<Model, Set<Eff>>
) : Feature<Msg, Model, Eff> {
    override var currentState: Model = initialState
        private set

    private var isCanceled = false
    private val stateListeners = mutableListOf<(state: Model) -> Unit>()
    private val effListeners = mutableListOf<(eff: Eff) -> Unit>()

    override fun accept(msg: Msg) {
        if (isCanceled) return
        val (newState, commands) = reducer(msg, currentState)
        currentState = newState
        stateListeners.notifyAll(newState)
        commands.forEach { command ->
            effListeners.notifyAll(command)
        }
    }

    override fun listenState(listener: (state: Model) -> Unit): Cancelable {
        val cancelable = stateListeners.addListenerAndMakeCancelable(listener)
        listener(currentState)
        return cancelable
    }

    override fun listenEffect(listener: (eff: Eff) -> Unit): Cancelable =
        effListeners.addListenerAndMakeCancelable(listener)

    override fun cancel() {
        isCanceled = true
    }
}
