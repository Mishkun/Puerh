package io.github.mishkun.puerh.core

interface Feature<Msg : Any, Model : Any, Eff : Any> {
    val currentState: Model
    fun accept(msg: Msg)
    fun listenState(listener: (model: Model) -> Unit): Cancelable
    fun listenEffect(listener: (eff: Eff) -> Unit): Cancelable
}
