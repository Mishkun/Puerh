package io.github.mishkun.puerh.sampleapp.di

import android.content.Context
import android.os.Handler
import android.os.Looper
import io.github.mishkun.puerh.core.Feature
import io.github.mishkun.puerh.core.SyncFeature
import io.github.mishkun.puerh.core.adapt
import io.github.mishkun.puerh.core.wrapWithEffectHandler
import io.github.mishkun.puerh.handlers.executor.ExecutorEffectHandler
import io.github.mishkun.puerh.handlers.executor.ExecutorEffectsInterpreter
import io.github.mishkun.puerh.sampleapp.backstack.logic.BackstackFeature
import io.github.mishkun.puerh.sampleapp.backstack.logic.NavGraph
import io.github.mishkun.puerh.sampleapp.backstack.logic.SCREEN_NAMES
import io.github.mishkun.puerh.sampleapp.counter.data.randomEffectInterpreter
import io.github.mishkun.puerh.sampleapp.toplevel.logic.TopLevelFeature
import io.github.mishkun.puerh.sampleapp.translate.data.TranslationApiEffectHandler
import kotlinx.serialization.json.Json
import java.util.concurrent.Executor
import java.util.concurrent.Executors

fun provideFeature(applicationContext: Context): Feature<TopLevelFeature.Msg, TopLevelFeature.State, TopLevelFeature.Eff> {
    val androidMainThreadExecutor = object : Executor {
        private val handler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            handler.post(command)
        }
    }
    val ioPool = Executors.newCachedThreadPool()

    return SyncFeature(
        TopLevelFeature.initialState(
            backstackFeatureState = BackstackFeature.initialState(
                SCREEN_NAMES.first(),
                generateScreenGraph()
            )
        ),
        TopLevelFeature::reducer
    ).wrapWithEffectHandler(
        ExecutorEffectHandler(
            adaptedRandomEffectInterpreter,
            androidMainThreadExecutor,
            ioPool
        )
    ).wrapWithEffectHandler<TopLevelFeature.Msg, TopLevelFeature.State, TopLevelFeature.Eff>(
        TranslationApiEffectHandler(
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = false
            },
            androidMainThreadExecutor,
            ioPool
        ).adapt(
            effAdapter = { (it as? TopLevelFeature.Eff.TranslateEff)?.eff },
            msgAdapter = { TopLevelFeature.Msg.TranslateMsg(it) }
        )
    )
}

private val adaptedRandomEffectInterpreter: ExecutorEffectsInterpreter<TopLevelFeature.Eff, TopLevelFeature.Msg> =
    { eff, listener ->
        if (eff is TopLevelFeature.Eff.CounterEff) randomEffectInterpreter(eff.eff) {
            listener(TopLevelFeature.Msg.CounterMsg(it))
        }
    }

private fun generateScreenGraph(): NavGraph {
    val nameTriples = SCREEN_NAMES.shuffled().zipWithNext().zip(SCREEN_NAMES)
    return nameTriples.associate { (shuffled, name3) ->
        val (name1, name2) = shuffled
        name1 to (name2 to name3)
    }
}
