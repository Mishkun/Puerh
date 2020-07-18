package io.github.mishkun.puerh.sampleapp.ui

import android.widget.LinearLayout.VERTICAL
import dev.inkremental.dsl.android.Dip
import dev.inkremental.dsl.android.margin
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
        orientation(VERTICAL)
        linearLayout {
            margin(Dip(16))
            button {
                text("Inkrease")
                onClick {
                    listener(TopLevelFeature.Msg.OnCounterChange(-1))
                }
            }
            textView { text(topState.counter.toString()) }
            button {
                text("Dekrease")
                onClick {
                    listener(TopLevelFeature.Msg.OnCounterChange(-1))
                }
            }
            button {
                text("Random")
                onClick {
                    listener(TopLevelFeature.Msg.OnRandomClick)
                }
            }
        }
        if (topState.progress != null) {
            textView {
                margin(Dip(16))
                text("progress ${topState.progress}")
            }
        }
    }
}
