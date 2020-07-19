package io.github.mishkun.puerh.sampleapp.counter.logic

typealias ReducerResult = Pair<CounterFeature.State, Set<CounterFeature.Eff>>

object CounterFeature {
    fun initialState(): State = State(1, null)

    fun initialEffects(): Set<Eff> = setOf(Eff.GenerateRandomCounterChange)

    data class State(val counter: Int, val progress: Int?)

    sealed class Msg {
        data class OnCounterChange(val value: Int) : Msg()
        data class OnProgressPublish(val progress: Int) : Msg()
        object OnRandomClick : Msg()
        object OnIncreaseClick : Msg()
        object OnDecreaseClick : Msg()
    }

    sealed class Eff {
        object GenerateRandomCounterChange : Eff()
    }

    fun reducer(msg: Msg, state: State): ReducerResult = when (msg) {
        is Msg.OnCounterChange -> state.addToCounter(msg.value) to emptySet()
        is Msg.OnRandomClick -> state to setOf(Eff.GenerateRandomCounterChange)
        is Msg.OnIncreaseClick -> state.addToCounter(1) to emptySet()
        is Msg.OnDecreaseClick -> state.addToCounter(-1) to emptySet()
        is Msg.OnProgressPublish -> when (msg.progress) {
            in 1..99 -> state.copy(progress = msg.progress) to emptySet()
            else -> state.copy(progress = null) to emptySet()
        }
    }

    private fun State.addToCounter(newValue: Int) = copy(counter = counter + newValue)
}
