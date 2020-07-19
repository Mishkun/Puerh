package io.github.mishkun.puerh.sampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.inkremental.Inkremental
import dev.inkremental.renderableContentView
import io.github.mishkun.puerh.sampleapp.ui.TranslateApp

class MainActivity : AppCompatActivity() {
    private val feature
        get() = (application as TranslateApplication).feature

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        feature.listenState { state ->
            Inkremental.render()
        }
        renderableContentView {
            TranslateApp(feature.currentState, feature::accept)
        }
    }
}
