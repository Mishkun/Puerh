package io.github.mishkun.puerh.core

interface EffectHandler<Eff : Any, Msg : Any> : Cancelable {
    fun setListener(listener: (Msg) -> Unit)
    fun handleEffect(eff: Eff)
}

fun <Eff1 : Any, Msg1 : Any, Eff2 : Any, Msg2 : Any> EffectHandler<Eff1, Msg1>.adapt(
    effAdapter: (Eff2) -> Eff1?,
    msgAdapter: (Msg1) -> Msg2?
): EffectHandler<Eff2, Msg2> = object : EffectHandler<Eff2, Msg2> {
    override fun setListener(listener: (Msg2) -> Unit) =
        setListener { msg: Msg1 -> msgAdapter(msg)?.let { listener(it) } }
    override fun handleEffect(eff: Eff2) {
        effAdapter(eff)?.let { handleEffect(it) }
    }
    override fun cancel() = this@adapt.cancel()
}


fun <Msg : Any, State : Any, Eff : Any> Feature<Msg, State, Eff>.wrapWithEffectHandler(
    effectHandler: EffectHandler<Eff, Msg>,
    initialEffects: Set<Eff> = emptySet()
) = object : Feature<Msg, State, Eff> by this {
    override fun cancel() {
        effectHandler.cancel()
        this@wrapWithEffectHandler.cancel()
    }
}.apply {
    effectHandler.setListener { msg -> accept(msg) }
    listenEffect { eff ->
        effectHandler.handleEffect(eff)
    }
    initialEffects.forEach(effectHandler::handleEffect)
}
