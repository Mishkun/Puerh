package io.github.mishkun.puerh.core

interface EffectHandler<Eff : Any, Msg : Any> : Cancelable {
    fun addListener(listener: (Msg) -> Unit)
    fun handleEffect(eff: Eff)
}
