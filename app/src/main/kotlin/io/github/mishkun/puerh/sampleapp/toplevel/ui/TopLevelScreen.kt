package io.github.mishkun.puerh.sampleapp.toplevel.ui

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import com.google.android.material.tabs.TabLayout
import dev.inkremental.dsl.android.*
import dev.inkremental.dsl.android.widget.button
import dev.inkremental.dsl.android.widget.frameLayout
import dev.inkremental.dsl.android.widget.linearLayout
import dev.inkremental.dsl.android.widget.textView
import dev.inkremental.dsl.google.android.material.tabs.tabItem
import dev.inkremental.dsl.google.android.material.tabs.tabLayout
import io.github.mishkun.puerh.sampleapp.backstack.ui.BackstackScreen
import io.github.mishkun.puerh.sampleapp.counter.ui.CounterScreen
import io.github.mishkun.puerh.sampleapp.toplevel.logic.TopLevelFeature
import io.github.mishkun.puerh.sampleapp.toplevel.logic.TopLevelFeature.State.ScreenState

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
        }
    }

    linearLayout {
        gravity(BOTTOM)
        button {
            weight(1f)
            text("Counter")
            onClick { listener(TopLevelFeature.Msg.OnCounterScreenSwitch) }
        }
        button {
            weight(1f)
            text("Backstack")
            onClick { listener(TopLevelFeature.Msg.OnBackstackScreenSwitch) }
        }
    }
}
