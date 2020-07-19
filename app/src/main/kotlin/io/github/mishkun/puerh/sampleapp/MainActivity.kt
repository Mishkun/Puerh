package io.github.mishkun.puerh.sampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.inkremental.Inkremental
import dev.inkremental.renderableContentView
import io.github.mishkun.puerh.sampleapp.counter.ui.CounterScreen

class MainActivity : AppCompatActivity() {
    private val feature
        get() = (application as MyApplication).feature

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        feature.listenState { state ->
            Inkremental.render()
        }
        renderableContentView {
            CounterScreen(feature.currentState, feature::accept)
        }
    }
}
