package io.github.mishkun.puerh.interpreter.executor

import java.util.concurrent.Executors

internal typealias Scheduler = (() -> Unit) -> Unit
internal typealias Task = () -> Unit

class ComputationScheduler : Scheduler {
    private val executor =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    override fun invoke(task: Task) {
        executor.execute(task)
    }
}

class IoScheduler : Scheduler {
    private val executor =
        Executors.newCachedThreadPool()

    override fun invoke(task: Task) {
        executor.execute(task)
    }
}