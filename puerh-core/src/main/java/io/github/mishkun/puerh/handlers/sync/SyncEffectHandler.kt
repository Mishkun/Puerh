package io.github.mishkun.puerh.handlers.sync

import io.github.mishkun.puerh.core.EffectHandler

private typealias MsgListener<Msg> = (Msg) -> Unit
typealias SyncEffectInterpreter<Eff, Msg> = (MsgListener<Msg>).(Eff) -> Unit

class SyncEffectHandler<Eff : Any, Msg : Any>(
    private val effectInterpreter: SyncEffectInterpreter<Eff, Msg>
) : EffectHandler<Eff, Msg> {
    private var listener: MsgListener<Msg>? = null

    override fun setListener(listener: MsgListener<Msg>) {
        this.listener = listener
    }

    override fun handleEffect(eff: Eff) {
        val listener = listener ?: {}
        effectInterpreter.invoke(listener, eff)
    }

    override fun cancel() {
        listener = null
    }
}
