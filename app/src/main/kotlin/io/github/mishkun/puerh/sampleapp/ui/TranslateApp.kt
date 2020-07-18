package io.github.mishkun.puerh.sampleapp.ui

import dev.inkremental.dsl.android.text
import dev.inkremental.dsl.android.widget.button
import dev.inkremental.dsl.android.widget.linearLayout
import dev.inkremental.dsl.android.widget.textView
import io.github.mishkun.puerh.sampleapp.logic.TopLevelFeature

fun TranslateApp(
    topState: TopLevelFeature.State,
    listener: (TopLevelFeature.Msg) -> Unit
) {
    linearLayout {
        button {
            text("Inkrease")
            onClick {
                listener(TopLevelFeature.Msg.IncreaseCounter(-1))
            }
        }
        textView { text(topState.counter.toString()) }
        button {
            text("Dekrease")
            onClick {
                listener(TopLevelFeature.Msg.IncreaseCounter(-1))
            }
        }
        button {
            text("Random")
            onClick {
                listener(TopLevelFeature.Msg.ClickRandom)
            }
        }
    }
}
