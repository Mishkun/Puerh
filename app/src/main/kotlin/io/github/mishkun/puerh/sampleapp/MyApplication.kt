package io.github.mishkun.puerh.sampleapp

import android.app.Application
import io.github.mishkun.puerh.sampleapp.di.provideFeature

class MyApplication : Application() {
    val feature by lazy { provideFeature(applicationContext) }
}
