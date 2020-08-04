package io.github.mishkun.puerh.sampleapp.toplevel.logic

import io.github.mishkun.puerh.sampleapp.backstack.logic.BackstackFeature
import io.github.mishkun.puerh.sampleapp.counter.logic.CounterFeature
import io.github.mishkun.puerh.sampleapp.toplevel.logic.TopLevelFeature.State.ScreenState.*
import io.github.mishkun.puerh.sampleapp.translate.logic.TranslateFeature

private typealias ReducerResult = Pair<TopLevelFeature.State, Set<TopLevelFeature.Eff>>

object TopLevelFeature {
    fun initialState(
        backstackFeatureState: BackstackFeature.State
    ): State = State(
        screens = listOf(
            Counter(CounterFeature.initialState()),
            Backstack(backstackFeatureState),
            Translate(TranslateFeature.initialState())
        ),
        currentScreenPos = 0
    )

    fun initialEffects(): Set<Eff> =
        CounterFeature.initialEffects().mapTo(HashSet(), Eff::CounterEff)

    data class State(
        val screens: List<ScreenState>,
        val currentScreenPos: Int
    ) {
        val currentScreen = screens[currentScreenPos]
        fun <T : ScreenState> changeCurrentScreen(block: T.() -> T): State {
            @Suppress("UNCHECKED_CAST") val newScreen = (currentScreen as? T)?.block()
            val newList = if (newScreen != null)
                screens.toMutableList().also { mutableScreens ->
                    mutableScreens[currentScreenPos] = newScreen
                } else screens
            return copy(screens = newList)
        }

        sealed class ScreenState {
            data class Counter(
                val state: CounterFeature.State
            ) : ScreenState()

            data class Backstack(
                val state: BackstackFeature.State
            ) : ScreenState()

            data class Translate(
                val state: TranslateFeature.State
            ) : ScreenState()
        }
    }

    sealed class Msg {
        data class CounterMsg(val msg: CounterFeature.Msg) : Msg()
        data class BackstackMsg(val msg: BackstackFeature.Msg) : Msg()
        data class TranslateMsg(val msg: TranslateFeature.Msg) : Msg()
        object OnBackstackScreenSwitch : Msg()
        object OnTranslateScreenSwitch : Msg()
        object OnCounterScreenSwitch : Msg()
        object OnBack : Msg()
    }

    sealed class Eff {
        object Finish : Eff()
        data class CounterEff(val eff: CounterFeature.Eff) : Eff()
        data class BackstackEff(val eff: BackstackFeature.Eff) : Eff()
        data class TranslateEff(val eff: TranslateFeature.Eff) : Eff()
    }

    fun reducer(msg: Msg, state: State): ReducerResult = when (state.currentScreen) {
        is Counter -> when (msg) {
            is Msg.CounterMsg -> {
                reduceCounter(state.currentScreen, msg.msg, state)
            }
            is Msg.OnBack -> {
                state to setOf(Eff.Finish)
            }
            is Msg.OnBackstackScreenSwitch -> state.copy(currentScreenPos = 1) to emptySet()
            is Msg.OnTranslateScreenSwitch -> state.copy(currentScreenPos = 2) to emptySet()
            else -> state to emptySet()
        }
        is Backstack -> when (msg) {
            is Msg.BackstackMsg -> {
                reduceBackstack(state.currentScreen, msg.msg, state)
            }
            is Msg.OnBack -> {
                reduceBackstack(state.currentScreen, BackstackFeature.Msg.OnBack, state)
            }
            is Msg.OnCounterScreenSwitch -> state.copy(currentScreenPos = 0) to emptySet()
            is Msg.OnTranslateScreenSwitch -> state.copy(currentScreenPos = 2) to emptySet()
            else -> state to emptySet()
        }
        is Translate -> when (msg) {
            is Msg.TranslateMsg -> reduceTranslate(state.currentScreen, msg.msg, state)
            is Msg.OnBackstackScreenSwitch -> state.copy(currentScreenPos = 1) to emptySet()
            is Msg.OnCounterScreenSwitch -> state.copy(currentScreenPos = 0) to emptySet()
            else -> state to emptySet()
        }
    }

    private fun reduceBackstack(
        currentScreen: Backstack,
        msg: BackstackFeature.Msg,
        state: State
    ): ReducerResult {
        val (newScreenState, effs) = BackstackFeature.reducer(msg, currentScreen.state)
        val newEffs = effs.mapTo(HashSet(), ::toTopLevel)
        return state.changeCurrentScreen<Backstack> { copy(state = newScreenState) } to newEffs
    }

    private fun toTopLevel(eff: BackstackFeature.Eff): Eff = when (eff) {
        is BackstackFeature.Eff.Finish -> Eff.Finish
    }

    private fun reduceCounter(
        currentScreen: Counter,
        msg: CounterFeature.Msg,
        state: State
    ): ReducerResult {
        val (newScreenState, effs) = CounterFeature.reducer(msg, currentScreen.state)
        val newEffs = effs.mapTo(HashSet(), Eff::CounterEff)
        return state.changeCurrentScreen<Counter> { copy(state = newScreenState) } to newEffs
    }

    private fun reduceTranslate(
        currentScreen: Translate,
        msg: TranslateFeature.Msg,
        state: State
    ): ReducerResult {
        val (newScreenState, effs) = TranslateFeature.reducer(msg, currentScreen.state)
        val newEffs = effs.mapTo(HashSet(), Eff::TranslateEff)
        return state.changeCurrentScreen<Translate> { copy(state = newScreenState) } to newEffs
    }
}
