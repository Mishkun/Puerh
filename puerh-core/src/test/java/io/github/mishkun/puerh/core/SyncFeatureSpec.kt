package io.github.mishkun.puerh.core

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class SyncFeatureSpec : FreeSpec({
    "describe current state" - {
        "it should be initialized with state"  {
            val testFeature = createTestFeature()
            testFeature.currentState shouldBe SyncTestFeature.State(0)
        }
        "it should update state on message" {
            val testFeature = createTestFeature()
            testFeature.accept(SyncTestFeature.Msg)
            testFeature.currentState shouldBe SyncTestFeature.State(1)
        }
    }
    "describe state subscription" - {
        "it should get first state on subscription" {
            val testFeature = createTestFeature()
            var gotState: SyncTestFeature.State? = null
            val subscriber: (SyncTestFeature.State) -> Unit = { gotState = it }

            testFeature.listenState(subscriber)

            gotState shouldBe SyncTestFeature.State(0)
        }
        "it should always get exactly one latest state on subscription" {
            val testFeature = createTestFeature()
            val states = mutableListOf<SyncTestFeature.State>()
            val subscriber: (SyncTestFeature.State) -> Unit = { states.add(it) }

            testFeature.accept(SyncTestFeature.Msg)
            testFeature.listenState(subscriber)

            states should containExactly(SyncTestFeature.State(1))
        }
        "it should subscribe to state updates" {
            val testFeature = createTestFeature()
            val states = mutableListOf<SyncTestFeature.State>()
            val subscriber: (SyncTestFeature.State) -> Unit = { states.add(it) }
            testFeature.listenState(subscriber)

            testFeature.accept(SyncTestFeature.Msg)

            states should containExactly(SyncTestFeature.State(0), SyncTestFeature.State(1))
        }
    }
    "describe effects subscription" - {
        "it should subscribe to effects" {
            val testFeature = createTestFeature()
            val effects = mutableListOf<SyncTestFeature.Eff>()
            val subscriber: (SyncTestFeature.Eff) -> Unit = { effects.add(it) }
            testFeature.listenEffect(subscriber)

            testFeature.accept(SyncTestFeature.Msg)

            effects should containExactly(SyncTestFeature.Eff)
        }
        "it should not get any effects on subscription" {
            val testFeature = createTestFeature()
            val effects = mutableListOf<SyncTestFeature.Eff>()
            val subscriber: (SyncTestFeature.Eff) -> Unit = { effects.add(it) }

            testFeature.accept(SyncTestFeature.Msg)
            testFeature.listenEffect(subscriber)

            effects should beEmpty()
        }
    }
    "describe feature disposal" - {
        "it should not get any effects after calling cancel method" {
            val testFeature = createTestFeature()
            val effects = mutableListOf<SyncTestFeature.Eff>()
            val subscriber: (SyncTestFeature.Eff) -> Unit = { effects.add(it) }

            testFeature.listenEffect(subscriber)
            testFeature.cancel()
            testFeature.accept(SyncTestFeature.Msg)

            effects should beEmpty()
        }
        "it should not get any new states after calling cancel method" {
            val testFeature = createTestFeature()
            val states = mutableListOf<SyncTestFeature.State>()
            val subscriber: (SyncTestFeature.State) -> Unit = { states.add(it) }

            testFeature.listenState(subscriber)
            testFeature.cancel()
            testFeature.accept(SyncTestFeature.Msg)

            states should containExactly(SyncTestFeature.State(0))
        }
    }
})

private fun createTestFeature() = SyncFeature(SyncTestFeature.State(0), SyncTestFeature::reducer)

private object SyncTestFeature {
    fun reducer(msg: Msg, state: State): Pair<State, Set<Eff>> =
        state.copy(counter = state.counter + 1) to setOf(Eff)

    object Msg
    object Eff
    data class State(val counter: Int)
}
