package io.github.mishkun.puerh.sampleapp.backstack.logic

import io.github.mishkun.puerh.sampleapp.backstack.logic.BackstackFeature.State.ScreenState

private typealias ReducerResult = Pair<BackstackFeature.State, Set<BackstackFeature.Eff>>

typealias NavGraph = Map<String, Pair<String, String>>

object BackstackFeature {
    fun initialState(
        startScreenName: String,
        graph: NavGraph
    ): State = State(
        backStack = emptyList(),
        graph = graph,
        screen = ScreenState(name = startScreenName, counter = 0)
    )

    fun initialEffects(): Set<Eff> = emptySet()

    data class State(
        val backStack: List<ScreenState>,
        val graph: NavGraph,
        val screen: ScreenState
    ) {
        val previousScreen: ScreenState? = backStack.lastOrNull()
        val canGoTo: Pair<String, String> get() = graph[screen.name] ?: graph.entries.first().value
        fun beenIn(screenName: String): Boolean = backStack.any { it.name == screenName }
        data class ScreenState(val name: String, val counter: Int)
    }

    sealed class Msg {
        data class OnGoToClicked(val screenName: String) : Msg()
        object OnIncreasePreviousClicked : Msg()
        object OnDecreasePreviousClicked : Msg()
        object OnIncreaseClicked : Msg()
        object OnDecreaseClicked : Msg()
        object OnBack : Msg()
    }

    sealed class Eff {
        object Finish : Eff()
    }

    fun reducer(msg: Msg, state: State): ReducerResult = when (msg) {
        is Msg.OnGoToClicked -> goToReducer(msg.screenName, state)
        Msg.OnIncreaseClicked -> state.changeCurrentScreen { changeCounterBy(1) } to emptySet()
        Msg.OnDecreaseClicked -> state.changeCurrentScreen { changeCounterBy(-1) } to emptySet()
        Msg.OnIncreasePreviousClicked -> state.changePreviousScreen { changeCounterBy(1) } to emptySet()
        Msg.OnDecreasePreviousClicked -> state.changePreviousScreen { changeCounterBy(-1) } to emptySet()
        Msg.OnBack -> goBackReducer(state)
    }

    private fun goBackReducer(state: State): ReducerResult = when (state.backStack.size) {
        0 -> state to setOf(Eff.Finish)
        else -> {
            val newBackStack = state.backStack.dropLast(1)
            state.copy(backStack = newBackStack, screen = state.backStack.last()) to emptySet()
        }
    }

    private fun goToReducer(
        screenName: String,
        state: State
    ): ReducerResult = when (val backScreen = state.backStack.find { it.name == screenName }) {
        null -> {
            val newScreen = ScreenState(name = screenName, counter = 0)
            val newBackStack = state.backStack + state.screen
            state.copy(backStack = newBackStack, screen = newScreen) to emptySet()
        }
        else -> {
            val newBackStack = state.backStack.takeWhile { it.name != screenName }
            state.copy(backStack = newBackStack, screen = backScreen) to emptySet()
        }
    }

    private fun State.changePreviousScreen(block: ScreenState.() -> ScreenState): State {
        return if (backStack.isNotEmpty()) {
            val newBackStack = backStack.dropLast(1) + backStack.last().block()
            copy(backStack = newBackStack)
        } else {
            this
        }
    }

    private fun State.changeCurrentScreen(block: ScreenState.() -> ScreenState): State =
        copy(screen = screen.block())

    private fun ScreenState.changeCounterBy(byValue: Int): ScreenState =
        copy(counter = counter + byValue)
}
