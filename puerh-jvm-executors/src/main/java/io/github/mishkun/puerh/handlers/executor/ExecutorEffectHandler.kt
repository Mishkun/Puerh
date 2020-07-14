package io.github.mishkun.puerh.handlers.executor

import io.github.mishkun.puerh.core.EffectHandler
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

class ExecutorEffectHandler<Msg : Any, Eff : Any>(
    private val effectsInterpreter: ExecutorService.(eff: Eff, listener: (Msg) -> Unit) -> Unit,
    private val callerThreadExecutor: Executor,
    private val effectsExecutorService: ExecutorService
) : EffectHandler<Eff, Msg> {
    private var listener: ((Msg) -> Unit)? = null

    override fun setListener(listener: (Msg) -> Unit) {
        this.listener = { msg -> callerThreadExecutor.execute { listener(msg) } }
    }

    override fun handleEffect(eff: Eff) {
        effectsExecutorService.run {
            effectsInterpreter(eff, listener ?: {})
        }
    }

    override fun cancel() {
        effectsExecutorService.shutdownNow()
    }
}
