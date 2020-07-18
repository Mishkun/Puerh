package io.github.mishkun.puerh.sampleapp.logic

typealias ReducerResult = Pair<TopLevelFeature.State, Set<TopLevelFeature.Eff>>

object TopLevelFeature {
    fun initialState(): State = State(1)

    fun initialEffects(): Set<Eff> = setOf(Eff.Load)

    data class State(val counter: Int) {

    }

    sealed class Msg {
        data class IncreaseCounter(val value: Int) : Msg()
        object ClickRandom: Msg()
    }

    sealed class Eff {
        object Load : Eff()
    }

    fun reducer(msg: Msg, state: State): ReducerResult = when(msg) {
        is Msg.IncreaseCounter -> state.copy(counter = state.counter + msg.value) to emptySet()
        is Msg.ClickRandom -> state to setOf(Eff.Load)
    }
}
