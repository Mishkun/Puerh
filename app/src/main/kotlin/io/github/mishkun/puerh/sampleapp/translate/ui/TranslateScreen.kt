package io.github.mishkun.puerh.sampleapp.translate.ui

import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.VERTICAL
import android.widget.Spinner
import dev.inkremental.dsl.android.*
import dev.inkremental.dsl.android.widget.editText
import dev.inkremental.dsl.android.widget.linearLayout
import dev.inkremental.dsl.android.widget.textView
import dev.inkremental.dsl.androidx.appcompat.widget.appCompatImageButton
import dev.inkremental.dsl.androidx.appcompat.widget.appCompatSpinner
import io.github.mishkun.puerh.R
import io.github.mishkun.puerh.sampleapp.translate.logic.TranslateFeature
import io.github.mishkun.puerh.sampleapp.translate.logic.TranslateFeature.State.SelectedLanguage

fun TranslateScreen(
    translateState: TranslateFeature.State,
    listener: (TranslateFeature.Msg) -> Unit
) = linearLayout {
    orientation(VERTICAL)
    gravity(BOTTOM)
    if (translateState.errorMessage == null) {
        textView {
            margin(16.dp)
            text(translateState.translatedText)
            padding(8.dp)
            backgroundColor(Color.parseColor("#BBFFDD"))
            gravity(BOTTOM)
            minHeight(400)
        }
    } else {
        textView {
            margin(16.dp)
            text(translateState.errorMessage)
            padding(8.dp)
            textColor(Color.WHITE)
            backgroundColor(Color.parseColor("#DD8888"))
            gravity(BOTTOM)
            minHeight(400)
        }
    }
    editText {
        margin(16.dp)
        text(translateState.inputText)
        gravity(BOTTOM)
        padding(8.dp)
        backgroundColor(Color.parseColor("#DDDDDD"))
        minHeight(400)
        onTextChanged { listener(TranslateFeature.Msg.OnTextInput(it.toString())) }
    }
    linearLayout {
        orientation(HORIZONTAL)

        val languages = translateState.languagesState.availableLanguages.map { it.displayName }
        val selectedFrom = when (val lang = translateState.languagesState.selectedFrom) {
            is SelectedLanguage.Auto -> "Auto"
            is SelectedLanguage.Concrete -> lang.language.displayName
        }
        languagesSpinner(selectedFrom, listOf("Auto") + languages) { item ->
            val lang = translateState.languagesState.availableLanguages.find {
                it.displayName == item
            }
            listener(TranslateFeature.Msg.OnLanguageFromChange(lang))
        }
        appCompatImageButton {
            weight(1f)
            imageResource(R.drawable.ic_interchange_24)
            onClick { listener(TranslateFeature.Msg.OnLanguageSwapClick) }
        }
        val selectedTo = translateState.languagesState.selectedTo.displayName
        languagesSpinner(selectedTo, languages) { item ->
            val lang = translateState.languagesState.availableLanguages.find {
                it.displayName == item
            }
            lang?.let { listener(TranslateFeature.Msg.OnLanguageToChange(it)) }
        }
    }
}

private fun languagesSpinner(
    selected: String,
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
        selection(languages.indexOf(selected))
    }
}

