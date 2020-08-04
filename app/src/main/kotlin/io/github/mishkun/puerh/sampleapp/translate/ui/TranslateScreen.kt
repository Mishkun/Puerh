package io.github.mishkun.puerh.sampleapp.translate.ui

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.VERTICAL
import android.widget.Spinner
import dev.inkremental.dsl.android.init
import dev.inkremental.dsl.android.onTextChanged
import dev.inkremental.dsl.android.text
import dev.inkremental.dsl.android.widget.editText
import dev.inkremental.dsl.android.widget.linearLayout
import dev.inkremental.dsl.android.widget.textView
import dev.inkremental.dsl.androidx.appcompat.widget.appCompatSpinner
import io.github.mishkun.puerh.sampleapp.translate.logic.TranslateFeature

fun TranslateScreen(
    translateState: TranslateFeature.State,
    listener: (TranslateFeature.Msg) -> Unit
) = linearLayout {
    orientation(VERTICAL)
    textView {
        text(translateState.translatedText)
    }
    editText {
        text(translateState.inputText)
        onTextChanged { listener(TranslateFeature.Msg.OnTextInput(it.toString())) }
    }
    linearLayout {
        orientation(HORIZONTAL)

        val languages = translateState.languagesState.availableLanguages.map { it.displayName }
        languagesSpinner(listOf("Auto") + languages) { item ->
            val lang = translateState.languagesState.availableLanguages.find {
                it.displayName == item
            }
            listener(TranslateFeature.Msg.OnLanguageFromChange(lang))
        }
        languagesSpinner(languages) { item ->
            val lang = translateState.languagesState.availableLanguages.find {
                it.displayName == item
            }
            lang?.let { listener(TranslateFeature.Msg.OnLanguageToChange(it)) }
        }
    }
}

private fun languagesSpinner(
    languages: List<String>,
    listener: (String) -> Unit
) {
    appCompatSpinner {
        init { v ->
            v as Spinner
            v.adapter = ArrayAdapter(
                v.context,
                android.R.layout.simple_spinner_item,
                languages
            )
            v.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View?,
                    i: Int,
                    l: Long
                ) {
                    val item = adapterView.getItemAtPosition(i).toString()
                    listener(item)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }
}

