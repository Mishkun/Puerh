package io.github.mishkun.puerh.handlers.executor

import io.github.mishkun.puerh.core.Cancelable
import java.util.concurrent.Executors
import java.util.concurrent.Future

typealias Scheduler = (() -> Unit) -> Cancelable
internal typealias Task = () -> Unit

object ComputationScheduler : Scheduler {
    private val executor =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    override fun invoke(task: Task): Cancelable {
        return executor.submit(task).toCancelable()
    }
}

object IoScheduler : Scheduler {
    private val executor =
        Executors.newCachedThreadPool()

    override fun invoke(task: Task): Cancelable {
        return executor.submit(task).toCancelable()
    }
}

private fun <T> Future<T>.toCancelable() = object : Cancelable {
    override fun cancel() {
        this@toCancelable.cancel(true)
    }
}
