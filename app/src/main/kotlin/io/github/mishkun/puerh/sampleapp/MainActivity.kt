package io.github.mishkun.puerh.sampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.inkremental.Inkremental
import dev.inkremental.renderableContentView
import io.github.mishkun.puerh.sampleapp.toplevel.logic.TopLevelFeature
import io.github.mishkun.puerh.sampleapp.toplevel.ui.TopLevelScreen

class MainActivity : AppCompatActivity() {
    private val feature
        get() = (application as MyApplication).feature

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        feature.listenState {
            Inkremental.render()
        }
        feature.listenEffect(this::handleEffect)
        renderableContentView {
            TopLevelScreen(feature.currentState, feature::accept)
        }
    }

    private fun handleEffect(eff: TopLevelFeature.Eff) = when (eff) {
        TopLevelFeature.Eff.Finish -> onBackPressedDispatcher.onBackPressed()
        else -> Unit
    }

    override fun onBackPressed() {
        feature.accept(TopLevelFeature.Msg.OnBack)
    }
}
