package io.github.mishkun.puerh.sampleapp.counter.ui

import android.widget.LinearLayout.VERTICAL
import dev.inkremental.dsl.android.Dip
import dev.inkremental.dsl.android.margin
import dev.inkremental.dsl.android.text
import dev.inkremental.dsl.android.widget.button
import dev.inkremental.dsl.android.widget.linearLayout
import dev.inkremental.dsl.android.widget.textView
import io.github.mishkun.puerh.sampleapp.counter.logic.CounterFeature

fun CounterScreen(
    counterState: CounterFeature.State,
    listener: (CounterFeature.Msg) -> Unit
) {
    linearLayout {
        orientation(VERTICAL)
        linearLayout {
            margin(Dip(16))
            button {
                text("Inkrease")
                onClick {
                    listener(CounterFeature.Msg.OnCounterChange(-1))
                }
            }
            textView { text(counterState.counter.toString()) }
            button {
                text("Dekrease")
                onClick {
                    listener(CounterFeature.Msg.OnCounterChange(-1))
                }
            }
            button {
                text("Random")
                onClick {
                    listener(CounterFeature.Msg.OnRandomClick)
                }
            }
        }
        if (counterState.progress != null) {
            textView {
                margin(Dip(16))
                text("progress ${counterState.progress}")
            }
        }
    }
}
