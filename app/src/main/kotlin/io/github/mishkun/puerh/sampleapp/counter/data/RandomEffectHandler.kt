package io.github.mishkun.puerh.sampleapp.counter.data

import io.github.mishkun.puerh.sampleapp.counter.logic.CounterFeature
import java.util.concurrent.ExecutorService
import kotlin.random.Random

fun ExecutorService.randomEffectInterpreter(
    eff: CounterFeature.Eff,
    listener: (CounterFeature.Msg) -> Unit
) = when (eff) {
    is CounterFeature.Eff.GenerateRandomCounterChange -> submit {
        for (i in 1..100) {
            Thread.sleep(10)
            listener(CounterFeature.Msg.OnProgressPublish(i))
        }
        val value = Random.nextInt(100) - 50
        listener(CounterFeature.Msg.OnCounterChange(value))
    }
}
