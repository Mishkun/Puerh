package io.github.mishkun.puerh.sampleapp.di

import android.content.Context
import android.os.Handler
import android.os.Looper
import io.github.mishkun.puerh.core.Feature
import io.github.mishkun.puerh.core.SyncFeature
import io.github.mishkun.puerh.core.wrapWithEffectHandler
import io.github.mishkun.puerh.handlers.executor.ExecutorEffectHandler
import io.github.mishkun.puerh.sampleapp.counter.data.randomEffectInterpreter
import io.github.mishkun.puerh.sampleapp.counter.logic.CounterFeature
import java.util.concurrent.Executor
import java.util.concurrent.Executors

fun provideFeature(applicationContext: Context): Feature<CounterFeature.Msg, CounterFeature.State, CounterFeature.Eff> {
    val androidMainThreadExecutor = object : Executor {
        private val handler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            handler.post(command)
        }
    }
    val ioPool = Executors.newCachedThreadPool()

    return SyncFeature(CounterFeature.initialState(), CounterFeature::reducer)
        .wrapWithEffectHandler(
            ExecutorEffectHandler(
                { eff, listener -> randomEffectInterpreter(eff, listener) },
                androidMainThreadExecutor,
                ioPool
            ),
            CounterFeature.initialEffects()
        )
}
