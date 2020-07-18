package io.github.mishkun.puerh.sampleapp.data

import io.github.mishkun.puerh.sampleapp.logic.TopLevelFeature
import java.util.concurrent.ExecutorService
import kotlin.random.Random

fun ExecutorService.randomEffectInterpreter(
    eff: TopLevelFeature.Eff,
    listener: (TopLevelFeature.Msg) -> Unit
) = when (eff) {
    is TopLevelFeature.Eff -> submit {
        Thread.sleep(1000)
        val value = Random.nextInt(100) - 50
        listener(TopLevelFeature.Msg.IncreaseCounter(value))
    }
}
