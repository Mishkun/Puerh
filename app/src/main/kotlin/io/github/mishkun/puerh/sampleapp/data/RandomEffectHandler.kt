package io.github.mishkun.puerh.sampleapp.data

import io.github.mishkun.puerh.sampleapp.logic.TopLevelFeature
import java.util.concurrent.ExecutorService
import kotlin.random.Random

fun ExecutorService.randomEffectInterpreter(
    eff: TopLevelFeature.Eff,
    listener: (TopLevelFeature.Msg) -> Unit
) = when (eff) {
    is TopLevelFeature.Eff.GenerateRandomCounterChange -> submit {
        for (i in 1..100) {
            Thread.sleep(10)
            listener(TopLevelFeature.Msg.OnProgressPublish(i))
        }
        val value = Random.nextInt(100) - 50
        listener(TopLevelFeature.Msg.OnCounterChange(value))
    }
}
