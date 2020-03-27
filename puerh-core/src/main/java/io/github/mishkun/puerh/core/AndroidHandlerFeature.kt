package io.github.mishkun.puerh.core

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import io.github.mishkun.puerh.core.utils.addListenerAndMakeCancelable
import io.github.mishkun.puerh.core.utils.notifyAll

class AndroidHandlerFeature<Msg : Any, Model : Any, Eff : Any>(
    initialState: Model,
    private val reducer: (Msg, Model) -> Pair<Model, Set<Eff>>
) : Feature<Msg, Model, Eff> {
    private val reduceThread = HandlerThread("reducer thread").apply {
        start()
    }
    private val reduceHandler = Handler(reduceThread.looper)
    private val callerThreadHandler = Handler(Looper.myLooper())
    private val stateListeners = mutableListOf<(state: Model) -> Unit>()
    private val effListeners = mutableListOf<(eff: Eff) -> Unit>()

    override var currentState: Model = initialState
        private set

    override fun accept(msg: Msg) {
        val state = currentState
        reduceHandler.post {
            val (newState, commands) = reducer(msg, state)
            callerThreadHandler.post {
                currentState = newState
                stateListeners.notifyAll(newState)
                commands.forEach { eff ->
                    effListeners.notifyAll(eff)
                }
            }
        }
    }

    override fun listenState(listener: (state: Model) -> Unit): Cancelable {
        val cancelable = stateListeners.addListenerAndMakeCancelable(listener)
        listener(currentState)
        return cancelable
    }

    override fun listenEffect(listener: (eff: Eff) -> Unit): Cancelable =
        effListeners.addListenerAndMakeCancelable(listener)
}
