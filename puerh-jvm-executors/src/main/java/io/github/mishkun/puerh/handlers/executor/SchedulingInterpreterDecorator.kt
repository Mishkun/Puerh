package io.github.mishkun.puerh.handlers.executor

import android.os.Handler
import android.os.Looper
import io.github.mishkun.puerh.core.Feature

class SchedulingInterpreterDecorator<Msg : Any, Model : Any, Cmd : Any>(
    private val innerFeature: Feature<Msg, Model, Cmd>,
    private val interpreter: ((Msg) -> Unit).(eff: Cmd) -> Unit,
    initialCommand: Cmd,
    private val scheduler: Scheduler
) : Feature<Msg, Model, Cmd> by innerFeature {
    private val callerThreadHandler = Handler(Looper.myLooper())
    private val msgListener: (Msg) -> Unit = { msg: Msg ->
        callerThreadHandler.post {
            innerFeature.accept(msg)
        }
    }

    init {
        innerFeature.listenEffect { effect ->
            scheduler {
                interpreter.invoke(msgListener, effect)
            }
        }
        scheduler {
            interpreter.invoke(msgListener, initialCommand)
        }
    }
}

fun <Msg : Any, Model : Any, Cmd : Any> Feature<Msg, Model, Cmd>.wrapWithInterpreter(
    scheduler: Scheduler,
    initialCommand: Cmd,
    interpreter: ((Msg) -> Unit).(eff: Cmd) -> Unit
) = SchedulingInterpreterDecorator(
    innerFeature = this,
    interpreter = interpreter,
    initialCommand = initialCommand,
    scheduler = scheduler
)
