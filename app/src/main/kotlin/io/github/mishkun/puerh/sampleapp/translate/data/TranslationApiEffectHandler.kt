package io.github.mishkun.puerh.sampleapp.translate.data

import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.map
import io.github.mishkun.puerh.BuildConfig
import io.github.mishkun.puerh.core.EffectHandler
import io.github.mishkun.puerh.sampleapp.translate.logic.ApiError
import io.github.mishkun.puerh.sampleapp.translate.logic.Language
import io.github.mishkun.puerh.sampleapp.translate.logic.TranslateFeature
import io.github.mishkun.puerh.sampleapp.util.nullIfBlank
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

val BASE_URL =
    "https://api.eu-de.language-translator.watson.cloud.ibm.com/instances/5e44a6a3-37d2-4fc0-b71d-ac503e83608d/v3/translate?version=2018-05-01"

@Serializable
private data class TranslationApiRequest(
    val source: String? = null,
    val target: String?,
    val text: List<String>
)

@Serializable
private data class TranslationApiResult(
    val translations: List<TranslationResult>,
    val detectedLanguage: String? = null
) {
    @Serializable
    data class TranslationResult(val translation: String)
}

class TranslationApiEffectHandler(
    private val json: Json,
    private val callerThreadExecutor: Executor,
    private val effectsExecutorService: ExecutorService
) : EffectHandler<TranslateFeature.Eff, TranslateFeature.Msg> {
    private var listener: ((TranslateFeature.Msg) -> Unit)? = null
    private var translateApiFuture: Future<*>? = null

    override fun setListener(listener: (TranslateFeature.Msg) -> Unit) {
        this.listener = { msg -> callerThreadExecutor.execute { listener(msg) } }
    }

    override fun handleEffect(eff: TranslateFeature.Eff) {
        translateApiFuture?.cancel(true)
        translateApiFuture = when (eff) {
            is TranslateFeature.Eff.TranslateText -> eff.text.nullIfBlank()?.let {
                effectsExecutorService.submit {
                    Thread.sleep(300)
                    val result = BASE_URL.httpPost()
                        .authentication()
                        .basic(username = "apikey", password = BuildConfig.API_KEY)
                        .jsonBody(
                            json.stringify(
                                TranslationApiRequest.serializer(),
                                TranslationApiRequest(
                                    eff.languageCodeFrom,
                                    eff.languageCodeTo,
                                    listOf(eff.text)
                                )
                            )
                        )
                        .responseString().let { (first, second, result) ->
                            result.map { value ->
                                json.fromJson(
                                    TranslationApiResult.serializer(),
                                    json.parseJson(value)
                                )
                            }
                        }
                    val msg = result.fold(success = { res ->
                        TranslateFeature.Msg.OnTranslationResult(
                            res.translations.first().translation,
                            detectedLanguage = res.detectedLanguage?.let { code ->
                                Language.values().find { it.code == code }
                            }
                        )
                    }, failure = { fail ->
                        TranslateFeature.Msg.OnTranslationError(
                            ApiError(
                                fail.response.statusCode,
                                fail.message
                            )
                        )
                    })
                    callerThreadExecutor.execute { listener?.invoke(msg) }
                }
            }
        }
    }

    override fun cancel() {
        effectsExecutorService.shutdownNow()
    }
}
