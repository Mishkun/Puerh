package io.github.mishkun.puerh.sampleapp.backstack.ui

import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.VERTICAL
import dev.inkremental.dsl.android.Dip
import dev.inkremental.dsl.android.padding
import dev.inkremental.dsl.android.text
import dev.inkremental.dsl.android.widget.button
import dev.inkremental.dsl.android.widget.linearLayout
import dev.inkremental.dsl.android.widget.textView
import io.github.mishkun.puerh.sampleapp.backstack.logic.BackstackFeature

fun BackstackScreen(
    state: BackstackFeature.State,
    listener: (BackstackFeature.Msg) -> Unit
) = linearLayout {
    orientation(VERTICAL)
    padding(Dip(16))

    textView {
        text(state.renderPath())
    }
    BackstackCounter(
        "Current Screen",
        state.screen.counter,
        { listener(BackstackFeature.Msg.OnIncreaseClicked) },
        { listener(BackstackFeature.Msg.OnDecreaseClicked) }
    )
    state.previousScreen?.counter?.let {
        BackstackCounter(
            "Previous Screen",
            it,
            { listener(BackstackFeature.Msg.OnIncreasePreviousClicked) },
            { listener(BackstackFeature.Msg.OnDecreasePreviousClicked) }
        )
    }

    textView { text("Where to go next?") }
    val (next1, next2) = state.canGoTo
    button {
        text(state.goButtonText(next1))
        onClick { listener(BackstackFeature.Msg.OnGoToClicked(next1)) }
    }
    button {
        text(state.goButtonText(next2))
        onClick { listener(BackstackFeature.Msg.OnGoToClicked(next1)) }
    }
}

fun BackstackFeature.State.renderPath(): String {
    val path = (backStack + screen).joinToString(separator = "/") { it.name }
    return "You are here: $path"
}

private fun BackstackFeature.State.goButtonText(screenName: String) =
    if (beenIn(screenName)) "Back to $screenName" else "Go to $screenName"


private fun BackstackCounter(
    label: String,
    counter: Int,
    onIncClick: () -> Unit,
    onDecClick: () -> Unit
) = linearLayout {
    padding(vertical = Dip(8))
    orientation(VERTICAL)
    textView {
        text(label)
    }
    linearLayout {
        orientation(HORIZONTAL)
        button {
            text("Inc")
            onClick { onIncClick() }
        }
        textView { text(counter.toString()) }
        button {
            text("Dec")
            onClick { onDecClick() }
        }
    }
}
