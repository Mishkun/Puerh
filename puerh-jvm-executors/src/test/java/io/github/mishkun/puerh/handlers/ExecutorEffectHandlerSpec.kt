package io.github.mishkun.puerh.handlers

import io.github.mishkun.puerh.core.SyncFeature
import io.github.mishkun.puerh.core.wrapWithEffectHandler
import io.github.mishkun.puerh.handlers.executor.ExecutorEffectHandler
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ExecutorEffectHandlerSpec : FreeSpec({
    "describe executor handler api" - {
        "should execute effects off the caller thread" {
            val latch = CountDownLatch(1)
            var executingThreadId: Long? = null
            val interpreter: ExecutorService.(eff: Eff, listener: (Msg) -> Unit) -> Unit =
                { eff, listener ->
                    submit {
                        executingThreadId = Thread.currentThread().id
                        listener.invoke(Msg.Msg2)
                        latch.countDown()
                    }
                }
            val handler = ExecutorEffectHandler(
                effectsInterpreter = interpreter,
                callerThreadExecutor = Executors.newSingleThreadExecutor(),
                effectsExecutorService = Executors.newSingleThreadExecutor()
            )

            handler.handleEffect(Eff.Eff1)
            latch.await()

            executingThreadId shouldNotBe null
            executingThreadId shouldNotBe Thread.currentThread().id
        }
        "should call listener on callerThreadExecutor" {
            val latch = CountDownLatch(2)
            var executorThreadId: Long? = null
            var listenerThreadId: Long? = null
            val interpreter: ExecutorService.(eff: Eff, listener: (Msg) -> Unit) -> Unit =
                { eff, listener ->
                    submit {
                        listener.invoke(Msg.Msg2)
                        latch.countDown()
                    }
                }
            val callerThreadExecutor = Executors.newSingleThreadExecutor()
            callerThreadExecutor.execute {
                executorThreadId = Thread.currentThread().id
            }
            val handler = ExecutorEffectHandler(
                effectsInterpreter = interpreter,
                callerThreadExecutor = callerThreadExecutor,
                effectsExecutorService = Executors.newSingleThreadExecutor()
            )
            handler.setListener {
                listenerThreadId = Thread.currentThread().id
                latch.countDown()
            }

            handler.handleEffect(Eff.Eff1)
            latch.await()

            listenerThreadId shouldNotBe null
            listenerThreadId shouldBe executorThreadId
        }
    }
    "describe executor handler feature integration" - {
        "should execute effects off thread and return messages to the feature" {
            val latch = CountDownLatch(3)
            val interpreter: ExecutorService.(eff: Eff, listener: (Msg) -> Unit) -> Unit =
                { eff, listener ->
                    if (eff is Eff.Eff1) {
                        submit {
                            Thread.sleep(100)
                            listener.invoke(Msg.Msg2)
                        }
                    } else {
                        submit {
                            Thread.sleep(100)
                            listener.invoke(Msg.Msg1)
                        }
                    }
                }
            val feature = SyncFeature(initialState(), ::reduce).wrapWithEffectHandler(
                ExecutorEffectHandler(
                    effectsInterpreter = interpreter,
                    // this would be the MainLooper Handler in case of Android dev
                    callerThreadExecutor = Executors.newSingleThreadExecutor(),
                    effectsExecutorService = Executors.newCachedThreadPool()
                )
            )
            feature.listenState { latch.countDown() }

            feature.accept(Msg.Msg1)
            latch.await()

            feature.currentState.counter shouldBe 2
            feature.currentState.msg1Counter shouldBe 1
            feature.currentState.msg2Counter shouldBe 1
        }
    }
}) {
    sealed class Eff {
        object Eff1 : Eff()
        object Eff2 : Eff()
    }

    sealed class Msg {
        object Msg1 : Msg()
        object Msg2 : Msg()
    }

    data class State(val msg1Counter: Int, val msg2Counter: Int, val counter: Int)
    companion object {
        fun initialState() = State(0, 0, 0)
        fun reduce(msg: Msg, state: State): Pair<State, Set<Eff>> = when (msg) {
            is Msg.Msg1 -> state.copy(
                msg1Counter = state.msg1Counter + 1,
                counter = state.counter + 1
            ) to setOf(Eff.Eff1)
            is Msg.Msg2 -> state.copy(
                msg2Counter = state.msg2Counter + 1,
                counter = state.counter + 1
            ) to emptySet()
        }
    }
}
