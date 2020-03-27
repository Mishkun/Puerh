package io.github.mishkun.puerh.core

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import io.github.mishkun.puerh.core.utils.notifyAll

interface Cancelable {
    fun cancel()
}

interface Feature<Msg : Any, Model : Any, Eff : Any> {
    val currentState: Model
    fun accept(msg: Msg)
    fun listenState(listener: (model: Model) -> Unit): Cancelable
    fun listenEffect(listener: (eff: Eff) -> Unit): Cancelable
}

class SyncFeature<Msg : Any, Model : Any, Eff : Any>(
    initialState: Model,
    private val reducer: (Msg, Model) -> Pair<Model, Set<Eff>>
) : Feature<Msg, Model, Eff> {
    override var currentState: Model = initialState
        private set
    private val stateListeners = mutableListOf<(state: Model) -> Unit>()
    private val effListeners = mutableListOf<(eff: Eff) -> Unit>()

    override fun accept(msg: Msg) {
        val (newState, commands) = reducer(msg, currentState)
        currentState = newState
        stateListeners.notifyAll(newState)
        commands.forEach { command ->
            effListeners.notifyAll(command)
        }
    }

    override fun listenState(listener: (state: Model) -> Unit): Cancelable =
        stateListeners.addListenerAndMakeCancelable(listener)

    override fun listenEffect(listener: (eff: Eff) -> Unit): Cancelable =
        effListeners.addListenerAndMakeCancelable(listener)
}

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

    override fun listenState(listener: (state: Model) -> Unit): Cancelable =
        stateListeners.addListenerAndMakeCancelable(listener)

    override fun listenEffect(listener: (eff: Eff) -> Unit): Cancelable =
        effListeners.addListenerAndMakeCancelable(listener)
}

private fun <T> MutableList<(T) -> Unit>.addListenerAndMakeCancelable(listener: (T) -> Unit): Cancelable {
    add(listener)
    return object : Cancelable {
        override fun cancel() {
            remove(listener)
        }
    }
}
