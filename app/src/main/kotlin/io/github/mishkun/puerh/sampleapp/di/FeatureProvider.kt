package io.github.mishkun.puerh.sampleapp.di

import android.content.Context
import android.os.Handler
import android.os.Looper
import io.github.mishkun.puerh.core.Feature
import io.github.mishkun.puerh.core.SyncFeature
import io.github.mishkun.puerh.sampleapp.backstack.logic.BackstackFeature
import io.github.mishkun.puerh.sampleapp.backstack.logic.NavGraph
import io.github.mishkun.puerh.sampleapp.backstack.logic.SCREEN_NAMES
import java.util.concurrent.Executor
import java.util.concurrent.Executors

fun provideFeature(applicationContext: Context): Feature<BackstackFeature.Msg, BackstackFeature.State, BackstackFeature.Eff> {
    val androidMainThreadExecutor = object : Executor {
        private val handler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            handler.post(command)
        }
    }
    val ioPool = Executors.newCachedThreadPool()

    return SyncFeature(
        BackstackFeature.initialState(SCREEN_NAMES.first(), generateScreenGraph()),
        BackstackFeature::reducer
    )
}

private fun generateScreenGraph(): NavGraph {
    val nameTriples = SCREEN_NAMES.shuffled().zipWithNext().zip(SCREEN_NAMES)
    return nameTriples.associate { (shuffled, name3) ->
        val (name1, name2) = shuffled
        name1 to (name2 to name3)
    }
}
