package io.github.mishkun.puerh.sampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.inkremental.Inkremental
import dev.inkremental.renderableContentView
import io.github.mishkun.puerh.sampleapp.backstack.logic.BackstackFeature
import io.github.mishkun.puerh.sampleapp.backstack.ui.BackstackScreen

class MainActivity : AppCompatActivity() {
    private val feature
        get() = (application as MyApplication).feature

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        feature.listenState { _ ->
            Inkremental.render()
        }
        feature.listenEffect(this::handleEffect)
        renderableContentView {
            BackstackScreen(feature.currentState, feature::accept)
        }
    }

    private fun handleEffect(eff: BackstackFeature.Eff) = when (eff) {
        BackstackFeature.Eff.Finish -> onBackPressedDispatcher.onBackPressed()
    }

    override fun onBackPressed() {
        feature.accept(BackstackFeature.Msg.OnBack)
    }
}
