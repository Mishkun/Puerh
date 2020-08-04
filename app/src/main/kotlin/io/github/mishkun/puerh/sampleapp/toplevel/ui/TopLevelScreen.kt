package io.github.mishkun.puerh.sampleapp.toplevel.ui

import android.graphics.Color
import android.widget.LinearLayout.VERTICAL
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.inkremental.dsl.android.BOTTOM
import dev.inkremental.dsl.android.init
import dev.inkremental.dsl.android.weight
import dev.inkremental.dsl.android.widget.frameLayout
import dev.inkremental.dsl.android.widget.linearLayout
import dev.inkremental.dsl.google.android.material.bottomnavigation.bottomNavigationView
import dev.inkremental.dsl.google.android.material.inflateMenu
import io.github.mishkun.puerh.R
import io.github.mishkun.puerh.sampleapp.backstack.ui.BackstackScreen
import io.github.mishkun.puerh.sampleapp.counter.ui.CounterScreen
import io.github.mishkun.puerh.sampleapp.toplevel.logic.TopLevelFeature
import io.github.mishkun.puerh.sampleapp.toplevel.logic.TopLevelFeature.State.ScreenState
import io.github.mishkun.puerh.sampleapp.translate.ui.TranslateScreen

fun TopLevelScreen(
    state: TopLevelFeature.State,
    listener: (TopLevelFeature.Msg) -> Unit
) = linearLayout {
    orientation(VERTICAL)
    frameLayout {
        weight(1f)
        when (val screen = state.currentScreen) {
            is ScreenState.Counter -> CounterScreen(
                screen.state
            ) { listener(TopLevelFeature.Msg.CounterMsg(it)) }
            is ScreenState.Backstack -> BackstackScreen(
                screen.state
            ) { listener(TopLevelFeature.Msg.BackstackMsg(it)) }
            is ScreenState.Translate -> TranslateScreen(
                screen.state
            ) { listener(TopLevelFeature.Msg.TranslateMsg(it)) }
        }
    }

    bottomNavigationView {
        backgroundColor(Color.WHITE)
        inflateMenu(R.menu.bottom_navigation)
        init { view ->
            val nav = view as BottomNavigationView
            nav.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_counter -> listener(TopLevelFeature.Msg.OnCounterScreenSwitch)
                    R.id.navigation_backstack -> listener(TopLevelFeature.Msg.OnBackstackScreenSwitch)
                    R.id.navigation_translate -> listener(TopLevelFeature.Msg.OnTranslateScreenSwitch)
                }
                true
            }
        }
        gravity(BOTTOM)
    }
}
