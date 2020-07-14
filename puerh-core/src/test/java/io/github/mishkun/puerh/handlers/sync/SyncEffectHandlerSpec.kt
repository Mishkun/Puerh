package io.github.mishkun.puerh.handlers.sync

import io.github.mishkun.puerh.core.SyncFeature
import io.github.mishkun.puerh.core.wrapWithEffectHandler
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class SyncEffectHandlerSpec : FreeSpec({
    "describe effect handler api" - {
        "should execute effect even if no listeners" {
            var executed = false
            val interpreter: ((Msg) -> Unit).(Eff) -> Unit = { executed = true }

            SyncEffectHandler(interpreter).handleEffect(Eff)

            executed shouldBe true
        }
        "should execute effect and notify listener with the resulting message" {
            var notified: Msg? = null
            val interpreter: ((Msg) -> Unit).(Eff) -> Unit = { invoke(Msg) }

            SyncEffectHandler(interpreter).apply {
                setListener { notified = it }
                handleEffect(Eff)
            }

            notified shouldBe Msg
        }
        "should not notify listeners if canceled" {
            var notified: Msg? = null
            val interpreter: ((Msg) -> Unit).(Eff) -> Unit = { invoke(Msg) }

            SyncEffectHandler(interpreter).apply {
                setListener { notified = it }
                cancel()
                handleEffect(Eff)
            }

            notified shouldBe null
        }
    }
    "describe integration with Feature" - {
        "should execute initial effects" {
            val initialEffects = setOf(Eff)
            val interpreter: ((Msg) -> Unit).(Eff) -> Unit = { invoke(Msg) }

            val feature = SyncFeature(State(0), ::justIncrementReducer)
                .wrapWithEffectHandler(SyncEffectHandler(interpreter), initialEffects)

            feature.currentState.counter shouldBe 1
        }
        "should get effects from feature after initial effects fired" {
            val initialEffects = setOf(Eff)
            val interpreter: ((Msg) -> Unit).(Eff) -> Unit = { invoke(Msg) }

            val feature = SyncFeature(State(0), ::incrementUntil5Reducer)
                .wrapWithEffectHandler(SyncEffectHandler(interpreter), initialEffects)

            feature.currentState.counter shouldBe 5
        }
        "should get effects from feature after external message acceptance" {
            val initialEffects = emptySet<Eff>()
            val interpreter: ((Msg) -> Unit).(Eff) -> Unit = { invoke(Msg) }

            val feature = SyncFeature(State(0), ::incrementUntil5Reducer)
                .wrapWithEffectHandler(SyncEffectHandler(interpreter), initialEffects)
            feature.accept(Msg)

            feature.currentState.counter shouldBe 5
        }
        "should not notify feature when canceled" {
            val initialEffects = emptySet<Eff>()
            val interpreter: ((Msg) -> Unit).(Eff) -> Unit = { invoke(Msg) }

            val feature = SyncFeature(State(0), ::incrementUntil5Reducer)
                .wrapWithEffectHandler(SyncEffectHandler(interpreter), initialEffects)
            feature.cancel()
            feature.accept(Msg)

            feature.currentState.counter shouldBe 0
        }
    }
}) {
    object Msg
    object Eff
    data class State(val counter: Int)
}

private fun justIncrementReducer(
    msg: SyncEffectHandlerSpec.Msg,
    state: SyncEffectHandlerSpec.State
) = state.copy(counter = state.counter + 1) to emptySet<SyncEffectHandlerSpec.Eff>()


private fun incrementUntil5Reducer(
    msg: SyncEffectHandlerSpec.Msg,
    state: SyncEffectHandlerSpec.State
) = state.copy(counter = state.counter + 1) to
        if (state.counter < 4) setOf(SyncEffectHandlerSpec.Eff) else emptySet()

