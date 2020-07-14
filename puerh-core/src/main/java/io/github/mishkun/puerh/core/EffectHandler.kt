package io.github.mishkun.puerh.core

interface EffectHandler<Eff : Any, Msg : Any> : Cancelable {
    fun setListener(listener: (Msg) -> Unit)
    fun handleEffect(eff: Eff)
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
